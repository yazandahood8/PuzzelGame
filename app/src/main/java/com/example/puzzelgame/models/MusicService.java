package com.example.puzzelgame.models;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.puzzelgame.R;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Replace with your actual music file in res/raw (e.g., bg_music.mp3)
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_music);
        mediaPlayer.setLooping(true); // Loop forever
        mediaPlayer.setVolume(1.0f, 1.0f);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not binding
    }
}
