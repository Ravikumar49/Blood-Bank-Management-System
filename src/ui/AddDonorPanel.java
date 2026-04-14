package ui;

import logic.DonorDAO;
import model.Donor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddDonorPanel extends JPanel {
    
    private JTextField txtName, txtAge, txtPhone, txtMedical;
    private JComboBox<String> cbBloodGroup;
    private JButton btnSave;

    public AddDonorPanel() {
        
        setLayout(null);
        setBackground(Color.WHITE);

        // UI Components
        JLabel lblTitle = new JLabel("Register New Donor");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(30, 20, 300, 30);
        add(lblTitle);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setBounds(30, 80, 100, 25);
        add(lblName);
        txtName = new JTextField();
        txtName.setBounds(150, 80, 200, 25);
        add(txtName);

        JLabel lblAge = new JLabel("Age:");
        lblAge.setBounds(30, 120, 100, 25);
        add(lblAge);
        txtAge = new JTextField();
        txtAge.setBounds(150, 120, 200, 25);
        add(txtAge);

        JLabel lblGroup = new JLabel("Blood Group:");
        lblGroup.setBounds(30, 160, 100, 25);
        add(lblGroup);
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        cbBloodGroup = new JComboBox<>(groups);
        cbBloodGroup.setBounds(150, 160, 200, 25);
        add(cbBloodGroup);

        JLabel lblPhone = new JLabel("Phone No:");
        lblPhone.setBounds(30, 200, 100, 25);
        add(lblPhone);
        txtPhone = new JTextField();
        txtPhone.setBounds(150, 200, 200, 25);
        add(txtPhone);

        JLabel lblMedical = new JLabel("Medical Issue:");
        lblMedical.setBounds(30, 240, 100, 25);
        add(lblMedical);
        txtMedical = new JTextField("None"); // Default Value
        txtMedical.setBounds(150, 240, 200, 25);
        add(txtMedical);

        btnSave = new JButton("Save Donor");
        btnSave.setBounds(150, 290, 120, 30);
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        add(btnSave);

        
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText();
                int age = Integer.parseInt(txtAge.getText()); 
                String group = cbBloodGroup.getSelectedItem().toString();
                String phone = txtPhone.getText();
                String medical = txtMedical.getText();

                // Pack data into the Model
                Donor newDonor = new Donor(name, age, group, phone, medical);
                
                int newDonorId = DonorDAO.addDonor(newDonor);

                if (newDonorId != -1) {
                    int choice = JOptionPane.showConfirmDialog(this, 
                        "Donor Registered Successfully!\nWould you like to record a blood donation for them right now?",
                        "Record Donation?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        
                    if (choice == JOptionPane.YES_OPTION) {
                        boolean stockSuccess = logic.BloodStockDAO.recordNewDonation(newDonorId);
                        if (stockSuccess) {
                            JOptionPane.showMessageDialog(this, "Blood bag added to inventory and expiration date set.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Error generating blood bag.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    
                    // clearFields(); // Reset the form for the next person
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to register donor. Check age (18-65) or duplicate phone number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for Age.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void clearFields() {
        txtName.setText("");
        txtAge.setText("");
        cbBloodGroup.setSelectedIndex(0);
        txtPhone.setText("");
        txtMedical.setText("None");
    }
}