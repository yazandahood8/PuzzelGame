package com.example.puzzelgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class TriviaActivity extends AppCompatActivity {

    private TextView tvQuestion, tvScore;
    private Button btnA, btnB, btnC, btnD;
    private String correctAnswer;
    private int score = 0;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "TriviaPrefs";
    private static final String SCORE_KEY = "score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        score = prefs.getInt(SCORE_KEY, 0);
        updateScoreDisplay();

        loadTriviaQuestion();
    }

    private void updateScoreDisplay() {
        tvScore.setText("Score: " + score);
    }

    private void loadTriviaQuestion() {
        enableAllButtons();
        String url = "https://opentdb.com/api.php?amount=111";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray results = response.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject q = results.getJSONObject(0);
                    String question = Html.fromHtml(q.getString("question")).toString();
                    correctAnswer = Html.fromHtml(q.getString("correct_answer")).toString();
                    Toast.makeText(TriviaActivity.this,correctAnswer,Toast.LENGTH_LONG).show();
                    JSONArray incorrects = q.getJSONArray("incorrect_answers");
                    ArrayList<String> answers = new ArrayList<>();
                    answers.add(correctAnswer);
                    for (int i = 0; i < incorrects.length(); i++) {
                        answers.add(Html.fromHtml(incorrects.getString(i)).toString());
                    }
                    Collections.shuffle(answers);

                    tvQuestion.setText(question);
                    btnA.setText(answers.get(0));
                    btnB.setText(answers.get(1));
                    try {
                        btnC.setText(answers.get(2));
                        btnD.setText(answers.get(3));
                        btnD.setVisibility(View.VISIBLE);
                        btnC.setVisibility(View.VISIBLE);
                    }catch (Exception ex){
                        btnD.setVisibility(View.GONE);
                        btnC.setVisibility(View.GONE);
                    }


                    setListeners(btnA, btnB, btnC, btnD);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error parsing trivia", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void setListeners(Button... buttons) {
        for (Button btn : buttons) {
            btn.setOnClickListener(v -> {
                String selected = btn.getText().toString();
                if (selected.equals(correctAnswer)) {
                    score++;
                    prefs.edit().putInt(SCORE_KEY, score).apply();
                    updateScoreDisplay();
                    Toast.makeText(this, "🎉 Correct! +1", Toast.LENGTH_SHORT).show();
                    loadTriviaQuestion(); // load next question
                } else {
                    Toast.makeText(this, "❌ Wrong! Answer: " + correctAnswer, Toast.LENGTH_LONG).show();
                    disableAllButtons();
                    startActivity(new Intent(TriviaActivity.this, FirstActivity.class));
                    finish();
                }
            });
        }
    }

    private void enableAllButtons() {
        btnA.setEnabled(true);
        btnB.setEnabled(true);
        btnC.setEnabled(true);
        btnD.setEnabled(true);
    }

    private void disableAllButtons() {
        btnA.setEnabled(false);
        btnB.setEnabled(false);
        btnC.setEnabled(false);
        btnD.setEnabled(false);
    }
}
