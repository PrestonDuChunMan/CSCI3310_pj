package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
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
//        comparisonTextView = findViewById(R.id.comparisonTextView);
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

        // Get team percentages
        int fgA = Math.round(currentGame.getTeamFGPercentage("A"));
        int fgB = Math.round(currentGame.getTeamFGPercentage("B"));
        int pt3A = Math.round(currentGame.getTeam3PTPercentage("A"));
        int pt3B = Math.round(currentGame.getTeam3PTPercentage("B"));
        int ftA = Math.round(currentGame.getTeamFTPercentage("A"));
        int ftB = Math.round(currentGame.getTeamFTPercentage("B"));

        // Update progress bars
        ProgressBar fgProgressA = findViewById(R.id.fgProgressA);
        ProgressBar fgProgressB = findViewById(R.id.fgProgressB);
        ProgressBar pt3ProgressA = findViewById(R.id.pt3ProgressA);
        ProgressBar pt3ProgressB = findViewById(R.id.pt3ProgressB);
        ProgressBar ftProgressA = findViewById(R.id.ftProgressA);
        ProgressBar ftProgressB = findViewById(R.id.ftProgressB);

        fgProgressA.setProgress(fgA);
        fgProgressB.setProgress(fgB);
        pt3ProgressA.setProgress(pt3A);
        pt3ProgressB.setProgress(pt3B);
        ftProgressA.setProgress(ftA);
        ftProgressB.setProgress(ftB);

        // (Optional) Hide the old text comparison if present
//        TextView comparisonTextView = findViewById(R.id.comparisonTextView);
        if (comparisonTextView != null) {
            comparisonTextView.setVisibility(View.GONE);
        }

        // Existing display logic for top performers remains unchanged
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
