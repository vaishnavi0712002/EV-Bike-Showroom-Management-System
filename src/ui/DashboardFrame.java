package ui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame(String username) {
        setTitle("EV Bike Showroom - Dashboard (" + username + ")");
        setSize(500, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel welcome = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton bikeBtn    = new JButton("Manage Bikes");
        JButton customerBtn = new JButton("Manage Customers");
        JButton salesBtn    = new JButton("Sales / Billing");
        JButton testRideBtn = new JButton("Test Ride Booking");
        JButton logoutBtn   = new JButton("Logout");

        bikeBtn.addActionListener(e -> new BikeManagementFrame().setVisible(true));
        customerBtn.addActionListener(e -> new CustomerManagementFrame().setVisible(true));
        salesBtn.addActionListener(e -> new SalesFrame().setVisible(true));
        testRideBtn.addActionListener(e -> new TestRideFrame().setVisible(true));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        panel.add(welcome);
        panel.add(bikeBtn);
        panel.add(customerBtn);
        panel.add(salesBtn);
        panel.add(testRideBtn);
        panel.add(logoutBtn);

        add(panel);
    }
}
