package com.example.puzzelgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.puzzelgame.models.MusicService;

public class FirstActivity extends AppCompatActivity {

    Button btnPuzzle, btnTrivia;
    Button btnToggleMusic;
    boolean isMusicPlaying = true;

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MusicService.class));
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnToggleMusic = findViewById(R.id.btnToggleMusic);

        btnPuzzle = findViewById(R.id.btnPuzzle);
        btnTrivia = findViewById(R.id.btnTrivia);

        btnPuzzle.setOnClickListener(v -> {
            Intent intent = new Intent(FirstActivity.this, MainActivity.class); // puzzle main
            startActivity(intent);
        });

        btnTrivia.setOnClickListener(v -> {
            Intent intent = new Intent(FirstActivity.this, TriviaActivity.class); // trivia
            startActivity(intent);
        });
        btnToggleMusic.setOnClickListener(v -> {
            if (isMusicPlaying) {
                stopService(new Intent(this, MusicService.class));
                btnToggleMusic.setText("▶️ Start Music");
            } else {
                startService(new Intent(this, MusicService.class));
                btnToggleMusic.setText("🔇 Stop Music");
            }
            isMusicPlaying = !isMusicPlaying;
        });
    }
}
