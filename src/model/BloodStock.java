package model;

public class BloodStock {
	private int stock_id;
	private String blood_group;
	private String donation_date;
	private String expiration_date;
	private int donor_id;
	private String status;
	
	//Constructor
	public BloodStock(int stock_id, String blood_group, String donation_date, String expiration_date, int donor_id, String status) {
		this.stock_id = stock_id;
		this.blood_group = blood_group;
		this.donation_date = donation_date;
		this.expiration_date = expiration_date;
		this.donor_id = donor_id;
		this.status = status;
	}
	
	//Getters
	public int getStockId() { return stock_id; }
	public String getBloodGroup() { return blood_group; }
	public String getDonationDate() { return donation_date; }
	public String getExpirationDate() { return expiration_date; }
	public int getDonor() { return donor_id; }
	public String getStatus() { return status; }
}
