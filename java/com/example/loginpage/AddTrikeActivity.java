package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

public class AddTrikeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trike);

        EditText fullNameField = findViewById(R.id.addTrikefullName);
        EditText plateNumField = findViewById(R.id.plateNumber);
        EditText addressField = findViewById(R.id.addTrikeAddress);
        EditText phoneField = findViewById(R.id.addTrikePhone);
        MaterialButton saveBtn = findViewById(R.id.addTrikeSaveBtn);

        saveBtn.setOnClickListener(v -> {
            String fullName = fullNameField.getText().toString();
            String plateNumber = plateNumField.getText().toString();
            String address = addressField.getText().toString();
            String phone = phoneField.getText().toString();

            if (fullName.length() < 1 || plateNumber.length() < 1 || address.length() < 1 || phone.length() < 1) {
                Toast.makeText(AddTrikeActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            }
            else {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trike_driver");
                databaseReference.runTransaction(new Transaction.Handler() {

                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer id = mutableData.child("id").getValue(Integer.class);
                        if (id == null) {
                            id = 1;
                        } else {
                            id++;
                        }
                        String key = databaseReference.push().getKey();
                        Map<String, Object> values = new HashMap<>();
                        values.put("id", id);
                        values.put("a_full_name", fullName);
                        values.put("plate_number", plateNumber);
                        values.put("b_address", address);
                        values.put("phone", phone);
                        databaseReference.child(key).updateChildren(values);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        if (error == null) {
                            //Don't mind me, I'm just a comment...
                        }
                        else {
                            Toast.makeText(AddTrikeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Toast toast = Toast.makeText(AddTrikeActivity.this, "New trike driver added successfully!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                fullNameField.setText("");
                plateNumField.setText("");
                addressField.setText("");
                phoneField.setText("");
            }
        });

    }
}