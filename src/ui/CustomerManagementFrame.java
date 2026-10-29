package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerManagementFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, phoneField, emailField, addressField;
    private int selectedCustomerId = -1;

    public CustomerManagementFrame() {
        setTitle("Manage Customers");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(new String[]{"ID", "Full Name", "Phone", "Email", "Address"}, 0);
        table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();

        form.add(new JLabel("Full Name:")); form.add(nameField);
        form.add(new JLabel("Phone:")); form.add(phoneField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Address:")); form.add(addressField);
        add(form, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn); btnPanel.add(clearBtn);
        add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        clearBtn.addActionListener(e -> clearForm());

        loadCustomers();
    }

    private void loadCustomers() {
        model.setRowCount(0);
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("customer_id"), rs.getString("full_name"),
                        rs.getString("phone"), rs.getString("email"), rs.getString("address")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
        }
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedCustomerId = (int) model.getValueAt(row, 0);
        nameField.setText(model.getValueAt(row, 1).toString());
        phoneField.setText(model.getValueAt(row, 2) == null ? "" : model.getValueAt(row, 2).toString());
        emailField.setText(model.getValueAt(row, 3) == null ? "" : model.getValueAt(row, 3).toString());
        addressField.setText(model.getValueAt(row, 4) == null ? "" : model.getValueAt(row, 4).toString());
    }

    private void addCustomer() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "INSERT INTO customers (full_name, phone, email, address) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindForm(ps);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer added.");
            clearForm();
            loadCustomers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding customer: " + ex.getMessage());
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer first.");
            return;
        }
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "UPDATE customers SET full_name=?, phone=?, email=?, address=? WHERE customer_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindForm(ps);
            ps.setInt(5, selectedCustomerId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer updated.");
            clearForm();
            loadCustomers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating customer: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        String sql = "DELETE FROM customers WHERE customer_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selectedCustomerId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer deleted.");
            clearForm();
            loadCustomers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage());
        }
    }

    private void bindForm(PreparedStatement ps) throws Exception {
        ps.setString(1, nameField.getText().trim());
        ps.setString(2, phoneField.getText().trim());
        ps.setString(3, emailField.getText().trim());
        ps.setString(4, addressField.getText().trim());
    }

    private void clearForm() {
        selectedCustomerId = -1;
        nameField.setText(""); phoneField.setText(""); emailField.setText(""); addressField.setText("");
        table.clearSelection();
    }
}
