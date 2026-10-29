package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BikeManagementFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField modelField, brandField, batteryField, rangeField, speedField,
            priceField, colorField, stockField, chargeField, motorField;
    private int selectedBikeId = -1;

    public BikeManagementFrame() {
        setTitle("Manage Bikes");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // ---- Table ----
        model = new DefaultTableModel(new String[]{
                "ID", "Model", "Brand", "Battery", "Range(km)", "TopSpeed", "Price", "Color", "Stock", "Charge Time", "Motor"
        }, 0);
        table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- Form ----
        JPanel form = new JPanel(new GridLayout(5, 4, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelField = new JTextField();
        brandField = new JTextField();
        batteryField = new JTextField();
        rangeField = new JTextField();
        speedField = new JTextField();
        priceField = new JTextField();
        colorField = new JTextField();
        stockField = new JTextField();
        chargeField = new JTextField();
        motorField = new JTextField();

        form.add(new JLabel("Model Name:")); form.add(modelField);
        form.add(new JLabel("Brand:")); form.add(brandField);
        form.add(new JLabel("Battery Capacity:")); form.add(batteryField);
        form.add(new JLabel("Range (km):")); form.add(rangeField);
        form.add(new JLabel("Top Speed:")); form.add(speedField);
        form.add(new JLabel("Price:")); form.add(priceField);
        form.add(new JLabel("Color:")); form.add(colorField);
        form.add(new JLabel("Stock Qty:")); form.add(stockField);
        form.add(new JLabel("Charging Time:")); form.add(chargeField);
        form.add(new JLabel("Motor Power:")); form.add(motorField);

        add(form, BorderLayout.NORTH);

        // ---- Buttons ----
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);
        add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addBike());
        updateBtn.addActionListener(e -> updateBike());
        deleteBtn.addActionListener(e -> deleteBike());
        clearBtn.addActionListener(e -> clearForm());

        loadBikes();
    }

    private void loadBikes() {
        model.setRowCount(0);
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "SELECT * FROM bikes ORDER BY bike_id";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bike_id"), rs.getString("model_name"), rs.getString("brand"),
                        rs.getString("battery_capacity"), rs.getInt("range_km"), rs.getInt("top_speed"),
                        rs.getBigDecimal("price"), rs.getString("color"), rs.getInt("quantity_in_stock"),
                        rs.getString("charging_time"), rs.getString("motor_power")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bikes: " + ex.getMessage());
        }
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedBikeId = (int) model.getValueAt(row, 0);
        modelField.setText(model.getValueAt(row, 1).toString());
        brandField.setText(model.getValueAt(row, 2).toString());
        batteryField.setText(model.getValueAt(row, 3).toString());
        rangeField.setText(model.getValueAt(row, 4).toString());
        speedField.setText(model.getValueAt(row, 5).toString());
        priceField.setText(model.getValueAt(row, 6).toString());
        colorField.setText(model.getValueAt(row, 7).toString());
        stockField.setText(model.getValueAt(row, 8).toString());
        chargeField.setText(model.getValueAt(row, 9).toString());
        motorField.setText(model.getValueAt(row, 10).toString());
    }

    private void addBike() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "INSERT INTO bikes (model_name, brand, battery_capacity, range_km, top_speed, price, color, quantity_in_stock, charging_time, motor_power) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindForm(ps);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bike added successfully.");
            clearForm();
            loadBikes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding bike: " + ex.getMessage());
        }
    }

    private void updateBike() {
        if (selectedBikeId == -1) {
            JOptionPane.showMessageDialog(this, "Select a bike from the table first.");
            return;
        }
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "UPDATE bikes SET model_name=?, brand=?, battery_capacity=?, range_km=?, top_speed=?, price=?, color=?, quantity_in_stock=?, charging_time=?, motor_power=? WHERE bike_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindForm(ps);
            ps.setInt(11, selectedBikeId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bike updated successfully.");
            clearForm();
            loadBikes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating bike: " + ex.getMessage());
        }
    }

    private void deleteBike() {
        if (selectedBikeId == -1) {
            JOptionPane.showMessageDialog(this, "Select a bike from the table first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected bike?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "DELETE FROM bikes WHERE bike_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selectedBikeId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Bike deleted.");
            clearForm();
            loadBikes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting bike: " + ex.getMessage());
        }
    }

    private void bindForm(PreparedStatement ps) throws Exception {
        ps.setString(1, modelField.getText().trim());
        ps.setString(2, brandField.getText().trim());
        ps.setString(3, batteryField.getText().trim());
        ps.setInt(4, parseIntSafe(rangeField.getText()));
        ps.setInt(5, parseIntSafe(speedField.getText()));
        ps.setBigDecimal(6, new java.math.BigDecimal(priceField.getText().trim()));
        ps.setString(7, colorField.getText().trim());
        ps.setInt(8, parseIntSafe(stockField.getText()));
        ps.setString(9, chargeField.getText().trim());
        ps.setString(10, motorField.getText().trim());
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void clearForm() {
        selectedBikeId = -1;
        modelField.setText(""); brandField.setText(""); batteryField.setText("");
        rangeField.setText(""); speedField.setText(""); priceField.setText("");
        colorField.setText(""); stockField.setText(""); chargeField.setText("");
        motorField.setText("");
        table.clearSelection();
    }
}
