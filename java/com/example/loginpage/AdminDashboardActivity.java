package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Duration;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        MaterialButton profileBtn = findViewById(R.id.adminProfileBtn);
        MaterialButton logoutBtn = findViewById(R.id.logOutBtn);
        MaterialButton aboutBtn = findViewById(R.id.info);
        MaterialButton trikeDriversBtn = findViewById(R.id.adminTrikeDriversBtn);
        MaterialButton addBtn = findViewById(R.id.studentDashboardScanBtn);
        MaterialButton rideHistoryBtn = findViewById(R.id.adminRideHistoryBtn);

        profileBtn.setOnClickListener(v -> {
            Intent i = new Intent(AdminDashboardActivity.this, AdminProfileActivity.class);
            startActivity(i);
        });

        logoutBtn.setOnClickListener(view -> {
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setText("Please wait...");
            toast.show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        trikeDriversBtn.setOnClickListener(view -> {
            Intent i = new Intent(AdminDashboardActivity.this, TrikeDriversActivity.class);
            startActivity(i);
        });

        rideHistoryBtn.setOnClickListener(v -> {
            Intent i = new Intent(AdminDashboardActivity.this, StudentsRideHistoryActivity.class);
            startActivity(i);
        });

        aboutBtn.setOnClickListener(view -> {
            Intent i = new Intent(AdminDashboardActivity.this, AboutActivity.class);
            startActivity(i);
        });

        addBtn.setOnClickListener(v -> {
            Intent i = new Intent(AdminDashboardActivity.this, AddTrikeActivity.class);
            startActivity(i);
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}