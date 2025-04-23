package com.example.puzzelgame.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.puzzelgame.R;

public class LevelAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] levels;
    private final int[] gridSizes;
    private final int userLevel;

    public LevelAdapter(Activity context, String[] levels, int[] gridSizes, int userLevel) {
        super(context, R.layout.level_item, levels);
        this.context = context;
        this.levels = levels;
        this.gridSizes = gridSizes;
        this.userLevel = userLevel;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.level_item, null, true);

        TextView levelText = row.findViewById(R.id.levelTitle);
        TextView infoText = row.findViewById(R.id.levelInfo);
        ImageView lockIcon = row.findViewById(R.id.lockIcon);

        levelText.setText(levels[position]);

        if (position + 1 <= userLevel) {
            // unlocked
            infoText.setText("Grid: " + gridSizes[position] + "x" + gridSizes[position]);
            row.setAlpha(1f);
            lockIcon.setVisibility(View.GONE);
        } else {
            // locked
            infoText.setText("Locked");
            row.setAlpha(0.5f);
            lockIcon.setVisibility(View.VISIBLE);
        }

        return row;
    }
}
