package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView editImg = findViewById(R.id.editImg);
        MaterialButton saveBtn = findViewById(R.id.saveBtn);
        EditText fnameField = findViewById(R.id.fnameField);
        EditText unameField = findViewById(R.id.unameField);
        EditText idField = findViewById(R.id.idField);
        EditText phoneField = findViewById(R.id.phoneField);
        TextView idText = findViewById(R.id.idText);

        editImg.setOnClickListener(view -> {
            Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            editImg.startAnimation(animFadein);
            fnameField.setEnabled(true);
//            idField.setEnabled(true);
            phoneField.setEnabled(true);
            fnameField.requestFocus();
            saveBtn.setVisibility(View.VISIBLE);
            saveBtn.setEnabled(true);
        });

        final SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCE", MODE_PRIVATE);
        final String currentUser = sharedPreferences.getString("USER_KEY", "");

        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("user_name").equalTo(currentUser).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String full_name = snapshot.child("full_name").getValue(String.class);
                    String username = snapshot.child("user_name").getValue(String.class);
                    String id = snapshot.child("stud_id").getValue(String.class);
                    String phone = snapshot.child("contact").getValue(String.class);

                    assert username != null;
                    if (username.equals(currentUser)) {
                        unameField.setText(username);
                        fnameField.setText(full_name);
                        idField.setText(id);
                        phoneField.setText(phone);
                        idText.setText("Active ||"+id);
                        break;
                    }
                    else {
                        unameField.setText(currentUser);
                        fnameField.setText("Unable to obtain");
                        idField.setText("Unable to obtain");
                        phoneField.setText("Unable to obtain");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaseError", databaseError.toString());
            }
        });

        saveBtn.setOnClickListener(view -> {
            String newFname = fnameField.getText().toString();
            String uName = unameField.getText().toString();
//            String newId = idField.getText().toString();
            String newPhone = phoneField.getText().toString();

            fnameField.setEnabled(false);
            unameField.setEnabled(false);
//            idField.setEnabled(false);
            phoneField.setEnabled(false);
            saveBtn.setVisibility(View.INVISIBLE);
            saveBtn.setEnabled(false);

            FirebaseDatabase.getInstance().getReference().child("user").orderByChild("user_name").equalTo(uName).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("LogConditional")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        assert id != null;

                        FirebaseDatabase.getInstance().getReference().child("user").child(id).child("full_name").setValue(newFname);
                        FirebaseDatabase.getInstance().getReference().child("user").child(id).child("contact").setValue(newPhone);
//                        FirebaseDatabase.getInstance().getReference().child("user").child(id).child("stud_id").setValue(newId);

                        Toast toast = Toast.makeText(getApplicationContext(), "Changes saved successfully!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
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

    @Override
    public void onBackPressed() {
        ProfileActivity.this.finish();
    }

}