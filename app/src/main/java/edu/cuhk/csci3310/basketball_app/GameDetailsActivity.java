package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class GameDetailsActivity extends AppCompatActivity implements GameStatsAdapter.OnPlayerClickListener {

    private TextView gameNameTextView;
    private TextView comparisonTextView;
    private TextView topPerformersTextView;
    private RecyclerView playersRecyclerView;
    private Button teamAButton, teamBButton;

    private GameDataManager dataManager;
    private Game currentGame;
    private String currentTeam = "A";
    private GameStatsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        long gameId = getIntent().getLongExtra("GAME_ID", -1);
        if (gameId == -1) {
            finish();
            return;
        }

        initializeViews();

        dataManager = new GameDataManager(this);
        currentGame = dataManager.getGameById(gameId);

        if (currentGame == null) {
            finish();
            return;
        }

        displayGameDetails();
        switchTeam("A"); // Start with Team A

        // Set up scroll synchronization
        setupSynchronizedScrolling();
    }

    private void initializeViews() {
        gameNameTextView = findViewById(R.id.gameNameTextView);
        comparisonTextView = findViewById(R.id.comparisonTextView);
        topPerformersTextView = findViewById(R.id.topPerformersTextView);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        teamAButton = findViewById(R.id.teamAButton);
        teamBButton = findViewById(R.id.teamBButton);

        teamAButton.setOnClickListener(v -> switchTeam("A"));
        teamBButton.setOnClickListener(v -> switchTeam("B"));

        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSynchronizedScrolling() {
        HorizontalScrollView headerScrollView = findViewById(R.id.statsHeaderScrollView);

        // Make header scroll sync with player rows
        if (adapter != null && headerScrollView != null) {
            // Make individual items scroll when header scrolls
            headerScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                // Find all visible view holders and sync their scroll position
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    RecyclerView.ViewHolder holder = playersRecyclerView.findViewHolderForAdapterPosition(i);
                    if (holder != null && holder.itemView instanceof ViewGroup) {
                        View scrollView = holder.itemView.findViewById(R.id.playerStatsScrollView);
                        if (scrollView instanceof HorizontalScrollView) {
                            ((HorizontalScrollView) scrollView).scrollTo(scrollX, 0);
                        }
                    }
                }
            });
        }
    }

    private void displayGameDetails() {
        gameNameTextView.setText(currentGame.getName());

        // Display team comparison
        StringBuilder comparison = new StringBuilder();

        // Field Goals comparison
        comparison.append("Field Goals\n");
        comparison.append(String.format(Locale.getDefault(), "Team A: %d/%d (%.1f%%)\n",
                currentGame.getTeamMadeFG("A"),
                currentGame.getTeamFGA("A"),
                currentGame.getTeamFGPercentage("A")));

        comparison.append(String.format(Locale.getDefault(), "Team B: %d/%d (%.1f%%)\n\n",
                currentGame.getTeamMadeFG("B"),
                currentGame.getTeamFGA("B"),
                currentGame.getTeamFGPercentage("B")));

        // 3-Pointers comparison
        comparison.append("3-Pointers\n");
        comparison.append(String.format(Locale.getDefault(), "Team A: %d/%d (%.1f%%)\n",
                currentGame.getTeamMade3PT("A"),
                currentGame.getTeam3PTA("A"),
                currentGame.getTeam3PTPercentage("A")));

        comparison.append(String.format(Locale.getDefault(), "Team B: %d/%d (%.1f%%)\n\n",
                currentGame.getTeamMade3PT("B"),
                currentGame.getTeam3PTA("B"),
                currentGame.getTeam3PTPercentage("B")));

        // Free Throws comparison
        comparison.append("Free Throws\n");
        comparison.append(String.format(Locale.getDefault(), "Team A: %d/%d (%.1f%%)\n",
                currentGame.getTeamMadeFT("A"),
                currentGame.getTeamFTA("A"),
                currentGame.getTeamFTPercentage("A")));

        comparison.append(String.format(Locale.getDefault(), "Team B: %d/%d (%.1f%%)\n\n",
                currentGame.getTeamMadeFT("B"),
                currentGame.getTeamFTA("B"),
                currentGame.getTeamFTPercentage("B")));

        // Other stats comparison
        comparison.append(String.format(Locale.getDefault(), "Rebounds: Team A %d - Team B %d\n",
                currentGame.getTeamRebounds("A"),
                currentGame.getTeamRebounds("B")));

        comparison.append(String.format(Locale.getDefault(), "Assists: Team A %d - Team B %d\n",
                currentGame.getTeamAssists("A"),
                currentGame.getTeamAssists("B")));

        comparison.append(String.format(Locale.getDefault(), "Steals: Team A %d - Team B %d\n",
                currentGame.getTeamSteals("A"),
                currentGame.getTeamSteals("B")));

        comparison.append(String.format(Locale.getDefault(), "Blocks: Team A %d - Team B %d\n",
                currentGame.getTeamBlocks("A"),
                currentGame.getTeamBlocks("B")));

        comparison.append(String.format(Locale.getDefault(), "Turnovers: Team A %d - Team B %d",
                currentGame.getTeamTurnovers("A"),
                currentGame.getTeamTurnovers("B")));

        comparisonTextView.setText(comparison.toString());

        // Display top performers
        StringBuilder topPerformers = new StringBuilder("Top Performers\n\n");

        Player teamATop = currentGame.getTopPerformer("A");
        Player teamBTop = currentGame.getTopPerformer("B");

        if (teamATop != null) {
            topPerformers.append(String.format(Locale.getDefault(),
                    "Team A: %s - %d pts, %d reb, %d ast\n",
                    teamATop.getName(),
                    teamATop.getPoints(),
                    teamATop.getRebounds(),
                    teamATop.getAssists()));
        }

        if (teamBTop != null) {
            topPerformers.append(String.format(Locale.getDefault(),
                    "Team B: %s - %d pts, %d reb, %d ast",
                    teamBTop.getName(),
                    teamBTop.getPoints(),
                    teamBTop.getRebounds(),
                    teamBTop.getAssists()));
        }

        topPerformersTextView.setText(topPerformers.toString());
    }

    private void switchTeam(String team) {
        currentTeam = team;
        if (team.equals("A")) {
            // Update button appearances for Team A selection
            teamAButton.setEnabled(false);
            teamBButton.setEnabled(true);

            // Team A active appearance (black background, white text)
            teamAButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
            teamAButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            // Team B inactive appearance (light grey background, black text)
            teamBButton.setBackgroundColor(Color.parseColor("#F0F0F0"));
            teamBButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));

            adapter = new GameStatsAdapter(currentGame.getTeamAPlayers(), this);
        } else {
            // Update button appearances for Team B selection
            teamAButton.setEnabled(true);
            teamBButton.setEnabled(false);

            // Team A inactive appearance (light grey background, black text)
            teamAButton.setBackgroundColor(Color.parseColor("#F0F0F0"));
            teamAButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));

            // Team B active appearance (black background, white text)
            teamBButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
            teamBButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            adapter = new GameStatsAdapter(currentGame.getTeamBPlayers(), this);
        }

        playersRecyclerView.setAdapter(adapter);
        setupSynchronizedScrolling(); // Re-setup scroll synchronization when team changes
    }

    @Override
    public void onPlayerClick(Player player) {
        Intent intent = new Intent(this, PlayerStatsActivity.class);
        intent.putExtra("PLAYER_NAME", player.getName());
        startActivity(intent);
    }
}
