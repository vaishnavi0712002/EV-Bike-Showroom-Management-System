package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestRideFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> bikeCombo, customerCombo, statusCombo;
    private JTextField dateField, timeField;
    private Map<String, Integer> bikeMap = new LinkedHashMap<>();
    private Map<String, Integer> customerMap = new LinkedHashMap<>();

    public TestRideFrame() {
        setTitle("Test Ride Booking");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(new String[]{"Ride ID", "Customer", "Bike", "Date", "Time", "Status"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        customerCombo = new JComboBox<>();
        bikeCombo = new JComboBox<>();
        dateField = new JTextField("YYYY-MM-DD");
        timeField = new JTextField("e.g. 10:30 AM");
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled"});

        form.add(new JLabel("Customer:")); form.add(customerCombo);
        form.add(new JLabel("Bike:")); form.add(bikeCombo);
        form.add(new JLabel("Date:")); form.add(dateField);
        form.add(new JLabel("Time:")); form.add(timeField);
        form.add(new JLabel("Status:")); form.add(statusCombo);
        add(form, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        JButton bookBtn = new JButton("Book Ride");
        JButton refreshBtn = new JButton("Refresh");
        btnPanel.add(bookBtn);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        bookBtn.addActionListener(e -> bookRide());
        refreshBtn.addActionListener(e -> { loadCombos(); loadRides(); });

        loadCombos();
        loadRides();
    }

    private void loadCombos() {
        bikeMap.clear();
        customerMap.clear();
        bikeCombo.removeAllItems();
        customerCombo.removeAllItems();

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement ps = conn.prepareStatement("SELECT bike_id, model_name FROM bikes");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bikeMap.put(rs.getString("model_name"), rs.getInt("bike_id"));
                bikeCombo.addItem(rs.getString("model_name"));
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

    private void bookRide() {
        String custSel = (String) customerCombo.getSelectedItem();
        String bikeSel = (String) bikeCombo.getSelectedItem();
        if (custSel == null || bikeSel == null) {
            JOptionPane.showMessageDialog(this, "No customers or bikes available.");
            return;
        }
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        if (date.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter date and time.");
            return;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "INSERT INTO test_rides (customer_id, bike_id, ride_date, ride_time, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerMap.get(custSel));
            ps.setInt(2, bikeMap.get(bikeSel));
            ps.setDate(3, Date.valueOf(date));
            ps.setString(4, time);
            ps.setString(5, (String) statusCombo.getSelectedItem());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Test ride booked.");
            loadRides();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error booking ride: " + ex.getMessage() +
                    "\n(Date must be in YYYY-MM-DD format.)");
        }
    }

    private void loadRides() {
        model.setRowCount(0);
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "SELECT t.ride_id, c.full_name, b.model_name, t.ride_date, t.ride_time, t.status " +
                "FROM test_rides t LEFT JOIN customers c ON t.customer_id=c.customer_id " +
                "LEFT JOIN bikes b ON t.bike_id=b.bike_id ORDER BY t.ride_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ride_id"), rs.getString("full_name"), rs.getString("model_name"),
                        rs.getDate("ride_date"), rs.getString("ride_time"), rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading rides: " + ex.getMessage());
        }
    }
}
