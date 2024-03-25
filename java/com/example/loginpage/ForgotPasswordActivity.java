package com.example.loginpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextView message2 = findViewById(R.id.message2);
        String messageText = "Unfortunately, Account Recovery feature via this app is not yet available in the current version.If you have forgotten your username or password, please notify the developer.";
        SpannableString spannableString = new SpannableString(messageText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                // Open the webpage in a browser
                Uri webpage = Uri.parse("https://web.facebook.com/nazty00/");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        };

        // Set the clickable span for the link text
        int startIndex = messageText.indexOf("the developer");
        int endIndex = startIndex + "the developer".length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the color of the link text
        int linkColor = ContextCompat.getColor(this, R.color.purple_700);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(linkColor);
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the text with the clickable link
        message2.setText(spannableString);
        message2.setMovementMethod(LinkMovementMethod.getInstance());
    }
}