package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class SalesFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> bikeCombo, customerCombo, paymentCombo;
    private JTextField priceField;
    private Map<String, Integer> bikeMap = new LinkedHashMap<>();
    private Map<String, Integer> customerMap = new LinkedHashMap<>();

    public SalesFrame() {
        setTitle("Sales / Billing");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(new String[]{"Sale ID", "Bike", "Customer", "Date", "Price", "Payment"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        bikeCombo = new JComboBox<>();
        customerCombo = new JComboBox<>();
        priceField = new JTextField();
        paymentCombo = new JComboBox<>(new String[]{"Cash", "Card", "UPI", "Finance/EMI"});

        form.add(new JLabel("Select Bike:")); form.add(bikeCombo);
        form.add(new JLabel("Select Customer:")); form.add(customerCombo);
        form.add(new JLabel("Sale Price:")); form.add(priceField);
        form.add(new JLabel("Payment Mode:")); form.add(paymentCombo);
        add(form, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        JButton recordBtn = new JButton("Record Sale");
        JButton refreshBtn = new JButton("Refresh");
        btnPanel.add(recordBtn);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        recordBtn.addActionListener(e -> recordSale());
        refreshBtn.addActionListener(e -> { loadCombos(); loadSales(); });

        bikeCombo.addActionListener(e -> autoFillPrice());

        loadCombos();
        loadSales();
    }

    private void loadCombos() {
        bikeMap.clear();
        customerMap.clear();
        bikeCombo.removeAllItems();
        customerCombo.removeAllItems();

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT bike_id, model_name, price, quantity_in_stock FROM bikes WHERE quantity_in_stock > 0");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = rs.getString("model_name") + " (Rs." + rs.getBigDecimal("price") + ", stock:" + rs.getInt("quantity_in_stock") + ")";
                bikeMap.put(label, rs.getInt("bike_id"));
                bikeCombo.addItem(label);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bikes: " + ex.getMessage());
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT customer_id, full_name, phone FROM customers");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = rs.getString("full_name") + " (" + rs.getString("phone") + ")";
                customerMap.put(label, rs.getInt("customer_id"));
                customerCombo.addItem(label);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
        }
    }

    private void autoFillPrice() {
        String selected = (String) bikeCombo.getSelectedItem();
        if (selected == null) return;
        Integer bikeId = bikeMap.get(selected);
        if (bikeId == null) return;
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement("SELECT price FROM bikes WHERE bike_id=?")) {
            ps.setInt(1, bikeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) priceField.setText(rs.getBigDecimal("price").toString());
        } catch (Exception ignored) {
        }
    }

    private void recordSale() {
        String bikeSel = (String) bikeCombo.getSelectedItem();
        String custSel = (String) customerCombo.getSelectedItem();
        if (bikeSel == null || custSel == null) {
            JOptionPane.showMessageDialog(this, "No bikes in stock or no customers registered.");
            return;
        }
        Integer bikeId = bikeMap.get(bikeSel);
        Integer custId = customerMap.get(custSel);
        String priceText = priceField.getText().trim();
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter sale price.");
            return;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try {
            conn.setAutoCommit(false);

            String insertSql = "INSERT INTO sales (bike_id, customer_id, sale_date, sale_price, payment_mode) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, bikeId);
                ps.setInt(2, custId);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                ps.setBigDecimal(4, new java.math.BigDecimal(priceText));
                ps.setString(5, (String) paymentCombo.getSelectedItem());
                ps.executeUpdate();
            }

            String updateStockSql = "UPDATE bikes SET quantity_in_stock = quantity_in_stock - 1 WHERE bike_id=? AND quantity_in_stock > 0";
            try (PreparedStatement ps = conn.prepareStatement(updateStockSql)) {
                ps.setInt(1, bikeId);
                ps.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Sale recorded successfully.");
            priceField.setText("");
            loadCombos();
            loadSales();
        } catch (Exception ex) {
            try { conn.rollback(); } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(this, "Error recording sale: " + ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    private void loadSales() {
        model.setRowCount(0);
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "SELECT s.sale_id, b.model_name, c.full_name, s.sale_date, s.sale_price, s.payment_mode " +
                "FROM sales s LEFT JOIN bikes b ON s.bike_id=b.bike_id " +
                "LEFT JOIN customers c ON s.customer_id=c.customer_id ORDER BY s.sale_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("sale_id"), rs.getString("model_name"), rs.getString("full_name"),
                        rs.getDate("sale_date"), rs.getBigDecimal("sale_price"), rs.getString("payment_mode")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading sales: " + ex.getMessage());
        }
    }
}
