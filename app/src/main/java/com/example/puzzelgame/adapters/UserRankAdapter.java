package com.example.puzzelgame.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.puzzelgame.R;
import com.example.puzzelgame.models.User;

import java.util.List;

public class UserRankAdapter extends ArrayAdapter<User> {

    private final Activity context;
    private final List<User> users;

    public UserRankAdapter(Activity context, List<User> users) {
        super(context, R.layout.rank_item, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.rank_item, null, true);

        TextView name = row.findViewById(R.id.userName);
        TextView level = row.findViewById(R.id.userLevel);

        name.setText((position + 1) + ". " + users.get(position).name);
        level.setText("Level: " + users.get(position).level);

        return row;
    }
}
