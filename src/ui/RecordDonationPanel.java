package ui;

import logic.BloodStockDAO;
import logic.DonorDAO;
import model.Donor;

import javax.swing.*;
import java.awt.*;

public class RecordDonationPanel extends JPanel {

    // Search Component
    private JTextField txtSearchPhone;
    private JButton btnSearch;

    // Form Components
    private JTextField txtName, txtAge, txtPhoneReg, txtMedical;
    private JComboBox<String> cbBloodGroup;
    private JButton btnAction;
    private JButton btnEmergencyTrace;

    // State Tracker (-1 means "New Donor", any other number is an Existing Donor's ID)
    private int currentDonorId = -1;

    public RecordDonationPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ==========================================
        // 1. TOP: The "Search First" Bar
        // ==========================================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        searchPanel.setBackground(new Color(236, 240, 241)); // Light gray
        searchPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Check if Donor is already registered"));

        searchPanel.add(new JLabel("Enter Donor Phone Number:"));
        txtSearchPhone = new JTextField(15);
        txtSearchPhone.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(txtSearchPhone);

        btnSearch = new JButton("Search Database");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);
        

        // ==========================================
        // 2. CENTER: The Dynamic Form
        // ==========================================
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Step 2: Donor Details"),
                BorderFactory.createEmptyBorder(10, 50, 10, 50)
        ));

        formPanel.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Age:"));
        txtAge = new JTextField();
        formPanel.add(txtAge);

        formPanel.add(new JLabel("Blood Group:"));
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        cbBloodGroup = new JComboBox<>(groups);
        formPanel.add(cbBloodGroup);

        formPanel.add(new JLabel("Phone Number:"));
        txtPhoneReg = new JTextField();
        formPanel.add(txtPhoneReg);

        formPanel.add(new JLabel("Medical Issues (if any):"));
        txtMedical = new JTextField("None");
        formPanel.add(txtMedical);


        add(formPanel, BorderLayout.CENTER);

        // ==========================================
        // 3. BOTTOM: The Smart Action Button
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        
        bottomPanel.setPreferredSize(new Dimension(0, 120));

        btnAction = new JButton("Awaiting Search...");
        btnAction.setPreferredSize(new Dimension(350, 45));
        btnAction.setFont(new Font("Arial", Font.BOLD, 16));
        btnAction.setEnabled(false);
        bottomPanel.add(btnAction);
        
        btnEmergencyTrace = new JButton("EMERGENCY: Flag Infection & Trace");
        btnEmergencyTrace.setBackground(new Color(192, 57, 43)); // Danger Red
        btnEmergencyTrace.setForeground(Color.WHITE);
        btnEmergencyTrace.setFont(new Font("Arial", Font.BOLD, 14));
        btnEmergencyTrace.setVisible(false); // Hide it by default!
        bottomPanel.add(btnEmergencyTrace);

        add(bottomPanel, BorderLayout.SOUTH);
        
        toggleFormState(false, false);

        // ==========================================
        // 4. THE SMART LOGIC (Button Actions)
        // ==========================================

        // --- SEARCH BUTTON LOGIC ---
        btnSearch.addActionListener(e -> {
            String phone = txtSearchPhone.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a phone number to search.");
                return;
            }

            Donor foundDonor = DonorDAO.getDonorByPhone(phone);

            if (foundDonor != null) {
                // PATH A: RETURNING DONOR
                currentDonorId = foundDonor.getDonorId();
                
                // Auto-fill the form to show the Admin who they found
                txtName.setText(foundDonor.getName());
                txtAge.setText(String.valueOf(foundDonor.getAge()));
                cbBloodGroup.setSelectedItem(foundDonor.getBloodGroup());
                txtPhoneReg.setText(foundDonor.getPhone());
                txtMedical.setText(foundDonor.getMedicalIssue());

                // Lock the form so the Admin doesn't accidentally overwrite data
                toggleFormState(true, false);

                // Update the Button
                btnAction.setText("Record Blood Donation for " + foundDonor.getName());
                btnAction.setBackground(new Color(46, 204, 113)); // Green
                btnAction.setForeground(Color.WHITE);
                btnAction.setEnabled(true);
                
                JOptionPane.showMessageDialog(this, "Returning Donor Found! Please verify details and record donation.");

            } else {
                // PATH B: BRAND NEW DONOR
                currentDonorId = -1;
                
                // Clear the form, but copy the phone number over to save them typing it again!
                clearForm();
                txtPhoneReg.setText(phone);
                
                // Unlock the form so the Admin can type the new details
                toggleFormState(true, true);

                // Update the Button
                btnAction.setText("Register New Donor & Record Blood");
                btnAction.setBackground(new Color(230, 126, 34)); // Orange
                btnAction.setForeground(Color.WHITE);
                btnAction.setEnabled(true);
                
                JOptionPane.showMessageDialog(this, "No matching records. This is a New Donor. Please fill out their details.");
            }
        });

        // Action Logic
        btnAction.addActionListener(e -> {
            try {
                if (currentDonorId != -1) {
                    // EXECUTE PATH A: RETURNING DONOR
                    // 1. Grab the (potentially updated) age and medical info
                    int updatedAge = Integer.parseInt(txtAge.getText().trim());
                    String updatedMedical = txtMedical.getText().trim();
                    
                    // 2. Update the human in the database using our new DAO method
                    DonorDAO.updateDonorInfo(currentDonorId, updatedAge, updatedMedical);
                    
                    // 3. Generate the new blood bag
                    boolean success = BloodStockDAO.recordNewDonation(currentDonorId);
                    handleResult(success, "Donor info updated and Blood bag added successfully!");
                    
                } else {
                    // EXECUTE PATH B: BRAND NEW DONOR
                    String name = txtName.getText().trim();
                    int age = Integer.parseInt(txtAge.getText().trim());
                    String group = cbBloodGroup.getSelectedItem().toString();
                    String phone = txtPhoneReg.getText().trim();
                    String medical = txtMedical.getText().trim();

                    Donor newDonor = new Donor(name, age, group, phone, medical);
                    int newGeneratedId = DonorDAO.addDonor(newDonor); 

                    if (newGeneratedId != -1) {
                        // Registration worked! Now record the blood.
                        boolean stockSuccess = BloodStockDAO.recordNewDonation(newGeneratedId);
                        handleResult(stockSuccess, "New Donor Registered AND Blood Bag recorded successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to register donor. Check for duplicate phone numbers.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                // This catch now protects BOTH Path A and Path B!
                JOptionPane.showMessageDialog(this, "Please enter a valid number for Age.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnEmergencyTrace.addActionListener(e -> {
            if (currentDonorId != -1) {
                // 1. Ask the Admin what disease was found
                String disease = JOptionPane.showInputDialog(this, 
                        "CRITICAL ALERT: Enter the diagnosed disease/infection:", 
                        "Emergency Lookback Protocol", JOptionPane.WARNING_MESSAGE);

                if (disease != null && !disease.trim().isEmpty()) {

                    // 2. Ask for double confirmation (this is a destructive action)
                    int confirm = JOptionPane.showConfirmDialog(this, 
                        "Are you sure? This will discard ALL available blood from this donor and pull trace records.", 
                        "Confirm Purge", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // 3. Execute the DAO transaction
                        java.util.List<String> victims = BloodStockDAO.executeEmergencyTrace(currentDonorId, disease);

                        if (victims != null) {
                            // 4. Build the Emergency Report
                            StringBuilder report = new StringBuilder();
                            report.append("PROTOCOL COMPLETE.\n\n");
                            report.append("- Donor marked as Infected.\n");
                            report.append("- All available inventory from Donor discarded.\n\n");
                            report.append("AFFECTED PATIENT TRACE:\n");

                            if (victims.isEmpty()) {
                                report.append("Safe. No patients have received this donor's blood yet.");
                            } else {
                                for (String victim : victims) {
                                    report.append(victim).append("\n");
                                }
                                report.append("\nIMMEDIATELY NOTIFY HOSPITAL STAFF TO CONTACT THESE PATIENTS.");
                            }

                            // 5. Display the report in a large text box
                            JTextArea textArea = new JTextArea(report.toString());
                            textArea.setEditable(false);
                            textArea.setForeground(Color.RED);
                            textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
                            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Emergency Trace Report", JOptionPane.ERROR_MESSAGE);

                            // Reset the screen
                            btnSearch.doClick(); // Refresh the data on screen
                        } else {
                            JOptionPane.showMessageDialog(this, "Database Error during Emergency Protocol.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    // Helper: Locks or unlocks the form fields
    private void toggleFormState(boolean hasBeenSearched, boolean isNewDonor) {
        txtName.setEditable(isNewDonor);
        txtAge.setEditable(true);
        txtPhoneReg.setEditable(isNewDonor);
        txtMedical.setEditable(true);
        cbBloodGroup.setEnabled(isNewDonor);
        
        // Visual cue: Grey out only the locked fields
        Color lockedColor = new Color(245, 245, 245);
        txtName.setBackground(isNewDonor ? Color.WHITE : lockedColor);
        txtPhoneReg.setBackground(isNewDonor ? Color.WHITE : lockedColor);
        
        // Age and Medical stay white so the Admin knows they can type here
        txtAge.setBackground(Color.WHITE);
        txtMedical.setBackground(Color.WHITE);
        
        
        //System.out.println("Emergency Button Should Be Visible: " + (hasBeenSearched && !isNewDonor));
        
        // Show the red button ONLY if they have been searched and are NOT new
        btnEmergencyTrace.setVisible(hasBeenSearched && !isNewDonor);
        
        this.revalidate();
        this.repaint();
    }

    // Helper: Clears the form fields
    private void clearForm() {
        txtName.setText("");
        txtAge.setText("");
        txtPhoneReg.setText("");
        txtMedical.setText("None");
        cbBloodGroup.setSelectedIndex(0);
    }

    // Helper: Handles the final success message and resets the screen
    private void handleResult(boolean success, String successMessage) {
        if (success) {
            JOptionPane.showMessageDialog(this, successMessage);
            // Reset everything for the next person in line
            txtSearchPhone.setText("");
            clearForm();
            toggleFormState(false, false);
            btnAction.setText("Awaiting Search...");
            btnAction.setEnabled(false);
            btnAction.setBackground(null);
            currentDonorId = -1;
        } else {
            JOptionPane.showMessageDialog(this, "Error processing the transaction.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}