package com.example.loginpage;

import androidx.annotation.NonNull;

public class DriverInformation {

    private static DriverInformation instance = null;
    String driverName;
    String driverAddress;
    String driverPhone;
    String driverPlate;

    DriverInformation () {}

    @NonNull
    public static DriverInformation getInstance() {
        if (instance == null) {
            instance = new DriverInformation();
        }
        return instance;
    }

    @NonNull
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(@NonNull String driverName) {
        this.driverName = driverName;
    }

    @NonNull
    public String getDriverAddress() {
        return driverAddress;
    }

    public void setDriverAddress(@NonNull String driverAddress) {
        this.driverAddress = driverAddress;
    }

    @NonNull
    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(@NonNull String driverPhone) {
        this.driverPhone = driverPhone;
    }

    @NonNull
    public String getDriverPlate() {
        return driverPlate;
    }

    public void setDriverPlate(@NonNull String driverPlate) {
        this.driverPlate = driverPlate;
    }
}
