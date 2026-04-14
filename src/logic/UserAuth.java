package logic;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserAuth {
	// Returns "ADMIN", "STAFF", or null if login fails
	public static String authenticate(String username, String password) {
        String role = null;
        String query = "SELECT role FROM users WHERE BINARY username = ? AND BINARY password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                role = rs.getString("role");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }
}
