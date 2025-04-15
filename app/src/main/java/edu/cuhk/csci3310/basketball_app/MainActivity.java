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

    Button btnGameState, btnCourtFinder, btnGameStatsCounter, btnPlayerList, btnGameStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGameState = findViewById(R.id.btnGameState);
        btnCourtFinder = findViewById(R.id.btnCourtFinder);
        btnGameStatsCounter = findViewById(R.id.btnGameStatsCounter);
        btnPlayerList = findViewById(R.id.btnPlayerList);
        btnGameStats = findViewById(R.id.btnGameStats);

        btnGameState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameStateTrackerActivity.class);
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

        btnGameStatsCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerStatsCounterActivity.class);
                startActivity(intent);
            }
        });

        btnGameStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For direct navigation to player stats, we'll go to the player list first
                // The user will then select a player to view their stats
                Intent intent = new Intent(MainActivity.this, GameStatsDisplayActivity.class);
                startActivity(intent);
            }
        });

        btnPlayerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerListActivity.class);
                startActivity(intent);
            }
        });


    }
}
