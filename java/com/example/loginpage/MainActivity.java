package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int INIT_FIREBASE = 1;
    EditText usernameField;
    EditText passwordField;

    Handler handler;
    private boolean firebaseInitialized = false;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.passwordField);
        TextView forgotLabel = findViewById(R.id.forgotLabel);
        MaterialButton loginBtn = findViewById(R.id.loginBtn);
        MaterialButton signupBtn = findViewById(R.id.signupBtn);
        ImageView logo = findViewById(R.id.appSealImg);
        TextView orText = findViewById(R.id.orLabel);

        final int[] x = {0}; //For Easter egg only...

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == INIT_FIREBASE) {
                    initializeFirebase();
                }
            }
        };

        Thread thread = new Thread(() -> {
            Looper.prepare();
            handler.sendEmptyMessage(INIT_FIREBASE);
            Looper.loop();
        });
        thread.start();

        loginBtn.setOnClickListener(view -> {
            String userInput = usernameField.getText().toString();
            String passwordInput = passwordField.getText().toString();

            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean matchFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String dbUsername = snapshot.child("user_name").getValue(String.class);
                        String dbPassword = snapshot.child("password").getValue(String.class);
                        String isAdmin = snapshot.child("is_admin").getValue(String.class);

                        assert dbPassword != null;
                        byte[] encodedData = dbPassword.getBytes(StandardCharsets.UTF_8);
                        byte[] tempPassword = Base64.decode(encodedData, Base64.DEFAULT);
                        String finalPassword = new String(tempPassword, StandardCharsets.UTF_8);

                        assert dbUsername != null;
                        if (dbUsername.equals(userInput) && finalPassword.equals(passwordInput) && (isAdmin != null && isAdmin.equals("yes"))) {
                            matchFound = true;
                            openAdminDashboard();
                            break;
                        }
                        else if (dbUsername.equals(userInput) && finalPassword.equals(passwordInput) && (isAdmin != null && isAdmin.equals("no"))) {
                            matchFound = true;
                            openStudentDashboard();
                            break;
                        }
                    }
                    if (!matchFound) {
                        Toast.makeText(MainActivity.this, "Invalid username and/or password! please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("databaseError", databaseError.toString());
                }
            });
        });

        logo.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
        });

        signupBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(i);
        });

        forgotLabel.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(i);
        });

        orText.setOnClickListener(v -> {
            x[0] += 1;
             if(x[0] == 5) {
                 x[0] = 0;
                 Intent i = new Intent(MainActivity.this, EasterEggActivity.class);
                 startActivity(i);
             }
        });

    }

    public void openStudentDashboard() {
        ProgressBar progressBar = findViewById(R.id.pBar);
        progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        String currentUser = usernameField.getText().toString();

        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("user_name").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("stud_id").getValue(String.class);

                    DataHolder dataHolder = DataHolder.getInstance();
                    dataHolder.setId(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("databaseError", databaseError.toString());
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_KEY", currentUser);
        editor.apply();

        usernameField.setText("");
        passwordField.setText("");

        handler.postDelayed(() -> {
            Intent i = new Intent(MainActivity.this, StudentDashboardActivity.class);
            startActivity(i);
        }, 1000);
    }

    public void openAdminDashboard() {
        String currentUser = usernameField.getText().toString();
//        ProgressBar progressBar = findViewById(R.id.pBar);
//        progressBar.setVisibility(View.VISIBLE);
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            Intent i = new Intent(MainActivity.this, AdminDashboardActivity.class);
//            startActivity(i);
//        }, 1000);
        Intent i = new Intent(MainActivity.this, AdminDashboardActivity.class);
        startActivity(i);

        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("user_name").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("stud_id").getValue(String.class);

                    AdminDataHolder dataHolder = AdminDataHolder.getInstance();
                    dataHolder.setId(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("databaseError", databaseError.toString());
            }
        });

        AdminDataHolder dataHolder = AdminDataHolder.getInstance();
        dataHolder.setUser(currentUser);
        usernameField.setText("");
        passwordField.setText("");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {

        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    void initializeFirebase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        firebaseInitialized = true;
        Log.d(TAG, "Firebase initialized");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firebaseInitialized) {
            Log.d(TAG, "Firebase not initialized yet");
        }
    }

}
