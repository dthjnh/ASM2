package com.example.asm2.AddDonationEditandDelete;

public class DonationSite {
    private int id;
    private String address;
    private String hours;
    private String bloodTypes;

    public DonationSite(int id, String address, String hours, String bloodTypes) {
        this.id = id;
        this.address = address;
        this.hours = hours;
        this.bloodTypes = bloodTypes;
    }

    public int getId() { return id; }
    public String getAddress() { return address; }
    public String getHours() { return hours; }
    public String getBloodTypes() { return bloodTypes; }
}