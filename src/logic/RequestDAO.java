package logic;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RequestDAO {

    // Method to issue blood and generate a receipt simultaneously
    public static boolean issueBlood(int stockId, String patientName, String bloodGroup) {
        
        String updateStockQuery = "UPDATE bloodstock SET status = 'Issued' WHERE stock_id = ?";
        String insertRequestQuery = "INSERT INTO request (blood_group, patient_name, request_date, issued_stock_id) VALUES (?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            
            // Start Transaction
            conn.setAutoCommit(false); 

            // Execute first query
            try (PreparedStatement pstmt1 = conn.prepareStatement(updateStockQuery)) {
                pstmt1.setInt(1, stockId);
                int stockUpdated = pstmt1.executeUpdate();
                
                if (stockUpdated == 0) {
                    conn.rollback(); // Undo! Bag wasn't found.
                    return false;
                }
            }

            // Execute second query
            try (PreparedStatement pstmt2 = conn.prepareStatement(insertRequestQuery)) {
                pstmt2.setString(1, bloodGroup);
                pstmt2.setString(2, patientName);
                
                java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
                pstmt2.setDate(3, today);
                
                pstmt2.setInt(4, stockId);
                
                pstmt2.executeUpdate();
            }

            // Commit Transaction
            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // If anything crashes, undo the whole transaction
                if (conn != null) conn.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                // Always turn auto-commit back on when finished
                if (conn != null) conn.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}