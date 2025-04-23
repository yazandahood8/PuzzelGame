package com.example.puzzelgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puzzelgame.adapters.LevelAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class
MainActivity extends AppCompatActivity {

    String[] levels = new String[20];
    int[] imageIds = new int[20];
    int[] gridSizes = new int[20];
    int[] timeLimits = new int[20];

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    ListView levelList;
    Button btnTopRanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        levelList = findViewById(R.id.levelList);
        btnTopRanks = findViewById(R.id.btnTopRanks);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize levels
        for (int i = 0; i < 20; i++) {
            levels[i] = "Level " + (i + 1);
            imageIds[i] = getResources().getIdentifier("img" + (i + 1), "drawable", getPackageName());

            if (i < 2) { gridSizes[i] = 2; timeLimits[i] = 30; }
            else if (i < 5) { gridSizes[i] = 3; timeLimits[i] = 45 + i * 5; }
            else if (i < 8) { gridSizes[i] = 4; timeLimits[i] = 90 + (i - 5) * 10; }
            else if (i < 10) { gridSizes[i] = 5; timeLimits[i] = 120 + (i - 8) * 10; }
            else if (i < 13) { gridSizes[i] = 6; timeLimits[i] = 150 + (i - 10) * 10; }
            else if (i < 15) { gridSizes[i] = 7; timeLimits[i] = 180 + (i - 13) * 10; }
            else if (i < 17) { gridSizes[i] = 8; timeLimits[i] = 200 + (i - 15) * 10; }
            else if (i < 19) { gridSizes[i] = 9; timeLimits[i] = 220 + (i - 17) * 10; }
            else { gridSizes[i] = 10; timeLimits[i] = 240; }
        }

        loadUserLevel();
        btnTopRanks.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TopRanksActivity.class)));
    }

    private void loadUserLevel() {
        String uid = mAuth.getCurrentUser().getUid();
        userRef.child(uid).child("level").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int userLevel = 1; // default fallback
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    userLevel = snapshot.getValue(Integer.class);
                }

                LevelAdapter adapter = new LevelAdapter(MainActivity.this, levels, gridSizes, userLevel);
                levelList.setAdapter(adapter);

                int finalUserLevel = userLevel;
                levelList.setOnItemClickListener((parent, view, position, id) -> {
                    if (position + 1 <= finalUserLevel) {
                        Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                        intent.putExtra("level", position + 1);

                        intent.putExtra("imageId", imageIds[position]);
                        intent.putExtra("gridSize", gridSizes[position]);
                        intent.putExtra("timeLimit", timeLimits[position]);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "🚫 Level locked!", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(this, "Failed to load user level", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
