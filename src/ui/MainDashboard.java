package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainDashboard extends JFrame {
    
    private JPanel contentPanel; 
    private CardLayout cardLayout;
    private JButton btnRecordDonation;
    private JButton btnViewStock;

    public MainDashboard(String role) {
    	// Start the background expiry sweep
        util.ExpiryTracker.startTracker();
        
        
        setTitle("Blood Bank Management System - " + role + " Dashboard");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        setLayout(new BorderLayout());

        // SideBar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(6, 1, 10, 10));
        sidebar.setBackground(new Color(45, 52, 54));
        sidebar.setPreferredSize(new Dimension(200, 600));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton btnHome = createMenuButton("Dashboard Home");
        sidebar.add(btnHome);
        if(role.equals("ADMIN")) {
        	//JButton btnAddDonor = createMenuButton("Register Donor");
        	//JButton btnViewStock = createMenuButton("Manage Stock");
        	
        	//sidebar.add(btnAddDonor);
        	//sidebar.add(btnViewStock);
        	
        	//btnAddDonor.addActionListener(e -> cardLayout.show(contentPanel, "DONOR"));
        	//btnViewStock.addActionListener(e -> cardLayout.show(contentPanel, "STOCK"));
        	
            btnRecordDonation = new JButton("Record Blood Donation");
            btnRecordDonation.setBackground(new Color(52, 152, 219));
            btnRecordDonation.setForeground(Color.WHITE);
            btnRecordDonation.setFocusPainted(false);
            btnViewStock = new JButton("View Inventory Report");
            btnViewStock.setBackground(new Color(52, 152, 219));
            btnViewStock.setForeground(Color.WHITE);
            btnViewStock.setFocusPainted(false);
            sidebar.add(btnViewStock);
            sidebar.add(btnRecordDonation);
            
            btnRecordDonation.addActionListener(e -> cardLayout.show(contentPanel, "RECORD_DONATION"));
            btnViewStock.addActionListener(e -> cardLayout.show(contentPanel, "VIEW_STOCK"));
        }
        else if(role.equals("STAFF")) {
        	JButton btnIssueBlood = createMenuButton("Issue Blood");
        	
        	sidebar.add(btnIssueBlood);
        	
        	btnIssueBlood.addActionListener(e -> cardLayout.show(contentPanel, "ISSUE"));
        }
        JButton btnLogout = createMenuButton("Logout");
        
        sidebar.add(new JLabel("")); 
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);
        // Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel homePanel = createDummyPanel("Welcome to the Blood Bank System!", Color.LIGHT_GRAY);
        //JPanel donorPanel = new AddDonorPanel();
        JPanel issuePanel = new IssueBloodPanel();
        //JPanel stockPanel = new BloodStockPanel();
        //JPanel addStockPanel = new AddStockPanel();
        JPanel recordDonationPanel = new RecordDonationPanel();
        JPanel viewStockPanel = new ViewStockPanel();

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(viewStockPanel, "VIEW_STOCK");
        //contentPanel.add(donorPanel, "DONOR");
        //contentPanel.add(stockPanel, "STOCK");
        contentPanel.add(recordDonationPanel, "RECORD_DONATION");
        contentPanel.add(issuePanel, "ISSUE");
        //contentPanel.add(addStockPanel, "STOCK");

        add(contentPanel, BorderLayout.CENTER);
        
        
        btnHome.addActionListener(e -> cardLayout.show(contentPanel, "HOME"));
        
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close dashboard
                new LoginScreen().setVisible(true); // Re-open login screen
            }
        });
    }

    // Helper method to make our menu buttons look nice and uniform
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        return btn;
    }

    // Helper method to create placeholder panels so you can test the UI immediately
    private JPanel createDummyPanel(String text, Color bgColor) {
        JPanel p = new JPanel(new GridBagLayout()); // GridBag centers text perfectly
        p.setBackground(bgColor);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.ITALIC, 24));
        p.add(label);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboard("ADMIN").setVisible(true));
    }
}