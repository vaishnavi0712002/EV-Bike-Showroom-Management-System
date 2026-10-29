package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("EV Bike Showroom - Login");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("EV Bike Showroom", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        JLabel hint = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        gbc.gridy = 4;
        panel.add(hint, gbc);

        add(panel);

        loginBtn.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dispose();
                new DashboardFrame(username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
