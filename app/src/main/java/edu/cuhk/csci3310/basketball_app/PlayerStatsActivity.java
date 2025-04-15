package edu.cuhk.csci3310.basketball_app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PlayerStatsActivity extends AppCompatActivity {

    private TextView playerNameTextView;
    private TextView averageStatsTextView;
    private RecyclerView gamesRecyclerView;
    private GameDataManager dataManager;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        playerName = getIntent().getStringExtra("PLAYER_NAME");
        if (playerName == null) {
            finish();
            return;
        }

        initializeViews();

        dataManager = new GameDataManager(this);
        displayPlayerStats();
    }

    private void initializeViews() {
        playerNameTextView = findViewById(R.id.playerNameTextView);
        averageStatsTextView = findViewById(R.id.averageStatsTextView);
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void displayPlayerStats() {
        playerNameTextView.setText(playerName);

        // Get player's average stats
        GameDataManager.PlayerStats averages = dataManager.getPlayerAverageStats(playerName);

        // Format the averages string
        String averagesText = String.format(Locale.getDefault(),
                "Averages: %.1f PPG (FG: %.1f%%, 3PT: %.1f%%, FT: %.1f%%), %.1f RPG, %.1f APG, %.1f SPG, %.1f BPG, %.1f TOPG",
                averages.points,
                averages.getFgPercentage(),
                averages.get3ptPercentage(),
                averages.getFtPercentage(),
                averages.rebounds,
                averages.assists,
                averages.steals,
                averages.blocks,
                averages.turnovers);

        averageStatsTextView.setText(averagesText);

        // Get all games this player participated in
        List<GameDataManager.PlayerGameStats> gameStats = dataManager.getPlayerStats(playerName);

        Collections.sort(gameStats, new Comparator<GameDataManager.PlayerGameStats>() {
            @Override
            public int compare(GameDataManager.PlayerGameStats a, GameDataManager.PlayerGameStats b) {
                Game gameA = dataManager.getGameById(a.getGameId());
                Game gameB = dataManager.getGameById(b.getGameId());
                if (gameA == null || gameB == null) return 0;
                return gameB.getDate().compareTo(gameA.getDate());
            }
        });

        // Display list of games with player's stats
        PlayerGameStatsAdapter adapter = new PlayerGameStatsAdapter(gameStats, dataManager);
        gamesRecyclerView.setAdapter(adapter);
    }
}
