package com.example.asm2.AddDonationEditandDelete;

public class DonationSite {
    private int id;
    private String address;
    private String hours;
    private String bloodTypes;
    private double latitude;
    private double longitude;
    private String creatorType;

    public DonationSite(int id, String address, String hours, String bloodTypes, double latitude, double longitude, String creatorType) {
        this.id = id;
        this.address = address;
        this.hours = hours;
        this.bloodTypes = bloodTypes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creatorType = creatorType;
    }

    public int getId() { return id; }
    public String getAddress() { return address; }
    public String getHours() { return hours; }
    public String getBloodTypes() { return bloodTypes; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCreatorType() { return creatorType; }
}