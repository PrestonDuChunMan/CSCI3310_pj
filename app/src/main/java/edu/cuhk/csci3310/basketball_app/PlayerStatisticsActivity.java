package edu.cuhk.csci3310.basketball_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import android.view.View;
import androidx.core.view.ViewCompat;
import android.widget.TextView;
import androidx.core.view.WindowInsetsCompat;

public class PlayerStatisticsActivity extends AppCompatActivity {

    private TextView statsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_statistics);

        statsTextView = findViewById(R.id.statsTextView);
        // Example of setting some dummy statistics data
        statsTextView.setText("Player: John Doe\nPoints: 12\nAssists: 5\nRebounds: 7");
    }
}

//DUMMY