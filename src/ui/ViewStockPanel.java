package ui;

import logic.BloodStockDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class ViewStockPanel extends JPanel {

    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;

    public ViewStockPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ==========================================
        // 1. TOP: Title & Refresh Button
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Blood Stock Inventory Report");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        topPanel.add(lblTitle, BorderLayout.WEST);

        btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        topPanel.add(btnRefresh, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER: The Data Table
        // ==========================================
        String[] columnNames = {"Blood Group", "Bags Available", "Inventory Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent Admin from accidentally typing in the table!
            }
        };

        stockTable = new JTable(tableModel);
        stockTable.setRowHeight(30);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 14));
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        stockTable.getTableHeader().setBackground(new Color(236, 240, 241));

        JScrollPane scrollPane = new JScrollPane(stockTable);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // 3. ACTION LOGIC
        // ==========================================
        btnRefresh.addActionListener(e -> loadInventoryData());

        // Load data immediately when the panel is created
        loadInventoryData(); 
    }

    // Helper method to fetch data and populate the table
    private void loadInventoryData() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Fetch fresh data from our new DAO method
        Map<String, Integer> inventory = BloodStockDAO.getInventorySummary();

        // Populate the table
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String group = entry.getKey();
            int count = entry.getValue();
            
            // The Smart Logic: Add a warning tag if stock is low
            String status;
            if (count == 0) {
                status = "CRITICAL: OUT OF STOCK";
            } else if (count < 3) {
                status = "LOW STOCK WARNING";
            } else {
                status = "Healthy";
            }

            // Add the row to the table
            tableModel.addRow(new Object[]{group, count, status});
        }
    }
}