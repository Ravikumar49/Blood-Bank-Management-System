package logic;

import model.Donor;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DonorDAO {
	
	// Method to search for a registered donor using their unique phone number
    public static Donor getDonorByPhone(String phone) {
        String query = "SELECT * FROM donors WHERE phone = ?";
        
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setString(1, phone);
            java.sql.ResultSet rs = pstmt.executeQuery();
            
            // If a row comes back, pack it into our Java Object!
            if (rs.next()) {
                return new Donor(
                    rs.getInt("donor_id"), 
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("blood_group"),
                    rs.getString("phone"),
                    rs.getString("medical_issue")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // If we get here, the phone number wasn't in the database
        return null; 
    }

    public static int addDonor(Donor donor) {
        String query = "INSERT INTO donors (name, age, blood_group, phone,  medical_issue, last_donation_date) VALUES (?, ?, ?, ?, ?, CURDATE())";
        
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             // RETURN_GENERATED_KEYS tells MySQL to give the ID back!
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, donor.getName());
            pstmt.setInt(2, donor.getAge());
            pstmt.setString(3, donor.getBloodGroup());
            pstmt.setString(4, donor.getPhone());
            pstmt.setString(5, donor.getMedicalIssue());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Grab the ID that MySQL just auto-incremented
                try (java.sql.ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Return the new ID!
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the insert failed (e.g., duplicate phone)
    }
    // Method to update a returning donor's changing information
    public static boolean updateDonorInfo(int donorId, int newAge, String newMedical) {
        String query = "UPDATE donors SET age = ?, medical_issue = ?, last_donation_date = CURDATE() WHERE donor_id = ?";
        
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, newAge);
            pstmt.setString(2, newMedical);
            pstmt.setInt(3, donorId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}