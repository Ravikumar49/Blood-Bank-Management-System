package logic;

import model.BloodStock;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodStockDAO {
	// Declare the Map at the top
    private static final Map<String, List<String>> COMPATIBILITY_MAP = new HashMap<>();

    // Using Hash Map for O(1) complexity
    static {
        COMPATIBILITY_MAP.put("A+", List.of("A+", "A-", "O+", "O-"));
        COMPATIBILITY_MAP.put("A-", List.of("A-", "O-"));
        COMPATIBILITY_MAP.put("B+", List.of("B+", "B-", "O+", "O-"));
        COMPATIBILITY_MAP.put("B-", List.of("B-", "O-"));
        COMPATIBILITY_MAP.put("AB+", List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        COMPATIBILITY_MAP.put("AB-", List.of("AB-", "A-", "B-", "O-"));
        COMPATIBILITY_MAP.put("O+", List.of("O+", "O-"));
        COMPATIBILITY_MAP.put("O-", List.of("O-"));
    }

    public static List<String> getCompatibleGroups(String patientGroup) {
        // getOrDefault protects you from garbage inputs
        return COMPATIBILITY_MAP.getOrDefault(patientGroup, new ArrayList<>());
    }
    
    // Method to aggregate current inventory for the Admin Dashboard
    public static java.util.Map<String, Integer> getInventorySummary() {
        // Pre-fill the map so every group shows up, even if the count is 0
        java.util.Map<String, Integer> inventory = new java.util.HashMap<>();
        String[] allGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : allGroups) {
            inventory.put(group, 0);
        }

        // We use a JOIN here to ensure we get the right blood group from the donor
        String query = "SELECT d.blood_group, COUNT(b.stock_id) as total_bags " +
                       "FROM bloodstock b " +
                       "JOIN donors d ON b.donor_id = d.donor_id " +
                       "WHERE b.status = 'Available' " +
                       "GROUP BY d.blood_group";

        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query);
             java.sql.ResultSet rs = pstmt.executeQuery()) {

            // Overwrite the 0 with the actual count if they have bags
            while (rs.next()) {
                String group = rs.getString("blood_group");
                int count = rs.getInt("total_bags");
                inventory.put(group, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventory;
    }
    
    // EMERGENCY PROTOCOL: Contact Tracing & Inventory Purge
    public static java.util.List<String> executeEmergencyTrace(int donorId, String diseaseName) {
        java.util.List<String> affectedPatients = new java.util.ArrayList<>();
        java.sql.Connection conn = null;

        try {
            conn = util.DBConnection.getConnection();
            conn.setAutoCommit(false); // Start ACID Transaction!

            // ACTION 1: Flag the Donor's medical record
            String updateDonor = "UPDATE donors SET medical_issue = ? WHERE donor_id = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(updateDonor)) {
                ps.setString(1, "CRITICAL FLAG: " + diseaseName);
                ps.setInt(2, donorId);
                ps.executeUpdate();
            }

            // ACTION 2: Purge all 'Available' bags from the refrigerator
            String discardBags = "UPDATE bloodstock SET status = 'Discarded' WHERE donor_id = ? AND status = 'Available'";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(discardBags)) {
                ps.setInt(1, donorId);
                ps.executeUpdate();
            }

            // ACTION 3: Trace the tainted blood to find patients (using a JOIN)
            // *Note: Verify these column names match your 'requests' table!
            String traceQuery = "SELECT r.patient_name, r.request_date, b.stock_id " +
                                "FROM request r " +
                                "JOIN bloodstock b ON r.issued_stock_id = b.stock_id " +
                                "WHERE b.donor_id = ?";
                                
            try (java.sql.PreparedStatement ps = conn.prepareStatement(traceQuery)) {
                ps.setInt(1, donorId);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String pName = rs.getString("patient_name");
                    // If you don't have an issue_date column in requests, just remove it from the string below
                    String sId = rs.getString("stock_id"); 
                    affectedPatients.add("URGENT: Patient '" + pName + "' received infected Bag #" + sId);
                }
            }

            conn.commit(); // Save all three actions!

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return null; // Return null if it fails
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception ex) {}
        }
        
        return affectedPatients; // Return the list of people to call!
    }
    
    // Method to fetch compatible blood bags for the Issue UI
    public static List<BloodStock> searchCompatibleStock(String patientGroup) {
        List<BloodStock> compatibleBags = new ArrayList<>();
        List<String> safeGroups = getCompatibleGroups(patientGroup);

        // We use a dynamically built SQL query based on how many safe groups there are
        String query = "SELECT * FROM bloodstock WHERE status = 'Available' AND blood_group = ? ORDER BY expiration_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Loop through every safe group
            for (String group : safeGroups) {
                pstmt.setString(1, group);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    BloodStock bag = new BloodStock(
                        rs.getInt("stock_id"), rs.getString("blood_group"),
                        rs.getString("donation_date"), rs.getString("expiration_date"),
                        rs.getInt("donor_id"), rs.getString("status")
                    );
                    compatibleBags.add(bag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compatibleBags;
    }
    
    
	// Method to record a new donation and generate a bag of blood
    public static boolean recordNewDonation(int donorId) {
        String query1 = "SELECT blood_group FROM donors WHERE donor_id = ?";
        String query2 = "INSERT INTO bloodstock (blood_group, donation_date, expiration_date, donor_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(query1)) {
            
            //Check if the donor exists
            pstmt1.setInt(1, donorId);
            ResultSet rs = pstmt1.executeQuery();

            if (rs.next()) {
                // If the donor exists. Extract their blood group.
                String bloodGroup = rs.getString("blood_group");

                // Set the expiration date to 42 Days from today
                java.time.LocalDate today = java.time.LocalDate.now();
                java.sql.Date donationDate = java.sql.Date.valueOf(today);
                java.sql.Date expirationDate = java.sql.Date.valueOf(today.plusDays(42));

                // Create the new Blood Bag
                try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {
                    pstmt2.setString(1, bloodGroup);
                    pstmt2.setDate(2, donationDate);
                    pstmt2.setDate(3, expirationDate);
                    pstmt2.setInt(4, donorId);

                    int rowsAffected = pstmt2.executeUpdate();
                    return rowsAffected > 0; // Returns true if the bag was successfully saved
                }
            } else {
                // If the donor wasn't found
                System.out.println("Validation Failed: Donor ID " + donorId + " does not exist.");
                return false; 
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	public static List<BloodStock> getAvailableStock() {
		List<BloodStock> stockList = new ArrayList<>();
		String query = "SELECT * FROM bloodstock WHERE status = 'AVAILABLE'";
		
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement pstmt = conn.prepareCall(query);
			 ResultSet rs = pstmt.executeQuery()) {
			
			while(rs.next()) {
				
				int stockID = rs.getInt("stock_id");
				String bloodGroup = rs.getString("blood_group");
				String donationDate = rs.getString("donation_date");
				String expirationDate = rs.getString("expiration_date");
				int donorID = rs.getInt("donor_id");
				String status = rs.getString("status");
				
				BloodStock bag = new BloodStock(stockID, bloodGroup, donationDate, expirationDate, donorID, status);
				
				stockList.add(bag);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return stockList;
	}
	// Background Task: Automatically expire old blood bags
    public static int sweepExpiredBags() {
        // CURDATE() is a MySQL function that gets today's exact date!
        String query = "UPDATE bloodstock SET status = 'Expired' " +
                       "WHERE expiration_date <= CURDATE() AND status = 'Available'";
                       
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            // Execute the update and return how many rows were changed
            return pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}