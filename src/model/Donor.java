package model;

public class Donor {
	private int donor_id;
    private String name;
    private int age;
    private String bloodGroup;
    private String phone;
    private String medicalIssue;

 // CONSTRUCTOR 1: For reading EXISTING donors from the Database (Includes ID)
    public Donor(int donor_id, String name, int age, String bloodGroup, String phone, String medicalIssue) {
        this.donor_id = donor_id;
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.medicalIssue = medicalIssue;
    }

    // CONSTRUCTOR 2: For creating BRAND NEW donors in the UI (No ID needed)
    public Donor(String name, int age, String bloodGroup, String phone, String medicalIssue) {
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.medicalIssue = medicalIssue;
    }

    // Getters
    public int getDonorId() {return donor_id;}
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getBloodGroup() { return bloodGroup; }
    public String getPhone() { return phone; }
    public String getMedicalIssue() { return medicalIssue; }
}