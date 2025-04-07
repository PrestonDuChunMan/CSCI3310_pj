package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerListActivity extends AppCompatActivity implements PlayerListAdapter.OnPlayerClickListener {

    private RecyclerView playersRecyclerView;
    private TextView emptyStateTextView;
    private GameDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        initializeViews();
        dataManager = new GameDataManager(this);
        loadPlayers();
    }

    private void initializeViews() {
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Add a null check
        if (playersRecyclerView != null) {
            playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadPlayers() {
        // Get players from the player pool
        SharedPreferences prefs = getSharedPreferences("PlayerPool", MODE_PRIVATE);
        Set<String> playerPool = new HashSet<>(prefs.getStringSet("players", new HashSet<>()));

        // Get players from game records (might have players not in the pool)
        Map<String, List<GameDataManager.PlayerGameStats>> playerStats = dataManager.getAllPlayerStats();
        Set<String> allPlayers = new HashSet<>(playerPool);
        allPlayers.addAll(playerStats.keySet());

        List<String> playersList = new ArrayList<>(allPlayers);

        if (playersList.isEmpty()) {
            if (emptyStateTextView != null) {
                emptyStateTextView.setVisibility(View.VISIBLE);
            }
            if (playersRecyclerView != null) {
                playersRecyclerView.setVisibility(View.GONE);
            }
        } else {
            if (emptyStateTextView != null) {
                emptyStateTextView.setVisibility(View.GONE);
            }
            if (playersRecyclerView != null) {
                playersRecyclerView.setVisibility(View.VISIBLE);
                PlayerListAdapter adapter = new PlayerListAdapter(playersList, this);
                playersRecyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onPlayerClick(String playerName) {
        Intent intent = new Intent(this, PlayerStatsActivity.class);
        intent.putExtra("PLAYER_NAME", playerName);
        startActivity(intent);
    }
}
