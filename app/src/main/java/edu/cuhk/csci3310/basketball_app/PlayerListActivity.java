package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;

public class PlayerListActivity extends AppCompatActivity implements PlayerListAdapter.OnPlayerClickListener {
    private RecyclerView playersRecyclerView;
    private TextView emptyStateTextView;
    private GameDataManager dataManager;
    private List<String> playersList;
    private Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        initializeViews();
        dataManager = new GameDataManager(this);
        loadPlayers();
        setupSort();
    }

    private void initializeViews() {
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        sortSpinner = findViewById(R.id.sortSpinner);
        if (playersRecyclerView != null) {
            playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setupSort() {
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (playersList != null) {
                    switch (position) {
                        case 0: // Most PPG
                            Collections.sort(playersList, (a, b) -> {
                                GameDataManager.PlayerStats statsA = dataManager.getPlayerAverageStats(a);
                                GameDataManager.PlayerStats statsB = dataManager.getPlayerAverageStats(b);
                                return Float.compare(statsB.points, statsA.points);
                            });
                            break;
                        case 1: // Best FG%
                            Collections.sort(playersList, (a, b) -> {
                                GameDataManager.PlayerStats statsA = dataManager.getPlayerAverageStats(a);
                                GameDataManager.PlayerStats statsB = dataManager.getPlayerAverageStats(b);
                                return Float.compare(statsB.getFgPercentage(), statsA.getFgPercentage());
                            });
                            break;
                        case 2: // Best 3PT%
                            Collections.sort(playersList, (a, b) -> {
                                GameDataManager.PlayerStats statsA = dataManager.getPlayerAverageStats(a);
                                GameDataManager.PlayerStats statsB = dataManager.getPlayerAverageStats(b);
                                return Float.compare(statsB.get3ptPercentage(), statsA.get3ptPercentage());
                            });
                            break;
                    }
                    updateAdapter();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
//    to-do: change load player method DONE changed it so it can get all player names shown

    private void loadPlayers() {
        SharedPreferences prefs = getSharedPreferences("PlayerPool", MODE_PRIVATE);
        Set<String> playerPool = new HashSet<>(prefs.getStringSet("players", new HashSet<>()));
        Map<String, java.util.List<GameDataManager.PlayerGameStats>> playerStats = dataManager.getAllPlayerStats();
        Set<String> allPlayers = new HashSet<>(playerPool);
        allPlayers.addAll(playerStats.keySet());
        playersList = new ArrayList<>(allPlayers);
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
            }
            updateAdapter();
        }
    }

    private void updateAdapter() {
        PlayerListAdapter adapter = new PlayerListAdapter(playersList, dataManager, PlayerListActivity.this);
        playersRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onPlayerClick(String playerName) {
        Intent intent = new Intent(this, PlayerStatsActivity.class);
        intent.putExtra("PLAYER_NAME", playerName);
        startActivity(intent);
    }
}
