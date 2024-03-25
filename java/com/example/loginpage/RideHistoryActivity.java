package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RideHistoryActivity extends AppCompatActivity {

    DriverInformation driverInfo = DriverInformation.getInstance();
    String monthValue = "";
    String yearValue = "";
    boolean filterUsed;
    List<Integer> yearsList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        ImageView searchButton = findViewById(R.id.searchImg);
        TextView applyFilterText = findViewById(R.id.applyText);

        applyFilterText.setOnClickListener(v -> {
            if(!filterUsed) {
                filterUsed = true;
                applyFilterText.setText("Clear Filter");
                applyFilterText.setTextColor(Color.rgb(83, 253, 53));
                filterHistory();
            }
            else {
                filterUsed = false;
                applyFilterText.setText("Apply Filter");
                applyFilterText.setTextColor(Color.rgb(253, 216, 53));
                loadHistory();
            }
        });

        searchButton.setOnClickListener(v -> {
            EditText searchField = findViewById(R.id.plateSearchField);
            String plateNumber = searchField.getText().toString().toUpperCase();
            setDriverInfo(plateNumber);
        });

        Spinner monthFilterSpinner = findViewById(R.id.monthFilterSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthFilterSpinner.setAdapter(adapter);

        populateYears();
        Spinner yearFilterSpinner = findViewById(R.id.yearFilterSpinner);
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, yearsList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearFilterSpinner.setAdapter(adapter2);

        monthFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: monthValue = "01";
                    break;
                    case 1: monthValue = "02";
                    break;
                    case 2: monthValue = "03";
                    break;
                    case 3: monthValue = "04";
                    break;
                    case 4: monthValue = "05";
                    break;
                    case 5: monthValue = "06";
                    break;
                    case 6: monthValue = "07";
                    break;
                    case 7: monthValue = "08";
                    break;
                    case 8: monthValue = "09";
                    break;
                    case 9: monthValue = "10";
                    break;
                    case 10: monthValue = "11";
                    break;
                    case 11: monthValue = "12";
                    break;
                    default : //Don't mind me!
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                yearValue = "2023";
            }
        });

        loadHistory();
    }

    public void loadHistory() {
        DataHolder dataHolder = DataHolder.getInstance();
        String userID = dataHolder.getId();

        TableLayout rideDataTable = findViewById(R.id.rideDataTable);
        FirebaseDatabase.getInstance().getReference().child("ride_session")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rideDataTable.removeAllViews();

                        // Iterate over the data in the database
                        for (DataSnapshot snapshots : snapshot.getChildren()) {
                            String rideUserID = snapshots.child("stud_id").getValue(String.class);
                            if (rideUserID != null && rideUserID.equals(userID)) {
                                Map<String, Object> data = (Map<String, Object>) snapshots.getValue();

                                TableRow headerRow = findViewById(R.id.rideHeaderRow);
                                TableRow dataRow = new TableRow(getApplicationContext());

                                assert data != null;

                                TextView scanDateTextView = new TextView(getApplicationContext());
                                scanDateTextView.setText(Objects.requireNonNull(data.get("scan_date")).toString());
                                scanDateTextView.setPadding(8, 8, 8, 8);
                                dataRow.addView(scanDateTextView);

                                TextView plateNumberTextView = new TextView(getApplicationContext());
                                plateNumberTextView.setText(Objects.requireNonNull(data.get("plate_number")).toString());
                                plateNumberTextView.setPadding(8, 8, 8, 8);
                                dataRow.addView(plateNumberTextView);

                                for (int i = 0; i < dataRow.getChildCount(); i++) {
                                    TextView dataCell = (TextView) dataRow.getChildAt(i);
                                    TextView headerCell = (TextView) headerRow.getChildAt(i);
                                    dataCell.setWidth(headerCell.getWidth());
                                    dataCell.setPadding(8, 8, 8, 8);
                                }

                                rideDataTable.addView(dataRow);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void filterHistory() {
        DataHolder dataHolder = DataHolder.getInstance();
        String userID = dataHolder.getId();
        String monthYear = monthValue+yearValue;

        TableLayout rideDataTable = findViewById(R.id.rideDataTable);
        FirebaseDatabase.getInstance().getReference("ride_session").orderByChild("month_year")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rideDataTable.removeAllViews();

                        // Iterate over the data in the database
                        for (DataSnapshot snapshots : snapshot.getChildren()) {
                            String month_year = snapshots.child("month_year").getValue(String.class);
                            String rideUserID = snapshots.child("stud_id").getValue(String.class);

                            if (userID.equals(rideUserID)) {
                                assert month_year != null;
                                if (month_year.equals(monthYear)) {
                                    Map<String, Object> data = (Map<String, Object>) snapshots.getValue();
                                    TableRow headerRow = findViewById(R.id.rideHeaderRow);
                                    TableRow dataRow = new TableRow(getApplicationContext());

                                    assert data != null;

                                    TextView scanDateTextView = new TextView(getApplicationContext());
                                    scanDateTextView.setText(Objects.requireNonNull(data.get("scan_date")).toString());
                                    scanDateTextView.setPadding(8, 8, 8, 8);
                                    dataRow.addView(scanDateTextView);

                                    TextView plateNumberTextView = new TextView(getApplicationContext());
                                    plateNumberTextView.setText(Objects.requireNonNull(data.get("plate_number")).toString());
                                    plateNumberTextView.setPadding(8, 8, 8, 8);
                                    dataRow.addView(plateNumberTextView);

                                    for (int i = 0; i < dataRow.getChildCount(); i++) {
                                        TextView dataCell = (TextView) dataRow.getChildAt(i);
                                        TextView headerCell = (TextView) headerRow.getChildAt(i);
                                        dataCell.setWidth(headerCell.getWidth());
                                        dataCell.setPadding(8, 8, 8, 8);
                                    }

                                    rideDataTable.addView(dataRow);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setDriverInfo(String plateInput) {
        FirebaseDatabase.getInstance().getReference().child("trike_driver").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String dbPlate = snapshot.child("plate_number").getValue(String.class);
                                if (dbPlate != null && dbPlate.equals(plateInput)) {
                                    String fullName = snapshot.child("a_full_name").getValue(String.class);
                                    String address = snapshot.child("b_address").getValue(String.class);
                                    String phone = snapshot.child("phone").getValue(String.class);
                                    String plateNum = snapshot.child("plate_number").getValue(String.class);

                                    assert plateNum != null;
                                    assert fullName != null;
                                    assert address != null;
                                    assert phone != null;

                                    driverInfo.setDriverName(fullName);
                                    driverInfo.setDriverAddress(address);
                                    driverInfo.setDriverPhone(phone);
                                    driverInfo.setDriverPlate(plateNum);

                                    AlertDialog.Builder builder;
                                    builder = new AlertDialog.Builder(RideHistoryActivity.this);
                                    builder.setMessage("Name: " + fullName + "\n\nAddress: " + address + "\n\nPhone: " + phone + "\n\nPlate Number: " + plateNum)
                                            .setCancelable(false)
                                            .setPositiveButton("Copy Number", (dialog, id) -> {
                                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("", phone);
                                                clipboard.setPrimaryClip(clip);
                                                Toast toast = Toast.makeText(getApplicationContext(), "Phone number copied to clipboard!", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER, 0,0 );
                                                toast.show();
                                            })
                                            .setNeutralButton("Close", (dialog, id) -> dialog.cancel())
                                            .setNegativeButton("Call Number", (dialog, which) -> {
                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                intent.setData(Uri.parse("tel: "+phone));
                                                startActivity(intent);
                                            })
                                            .setIcon(R.drawable.abouticon);
                                    AlertDialog alert = builder.create();
                                    alert.setTitle("Driver Information");
                                    alert.show();
                                    break;
                                }

                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void populateYears() {
        for (int i = 2023; i <= 2103; i++) {
            yearsList.add(i);
        }
    }

}