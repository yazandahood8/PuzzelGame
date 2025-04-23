package com.example.puzzelgame;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puzzelgame.adapters.UserRankAdapter;
import com.example.puzzelgame.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopRanksActivity extends AppCompatActivity {

    private ListView listView;
    private UserRankAdapter adapter;
    private ArrayList<User> userList = new ArrayList<>();
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ranks);

        listView = findViewById(R.id.ranksList);
        adapter = new UserRankAdapter(this, userList);
        listView.setAdapter(adapter);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadTopUsers();
    }

    private void loadTopUsers() {
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                userList.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }

                // Sort users by level descending
                Collections.sort(userList, (u1, u2) -> Integer.compare(u2.level, u1.level));
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
