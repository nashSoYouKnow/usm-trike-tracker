package com.example.loginpage;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String text = "The USM Trike Tracker is a mobile app developed by <b>Nazer V. Malaguia</b> in January, 2023. As a response " +
                "to demand for safety of the commuter students in <font color='#FFD700'><b>University of Southern Mindanao</b></font>, this app was created. " +
                "The main functionality of this app is to <font color='#FDFEFE'><strong>record the ride session</strong></font> " +
                "of the students every time they ride on a tricycle through <font color='#FCF3CF'><strong>scanning of a QR " +
                "code</strong></font> which can be found in front of every tricycle (if implemented). Every scan will be recorded " +
                "and considered as a ride session. The highlight feature of this app is its ability to do transactions in the " +
                "absence of internet connection. You can even use the app even in <b>airplane mode</b>. All transactions you " +
                "processed while you were offline will be automatically recorded to the online database as soon as your phone " +
                "detected an internet connection, thanks to Google's <b>Firebase</b> platform that provides a feature called " +
                "<b><i>Offline Data Persistence</i></b> that allows the app to continue functioning even when the device is " +
                "not connected to the internet. With this feature, Firebase stores a copy of the data locally on the device and " +
                "<i>synchronizes</i> the data with the server as soon as the device goes back online. Below are the detailed information " +
                "about the USM Trike Tracker app.";

        TextView introTextView = findViewById(R.id.intro);
        introTextView.setText(Html.fromHtml(text));

        String adminInsText = "\tAdmin: Click on the Profile button to see your profile details. To add a new trike driver, click on the Add " +
                "Trike Driver button. Enter all necessary information of the driver and his tricycle and click on Add button. To see the list " +
                "of trike drivers, click on Trike Drivers button. There you can see the Trike Drivers table with a search bar at the top. " +
                "At the upper left corner of the screen, you can see a dropdown button for sorting the data. You can sort the order of " +
                "data being displayed on the table by Name, Address, Phone #, and Plate #. At the bottom of the screen is the " +
                "Generate QR Code button. Click on it to open a new window where you can Generate a QR code based on the selected driver. " +
                "After selecting a driver from the dropdown list, the corresponding QR code will be automatically generated. Tap on the Save " +
                "QR Code button to save the QR code to your phone's internal storage. It is located in <b>Phone Storage/DCIM/QR Codes</b>. " +
                "To check all the saved QR codes, Tap on Open QR Codes Directory button. Going back to the Admin dashboard, tap on the <b>Ride " +
                "History</b> button to open the window that displays all the ride history of all students. ";
        TextView adminInsTextView = findViewById(R.id.adminInstruction);
        adminInsTextView.setText(Html.fromHtml(adminInsText));

    }
}