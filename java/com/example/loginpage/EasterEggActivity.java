package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class EasterEggActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg);

        // Load and display the GIF image
        ImageView imageView = findViewById(R.id.gifImage);
        Glide.with(this).asGif().load(R.drawable.androidapple).into(imageView);

        // Play the sound when the activity is created
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.backgroundforapp);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            afd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the sound when the activity is destroyed
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
