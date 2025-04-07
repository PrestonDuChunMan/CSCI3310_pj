package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

public class PlayerStatsCounterActivity extends AppCompatActivity {

    private EditText gameNameEditText;
    private RecyclerView playersRecyclerView;
    private PlayerStatsAdapter adapter;
    private Button teamAButton, teamBButton;
    private TextView teamTotalsTextView;
    private Button addPlayerButton, saveGameButton;
    private TextView teamANameScoreTextView, teamAScoreTextView;
    private TextView teamBNameScoreTextView, teamBScoreTextView;

    private List<Player> teamAPlayers = new ArrayList<>();
    private List<Player> teamBPlayers = new ArrayList<>();
    private List<Player> currentTeamPlayers;
    private Set<String> playerPool = new HashSet<>();
    private String currentTeam = "A";
    private Game currentGame;

    private Stack<StatsAction> undoStack = new Stack<>();
    private Stack<StatsAction> redoStack = new Stack<>();

    private Handler buttonAnimationHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats_counter);

        initializeViews();
        setupListeners();
        loadPlayerPool();
        initializeGame();
        currentTeamPlayers = teamAPlayers;
        updateUI();

        // Set up scroll synchronization
        setupSynchronizedScrolling();
    }

    private void setupSynchronizedScrolling() {
        HorizontalScrollView headerScrollView = findViewById(R.id.statsHeaderScrollView);

        // Attach the header to the adapter for synchronization
        adapter.setHeaderScrollView(headerScrollView);

        // Make header scroll sync with player rows
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

    private void initializeViews() {
        gameNameEditText = findViewById(R.id.gameNameEditText);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        teamAButton = findViewById(R.id.teamAButton);
        teamBButton = findViewById(R.id.teamBButton);
//        teamTotalsTextView = findViewById(R.id.teamTotalsTextView);
        addPlayerButton = findViewById(R.id.addPlayerButton);
        saveGameButton = findViewById(R.id.saveGameButton);
        Button undoButton = findViewById(R.id.undoButton);
//        Button redoButton = findViewById(R.id.redoButton);

        // Score display views
        teamANameScoreTextView = findViewById(R.id.teamANameScoreTextView);
        teamAScoreTextView = findViewById(R.id.teamAScoreTextView);
        teamBNameScoreTextView = findViewById(R.id.teamBNameScoreTextView);
        teamBScoreTextView = findViewById(R.id.teamBScoreTextView);

        // Setup RecyclerView
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayerStatsAdapter(currentTeamPlayers, this::onStatIncrement, this::animateButtonPress);
        playersRecyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        teamAButton.setOnClickListener(v -> switchTeam("A"));
        teamBButton.setOnClickListener(v -> switchTeam("B"));

        addPlayerButton.setOnClickListener(v -> showAddPlayerDialog());

        saveGameButton.setOnClickListener(v -> saveGame());

        findViewById(R.id.undoButton).setOnClickListener(v -> undoAction());
//        findViewById(R.id.redoButton).setOnClickListener(v -> redoAction());
    }

    private void initializeGame() {
        // Generate default game name with date and auto-increment number
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        SharedPreferences prefs = getSharedPreferences("GameCounter", MODE_PRIVATE);
        int gameCount = prefs.getInt("gameCount", 0) + 1;

        String defaultName = currentDate + " Game " + gameCount;
        gameNameEditText.setText(defaultName);

        // Create new game object
        currentGame = new Game(defaultName, new ArrayList<>(teamAPlayers), new ArrayList<>(teamBPlayers));

        // Initialize team names in score display
        teamANameScoreTextView.setText("Team A");
        teamBNameScoreTextView.setText("Team B");

        // Set initial team button appearance (Team A selected by default)
        teamAButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
        teamAButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        teamBButton.setBackgroundColor(Color.parseColor("#F0F0F0"));
        teamBButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));

        updateTeamScores();
    }

    private void loadPlayerPool() {
        SharedPreferences prefs = getSharedPreferences("PlayerPool", MODE_PRIVATE);
        playerPool = prefs.getStringSet("players", new HashSet<>());
    }

    private void savePlayerPool() {
        SharedPreferences prefs = getSharedPreferences("PlayerPool", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("players", playerPool);
        editor.apply();
    }

    private void switchTeam(String team) {
        currentTeam = team;
        if (team.equals("A")) {
            currentTeamPlayers = teamAPlayers;

            // Update button appearances for Team A selection
            teamAButton.setEnabled(false);
            teamBButton.setEnabled(true);

            // Team A active appearance (black background, white text)
            teamAButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
            teamAButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            // Team B inactive appearance (light grey background, black text)
            teamBButton.setBackgroundColor(Color.parseColor("#F0F0F0"));
            teamBButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        } else {
            currentTeamPlayers = teamBPlayers;

            // Update button appearances for Team B selection
            teamAButton.setEnabled(true);
            teamBButton.setEnabled(false);

            // Team A inactive appearance (light grey background, black text)
            teamAButton.setBackgroundColor(Color.parseColor("#F0F0F0"));
            teamAButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));

            // Team B active appearance (black background, white text)
            teamBButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
            teamBButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
        adapter.updatePlayers(currentTeamPlayers);
        updateTeamTotals();
    }

    private void showAddPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Player");

        final EditText input = new EditText(this);
        builder.setView(input);

        // If we have existing players in the pool, show them in a selectable list
        if (!playerPool.isEmpty()) {
            String[] playerNames = playerPool.toArray(new String[0]);
            builder.setItems(playerNames, (dialog, which) -> {
                String selectedName = playerNames[which];
                addPlayer(selectedName);
            });
        }

        builder.setPositiveButton("Add New", (dialog, which) -> {
            String playerName = input.getText().toString().trim();
            if (!playerName.isEmpty()) {
                playerPool.add(playerName);
                savePlayerPool();
                addPlayer(playerName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addPlayer(String name) {
        Player newPlayer = new Player(name);
        if (currentTeam.equals("A")) {
            teamAPlayers.add(newPlayer);
        } else {
            teamBPlayers.add(newPlayer);
        }

        // Make sure the player name is added to the pool and saved
        playerPool.add(name);
        savePlayerPool();

        adapter.notifyDataSetChanged();
        updateTeamTotals();
    }

    private void onStatIncrement(Player player, String statType, int value) {
        // Save for undo
        StatsAction action = new StatsAction(player, statType, value);
        undoStack.push(action);
        redoStack.clear();

        // Update stats based on type
        switch (statType) {
            case "madeFG":
                player.setMadeFG(player.getMadeFG() + value);
                player.setPoints(player.getPoints() + (2 * value)); // 2 points per FG
                break;
            case "missedFG":
                player.setMissedFG(player.getMissedFG() + value);
                break;
            case "made3PT":
                player.setMade3PT(player.getMade3PT() + value);
                // Also count as field goals made
                player.setMadeFG(player.getMadeFG() + value);
                player.setPoints(player.getPoints() + (3 * value)); // 3 points per 3PT
                break;
            case "missed3PT":
                player.setMissed3PT(player.getMissed3PT() + value);
                // Also count as field goals missed
                player.setMissedFG(player.getMissedFG() + value);
                break;
            case "madeFT":
                player.setMadeFT(player.getMadeFT() + value);
                player.setPoints(player.getPoints() + value); // 1 point per FT
                break;
            case "missedFT":
                player.setMissedFT(player.getMissedFT() + value);
                break;
            case "rebounds":
                player.setRebounds(player.getRebounds() + value);
                break;
            case "assists":
                player.setAssists(player.getAssists() + value);
                break;
            case "steals":
                player.setSteals(player.getSteals() + value);
                break;
            case "blocks":
                player.setBlocks(player.getBlocks() + value);
                break;
            case "turnovers":
                player.setTurnovers(player.getTurnovers() + value);
                break;
        }

        // Update UI
        adapter.notifyDataSetChanged();
        updateTeamTotals();
        updateTeamScores();
    }

    private void animateButtonPress(Button button) {
        // Store the original background tint
//        final ColorStateList originalTint = button.getBackgroundTintList();
//
//        // Change to green tint instead of replacing the background
//        button.setBackgroundTintList(ColorStateList.valueOf(
//                ContextCompat.getColor(this, android.R.color.holo_green_light)));
//
//        // Schedule return to original tint after 3 seconds
//        buttonAnimationHandler.removeCallbacksAndMessages(button);
//        buttonAnimationHandler.postDelayed(() -> {
//            // Restore the original tint
//            button.setBackgroundTintList(originalTint);
//        }, 2000);
    }

    private void undoAction() {
        if (undoStack.isEmpty()) return;

        StatsAction action = undoStack.pop();
        redoStack.push(action);

        // Reverse the action by passing negative value
        onStatIncrement(action.getPlayer(), action.getStatType(), -action.getValue());

        // Remove the undo entry created by the reverse action
        if (!undoStack.isEmpty()) {
            undoStack.pop();
        }
    }

//    private void redoAction() {
//        if (redoStack.isEmpty()) return;
//
//        StatsAction action = redoStack.pop();
//
//        // Apply the action again
//        onStatIncrement(action.getPlayer(), action.getStatType(), action.getValue());
//    }

    private void updateTeamTotals() {
        int totalPoints = 0;
        int totalMadeFG = 0;
        int totalMissedFG = 0;
        int totalFGA = 0;
        int totalMade3PT = 0;
        int totalMissed3PT = 0;
        int total3PTA = 0;
        int totalMadeFT = 0;
        int totalMissedFT = 0;
        int totalFTA = 0;
        int totalRebounds = 0;
        int totalAssists = 0;
        int totalSteals = 0;
        int totalBlocks = 0;
        int totalTurnovers = 0;

        for (Player player : currentTeamPlayers) {
            totalPoints += player.getPoints();
            totalMadeFG += player.getMadeFG();
            totalMissedFG += player.getMissedFG();
            totalFGA = totalMadeFG + totalMissedFG; // FGA is sum of makes and misses
            totalMade3PT += player.getMade3PT();
            totalMissed3PT += player.getMissed3PT();
            total3PTA = totalMade3PT + totalMissed3PT; // 3PTA is sum of 3PT makes and misses
            totalMadeFT += player.getMadeFT();
            totalMissedFT += player.getMissedFT();
            totalFTA = totalMadeFT + totalMissedFT; // FTA is sum of FT makes and misses
            totalRebounds += player.getRebounds();
            totalAssists += player.getAssists();
            totalSteals += player.getSteals();
            totalBlocks += player.getBlocks();
            totalTurnovers += player.getTurnovers();
        }

        String fgPercentage = totalFGA > 0 ? String.format(Locale.getDefault(), "%.1f%%", (totalMadeFG * 100.0 / totalFGA)) : "0.0%";
        String tptPercentage = total3PTA > 0 ? String.format(Locale.getDefault(), "%.1f%%", (totalMade3PT * 100.0 / total3PTA)) : "0.0%";
        String ftPercentage = totalFTA > 0 ? String.format(Locale.getDefault(), "%.1f%%", (totalMadeFT * 100.0 / totalFTA)) : "0.0%";

        String totals = "Team " + currentTeam + " Totals\n" +
                "PTS: " + totalPoints + " | " +
                "FG: " + totalMadeFG + "/" + totalFGA + " (" + fgPercentage + ") | " +
                "3PT: " + totalMade3PT + "/" + total3PTA + " (" + tptPercentage + ") | " +
                "FT: " + totalMadeFT + "/" + totalFTA + " (" + ftPercentage + ") | " +
                "REB: " + totalRebounds + " | " +
                "AST: " + totalAssists + " | " +
                "STL: " + totalSteals + " | " +
                "BLK: " + totalBlocks + " | " +
                "TO: " + totalTurnovers;

//        teamTotalsTextView.setText(totals);
    }

    private void updateTeamScores() {
        int teamAScore = calculateTeamScore(teamAPlayers);
        int teamBScore = calculateTeamScore(teamBPlayers);

        teamAScoreTextView.setText(String.valueOf(teamAScore));
        teamBScoreTextView.setText(String.valueOf(teamBScore));
    }

    private int calculateTeamScore(List<Player> players) {
        int totalScore = 0;
        for (Player player : players) {
            totalScore += player.getPoints();
        }
        return totalScore;
    }

    private void saveGame() {
        String gameName = gameNameEditText.getText().toString().trim();
        if (gameName.isEmpty()) {
            Toast.makeText(this, "Please enter a game name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update game object
        currentGame.setName(gameName);
        currentGame.setTeamAPlayers(new ArrayList<>(teamAPlayers));
        currentGame.setTeamBPlayers(new ArrayList<>(teamBPlayers));

        // Save game to database or shared preferences
        GameDataManager dataManager = new GameDataManager(this);
        dataManager.saveGame(currentGame);

        // Update game counter
        SharedPreferences prefs = getSharedPreferences("GameCounter", MODE_PRIVATE);
        int gameCount = prefs.getInt("gameCount", 0) + 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("gameCount", gameCount);
        editor.apply();

        Toast.makeText(this, "Game stats saved!", Toast.LENGTH_SHORT).show();

        // Navigate to game stats display
        Intent intent = new Intent(this, GameStatsDisplayActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI() {
        adapter.updatePlayers(currentTeamPlayers);
        updateTeamTotals();
        updateTeamScores();
    }

    private static class StatsAction {
        private Player player;
        private String statType;
        private int value;

        public StatsAction(Player player, String statType, int value) {
            this.player = player;
            this.statType = statType;
            this.value = value;
        }

        public Player getPlayer() {
            return player;
        }

        public String getStatType() {
            return statType;
        }

        public int getValue() {
            return value;
        }
    }
}
