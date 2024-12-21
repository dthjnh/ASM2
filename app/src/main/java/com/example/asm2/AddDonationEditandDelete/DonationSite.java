package com.example.asm2.AddDonationEditandDelete;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonationSite {
    private int id;
    private String name;
    private String address;
    private String hours;
    private String bloodTypes;
    private double latitude;
    private double longitude;
    private String creatorType;

    public DonationSite(int id, String name, String address, String hours, String bloodTypes, double latitude, double longitude, String creatorType) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.hours = hours;
        this.bloodTypes = bloodTypes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creatorType = creatorType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getHours() {
        return hours;
    }

    public String getBloodTypes() {
        return bloodTypes;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCreatorType() {
        return creatorType;
    }

    public List<String> getRequiredBloodTypes() {
        return Arrays.asList(bloodTypes.split(","));
    }

    public boolean isOpenNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String[] hoursParts = hours.split(" - ");
        try {
            Date openingTime = sdf.parse(hoursParts[0]);
            Date closingTime = sdf.parse(hoursParts[1]);
            Date currentTime = sdf.parse(sdf.format(new Date()));

            return currentTime.after(openingTime) && currentTime.before(closingTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}