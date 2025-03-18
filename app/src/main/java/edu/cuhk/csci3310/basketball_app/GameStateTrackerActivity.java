package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameStateTrackerActivity extends AppCompatActivity {

    private int score = 0;
    private TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_state_tracker);

        scoreTextView = findViewById(R.id.scoreTextView);
        Button scoreButton = findViewById(R.id.scoreButton);
        Button playerStatsButton = findViewById(R.id.playerStatsButton);
        Button courtFinderButton = findViewById(R.id.courtFinderButton);

        // Button click to update score
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score++;
                scoreTextView.setText("Score: " + score);
            }
        });

        // Navigate to PlayerStatisticsActivity
        playerStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameStateTrackerActivity.this, PlayerStatisticsActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to CourtFinderActivity
        courtFinderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameStateTrackerActivity.this, CourtFinderActivity.class);
                startActivity(intent);
            }
        });
    }
}
