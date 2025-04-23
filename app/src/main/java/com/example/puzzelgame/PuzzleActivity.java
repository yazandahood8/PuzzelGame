package com.example.puzzelgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private Button resetButton;
    private TextView timerText;
    private MediaPlayer mediaPlayer;

    private int gridSize;
    private Bitmap[] originalPieces;
    private Bitmap[] currentPieces;
    private int draggedIndex = -1;

    private int[] sound_effects;

    private CountDownTimer countDownTimer;
    private int timeLimit,level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        gridLayout = findViewById(R.id.puzzleGrid);
        resetButton = findViewById(R.id.resetButton);
        timerText = findViewById(R.id.timerText);

        resetButton.setOnClickListener(v -> setupPuzzle());
    //    sound_effects= new int[]{R.raw.amazing, R.raw.welldone};
        sound_effects= new int[]{R.raw.amazing};
        Button solveButton = findViewById(R.id.solveButton);
        solveButton.setOnClickListener(v -> solvePuzzle());
        setupPuzzle();
    }
    private void solvePuzzle() {
        currentPieces = Arrays.copyOf(originalPieces, originalPieces.length);
        showPuzzlePieces();
        if (countDownTimer != null) countDownTimer.cancel();
        Toast.makeText(this, "🧠 Puzzle Solved Automatically!", Toast.LENGTH_SHORT).show();
       // updateUserLevelInFirebase();
    }
    private void setupPuzzle() {
        if (countDownTimer != null) countDownTimer.cancel();

        int imageId = getIntent().getIntExtra("imageId", R.drawable.img1);
        gridSize = getIntent().getIntExtra("gridSize", 3);
        timeLimit = getIntent().getIntExtra("timeLimit", 60);
        level = getIntent().getIntExtra("level", 1);

        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        Bitmap image = BitmapFactory.decodeResource(getResources(), imageId);
        originalPieces = splitImage(image, gridSize);
        currentPieces = Arrays.copyOf(originalPieces, originalPieces.length);
        shuffleArray(currentPieces);
        showPuzzlePieces();
        startTimer(timeLimit);
    }

    private void startTimer(int seconds) {
        timerText.setText("Time: " + seconds + "s");

        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                Toast.makeText(PuzzleActivity.this, "⏰ Time's up!", Toast.LENGTH_LONG).show();
                gridLayout.removeAllViews(); // disable interaction
            }
        }.start();
    }

    private Bitmap[] splitImage(Bitmap image, int size) {
        int pieceWidth = image.getWidth() / size;
        int pieceHeight = image.getHeight() / size;
        Bitmap[] pieces = new Bitmap[size * size];

        int index = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                pieces[index++] = Bitmap.createBitmap(image, x * pieceWidth, y * pieceHeight, pieceWidth, pieceHeight);
            }
        }
        return pieces;
    }

    private void shuffleArray(Bitmap[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Bitmap temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showPuzzlePieces() {
        gridLayout.removeAllViews();

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        int spacing = 4;
        int tileWidth = screenWidth / gridSize - spacing * 2;
        int tileHeight = screenHeight / gridSize - spacing * 2;

        for (int i = 0; i < currentPieces.length; i++) {
            final ImageView tile = new ImageView(this);
            tile.setImageBitmap(currentPieces[i]);
            tile.setScaleType(ImageView.ScaleType.FIT_XY);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = tileWidth;
            params.height = tileHeight;
            params.setMargins(spacing, spacing, spacing, spacing);
            tile.setLayoutParams(params);
            tile.setTag(i);

            tile.setOnLongClickListener(v -> {
                draggedIndex = (int) tile.getTag();
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(tile);
                tile.startDragAndDrop(null, shadowBuilder, null, 0);
                return true;
            });

            tile.setOnDragListener((v, event) -> {
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    ImageView target = (ImageView) v;
                    int targetIndex = (int) target.getTag();

                    Bitmap temp = currentPieces[draggedIndex];
                    currentPieces[draggedIndex] = currentPieces[targetIndex];
                    currentPieces[targetIndex] = temp;

                    showPuzzlePieces();

                    if (isSolved()) {
                        if (countDownTimer != null) countDownTimer.cancel();
                        Toast.makeText(this, "🎉 Puzzle Completed!", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            });

            gridLayout.addView(tile);
        }
    }

    private boolean isSolved() {
        for (int i = 0; i < originalPieces.length; i++) {
            if (originalPieces[i] != currentPieces[i]) return false;
        }
        // puzzle solved, update level

        updateUserLevelInFirebase();

        return true;
    }
    private void updateUserLevelInFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("level");

        // Increase level by 1 (if not already higher)
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                int currentLevel = snapshot.getValue(Integer.class);
                if (level >= currentLevel) {
                    userRef.setValue(level + 1);
                }
            } else {
                userRef.setValue(level + 1);
            }

            int randomSound = sound_effects[new java.util.Random().nextInt(sound_effects.length)];
            mediaPlayer = MediaPlayer.create(PuzzleActivity.this, randomSound);
            mediaPlayer.start();

            // Navigate to MainActivity after sound
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                Intent intent = new Intent(PuzzleActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });

        });
    }

}
