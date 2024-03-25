package com.example.loginpage;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStorageState;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class GenerateCodeActivity extends AppCompatActivity {

    EditText inputField;
    private ImageView imageView;
    int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    int REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final int REQUEST_CODE_OPEN_DIRECTORY = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        MaterialButton openQRCodesBtn = findViewById(R.id.openQrCodesBtn);
        Spinner nameSpinner = findViewById(R.id.nameSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(adapter);

        imageView = findViewById(R.id.QR_Image);
        inputField = findViewById(R.id.edt_value);
        Button btnSave = findViewById(R.id.generateQrSaveBtn);

        openQRCodesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
                }
                catch(Exception e) {
                    Log.e("OpeningError", e.getMessage());
                }
            }
        });

        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = parent.getItemAtPosition(position).toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trike_driver");
                databaseReference.orderByChild("a_full_name").equalTo(selectedName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String plateNumber = snapshot.child("plate_number").getValue(String.class);
                            inputField.setText(plateNumber);
                        }
                    }

                    @SuppressLint("LogConditional")
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG", "Database Error: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Don't mind me. I'll just sit here...
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Don't mind me, you don't need me...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputField.getText().toString().length() == 0) {
                    Toast.makeText(GenerateCodeActivity.this, "Something went wrong while fetching the data.", Toast.LENGTH_SHORT).show();
                }
                else {
                    generateQRCode();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Don't mind me, you don't need me either...
            }
        };

        inputField.addTextChangedListener(textWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        saveQR();
                    }
                } else {
                    saveQR();
                }
            }

        });

        FirebaseDatabase.getInstance().getReference().child("trike_driver").orderByChild("a_full_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> names = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    if (data != null) {
                        String name = (String) data.get("a_full_name");
                        if (name != null) {
                            names.add(name);
                        }
                    }
                }

                adapter.clear();
                adapter.addAll(names);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE || requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveQR();
                }
            } else {
                Toast.makeText(GenerateCodeActivity.this, "Write/Read/Manage external storage permission denied Grant results: "+grantResults[0], Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void saveQR() {
        try {
            Spinner mySpinner = findViewById(R.id.nameSpinner);
            String outputFileName = mySpinner.getSelectedItem().toString();

            Drawable drawable = imageView.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, outputFileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/QR Codes/");

            // Insert the metadata into the MediaStore and get the content Uri of the new image
            ContentResolver resolver = getContentResolver();
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Open an output stream to the new image and write the bitmap to it
            OutputStream os = resolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();

            Toast toast = Toast.makeText(this, "QR code saved to Phone Storage/" + Environment.DIRECTORY_DCIM+"/QR Codes", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    void generateQRCode() {
        try {
            if(inputField.getText().toString().length() != 0) {
                int smallerDimension = 487;

                QRGEncoder qrgEncoder = new QRGEncoder(inputField.getText().toString().trim(), null, QRGContents.Type.TEXT, smallerDimension);
                try {
                    Bitmap bitmapResult = qrgEncoder.getBitmap();
                    imageView.setImageBitmap(bitmapResult);

                    ColorMatrix colorMatrix_Invert = new ColorMatrix(new float[] {
                            -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                            0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                            0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                    });
                    ColorFilter colorFilter_Invert = new ColorMatrixColorFilter(colorMatrix_Invert);
                    Paint paint = new Paint();
                    paint.setColorFilter(colorFilter_Invert);

                    Bitmap bitmapInverted = Bitmap.createBitmap(bitmapResult.getWidth(), bitmapResult.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmapInverted);
                    canvas.drawBitmap(bitmapResult, 0, 0, paint);
                    imageView.setImageBitmap(bitmapInverted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            Log.e("The Error:", e.getMessage());
        }
    }

}