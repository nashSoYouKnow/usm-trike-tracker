package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        ImageView editImg = findViewById(R.id.adminEditImg);
        MaterialButton saveBtn = findViewById(R.id.adminSaveBtn);
        EditText fnameField = findViewById(R.id.adminFnameField);
        EditText unameField = findViewById(R.id.adminUnameField);
        EditText phoneField = findViewById(R.id.adminPhoneField);

        editImg.setOnClickListener(view -> {
            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            editImg.startAnimation(animFadeIn);
            fnameField.setEnabled(true);
            phoneField.setEnabled(true);
            fnameField.requestFocus();
            saveBtn.setVisibility(View.VISIBLE);
            saveBtn.setEnabled(true);
        });

        AdminDataHolder dataHolder = AdminDataHolder.getInstance();
        String currentUser = dataHolder.getUser();

        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("user_name").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String full_name = snapshot.child("full_name").getValue(String.class);
                    String username = snapshot.child("user_name").getValue(String.class);
                    String phone = snapshot.child("contact").getValue(String.class);

                    assert username != null;
                    if (username.equals(currentUser)) {
                        unameField.setText(username);
                        fnameField.setText(full_name);
                        phoneField.setText(phone);
                        break;
                    }
                    else {
                        unameField.setText("Unable to obtain");
                        fnameField.setText("Unable to obtain");
                        phoneField.setText("Unable to obtain");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("databaseError", databaseError.toString());
            }
        });

        saveBtn.setOnClickListener(view -> {
            String newFname = fnameField.getText().toString();
            String userName = unameField.getText().toString();
            String newPhone = phoneField.getText().toString();

            fnameField.setEnabled(false);
            unameField.setEnabled(false);
            phoneField.setEnabled(false);
            saveBtn.setVisibility(View.INVISIBLE);
            saveBtn.setEnabled(false);

            FirebaseDatabase.getInstance().getReference().child("user").orderByChild("user_name").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("LogConditional")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        assert id != null;

                            FirebaseDatabase.getInstance().getReference().child("user").child(id).child("full_name").setValue(newFname);
                            FirebaseDatabase.getInstance().getReference().child("user").child(id).child("contact").setValue(newPhone);
                            Toast.makeText(getApplicationContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("databaseError", databaseError.toString());
                }
            });
        });

    }

}