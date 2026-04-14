package ui;

import logic.BloodStockDAO;
import logic.RequestDAO;
import model.BloodStock;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IssueBloodPanel extends JPanel {

    private JTextField txtPatientName;
    private JComboBox<String> cbRequestedGroup;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton btnSearch, btnIssue;

    public IssueBloodPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search Form

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Patient Requirements"));
        topPanel.setPreferredSize(new Dimension(0, 130));

        topPanel.add(new JLabel("Patient Name:"));
        txtPatientName = new JTextField(15);
        topPanel.add(txtPatientName);

        topPanel.add(new JLabel("Needed Blood:"));
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        cbRequestedGroup = new JComboBox<>(groups);
        topPanel.add(cbRequestedGroup);

        btnSearch = new JButton("Find Compatible Blood");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(200, 40)); 

        topPanel.add(btnSearch);

        add(topPanel, BorderLayout.NORTH);

        // Results Table
        
        String[] columns = {"Stock ID", "Blood Group", "Donation Date", "Expiry Date"};
        tableModel = new DefaultTableModel(columns, 0);
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Can only select one bag at a time
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select a Bag to Issue"));
        add(scrollPane, BorderLayout.CENTER);

        // Issue Button
 
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        
        btnIssue = new JButton("Issue Selected Bag");
        btnIssue.setBackground(new Color(46, 204, 113));
        btnIssue.setForeground(Color.WHITE);
        btnIssue.setEnabled(false); // Disabled until they search and select a bag
        bottomPanel.add(btnIssue);

        add(bottomPanel, BorderLayout.SOUTH);

        // Search Button
        
        btnSearch.addActionListener(e -> {
            String patientName = txtPatientName.getText().trim();
            if (patientName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the Patient's Name first!");
                return;
            }
            
            String neededGroup = cbRequestedGroup.getSelectedItem().toString();
            loadCompatibleStock(neededGroup);
            btnIssue.setEnabled(true); // Turn on the Issue button
        });

        // Issue Button
        
        btnIssue.addActionListener(e -> {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please click on a bag in the table to select it.");
                return;
            }

            // Grab the Stock ID from the hidden first column of the selected row
            int stockId = (int) tableModel.getValueAt(selectedRow, 0);
            String bagGroup = (String) tableModel.getValueAt(selectedRow, 1);
            String patientName = txtPatientName.getText().trim();
            String requestedGroup = cbRequestedGroup.getSelectedItem().toString();

            // Confirm with the user
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to issue Bag #" + stockId + " (" + bagGroup + ") to " + patientName + "?",
                "Confirm Issue", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Run The Transaction
                boolean success = RequestDAO.issueBlood(stockId, patientName, requestedGroup);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Success! Blood Bag Issued and Receipt Generated.");
                    // Refresh the table so the issued bag disappears
                    loadCompatibleStock(requestedGroup); 
                } else {
                    JOptionPane.showMessageDialog(this, "Transaction Failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Helper method to populate the table
    private void loadCompatibleStock(String group) {
        tableModel.setRowCount(0); // Clear old results
        List<BloodStock> stockList = BloodStockDAO.searchCompatibleStock(group);

        for (BloodStock bag : stockList) {
            Object[] rowData = {
                bag.getStockId(),
                bag.getBloodGroup(),
                bag.getDonationDate(),
                bag.getExpirationDate()
            };
            tableModel.addRow(rowData);
        }
        
        if (stockList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "URGENT: No compatible blood available for " + group + "!");
        }
    }
}