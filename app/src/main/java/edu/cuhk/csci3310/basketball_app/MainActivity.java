package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnGameState, btnPlayerStats, btnCourtFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGameState = findViewById(R.id.btnGameState);
        btnPlayerStats = findViewById(R.id.btnPlayerStats);
        btnCourtFinder = findViewById(R.id.btnCourtFinder);

        btnGameState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameStateTrackerActivity.class);
                startActivity(intent);
            }
        });

        btnPlayerStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerStatisticsActivity.class);
                startActivity(intent);
            }
        });

        btnCourtFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CourtFinderActivity.class);
                startActivity(intent);
            }
        });
    }
}