package ui;

import logic.BloodStockDAO;
import logic.DonorDAO;
import model.Donor;

import javax.swing.*;
import java.awt.*;

public class AddStockPanel extends JPanel {

    private JTextField txtPhone;
    private JButton btnSearch, btnRecord;
    private JLabel lblNameResult, lblGroupResult;
    
    // We store the ID here temporarily once the search finds a donor
    private int currentDonorId = -1; 

    public AddStockPanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ==========================================
        // 1. TOP PANEL: The Search Bar
        // ==========================================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Find Registered Donor"));

        searchPanel.add(new JLabel("Donor Phone Number:"));
        txtPhone = new JTextField(15);
        searchPanel.add(txtPhone);

        btnSearch = new JButton("Search Donor");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER PANEL: Donor Details Display
        // ==========================================
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        detailsPanel.setBackground(new Color(245, 246, 250)); // Light grey background
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Step 2: Verify Donor Details"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        lblNameResult = new JLabel("Donor Name: (Awaiting Search...)");
        lblNameResult.setFont(new Font("Arial", Font.BOLD, 16));
        
        lblGroupResult = new JLabel("Blood Group: --");
        lblGroupResult.setFont(new Font("Arial", Font.BOLD, 16));
        lblGroupResult.setForeground(new Color(192, 57, 43)); // Dark red text

        detailsPanel.add(lblNameResult);
        detailsPanel.add(lblGroupResult);

        add(detailsPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. BOTTOM PANEL: Record Button
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);

        btnRecord = new JButton("Record Blood Donation");
        btnRecord.setBackground(new Color(46, 204, 113));
        btnRecord.setForeground(Color.WHITE);
        btnRecord.setFont(new Font("Arial", Font.BOLD, 14));
        btnRecord.setPreferredSize(new Dimension(220, 40));
        
        // Disable this button until a donor is actually found!
        btnRecord.setEnabled(false); 
        bottomPanel.add(btnRecord);

        add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // 4. BUTTON ACTIONS
        // ==========================================

        // Action 1: Search Button
        btnSearch.addActionListener(e -> {
            String phone = txtPhone.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a phone number.");
                return;
            }

            // Call the DAO method we wrote earlier!
            Donor foundDonor = DonorDAO.getDonorByPhone(phone);

            if (foundDonor != null) {
                // Success! Update UI and store the ID
                currentDonorId = foundDonor.getDonorId();
                lblNameResult.setText("Donor Name: " + foundDonor.getName());
                lblGroupResult.setText("Blood Group: " + foundDonor.getBloodGroup());
                
                btnRecord.setEnabled(true); // Unlock the final step
                JOptionPane.showMessageDialog(this, "Donor Found! You can now record the donation.");
            } else {
                // Fail cleanly
                currentDonorId = -1;
                lblNameResult.setText("Donor Name: Not Found");
                lblGroupResult.setText("Blood Group: --");
                btnRecord.setEnabled(false);
                JOptionPane.showMessageDialog(this, "No donor found with this phone number. Please register them first.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Action 2: Record Donation Button
        btnRecord.addActionListener(e -> {
            if (currentDonorId != -1) {
                // Call the generator method!
                boolean success = BloodStockDAO.recordNewDonation(currentDonorId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Success! Blood bag added to inventory. Expiration date auto-calculated.");
                    resetUI(); // Clear the screen for the next person
                } else {
                    JOptionPane.showMessageDialog(this, "Database Error: Could not record donation.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Helper to clear the form after a successful transaction
    private void resetUI() {
        txtPhone.setText("");
        lblNameResult.setText("Donor Name: (Awaiting Search...)");
        lblGroupResult.setText("Blood Group: --");
        currentDonorId = -1;
        btnRecord.setEnabled(false);
    }
}