package ui;

import logic.BloodStockDAO;
import model.BloodStock;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BloodStockPanel extends JPanel {

    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;

    public BloodStockPanel() {
       
    	setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Title
        JLabel lblTitle = new JLabel("Available Blood Stock Inventory");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        // Table
        // Define the column headers for our table
        String[] columns = {"Stock ID", "Blood Group", "Donation Date", "Expiry Date", "Donor ID", "Status"};
        tableModel = new DefaultTableModel(columns, 0); // 0 means start with zero rows
        stockTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(stockTable);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh Button
        btnRefresh = new JButton("Refresh Inventory");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadTableData());

        // Load the data immediately when the panel is first created
        loadTableData();
    }

    // Helper method to fetch data from DAO and put it in the table
    private void loadTableData() {
        // Clear out any old rows first so we don't duplicate data
        tableModel.setRowCount(0);

        List<BloodStock> stockList = BloodStockDAO.getAvailableStock();

        // Loop through the basket and add each bag as a row
        for (BloodStock bag : stockList) {
            Object[] rowData = {
                bag.getStockId(),
                bag.getBloodGroup(),
                bag.getDonationDate(),
                bag.getExpirationDate(),
                bag.getDonor(),
                bag.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }
}