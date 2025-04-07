package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameStatsDisplayActivity extends AppCompatActivity implements GameListAdapter.OnGameClickListener {

    private RecyclerView gamesRecyclerView;
    private GameListAdapter adapter;
    private TextView emptyStateTextView;
    private GameDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stats_display);

        initializeViews();
        dataManager = new GameDataManager(this);
        loadGames();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGames(); // Refresh the games list when returning to this activity
    }

    private void initializeViews() {
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        // Add a null check and provide a default layout manager
        if (gamesRecyclerView != null) {
            gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadGames() {
        List<Game> games = dataManager.getAllGames();

        if (games.isEmpty()) {
            if (emptyStateTextView != null) {
                emptyStateTextView.setVisibility(View.VISIBLE);
            }
            if (gamesRecyclerView != null) {
                gamesRecyclerView.setVisibility(View.GONE);
            }
        } else {
            if (emptyStateTextView != null) {
                emptyStateTextView.setVisibility(View.GONE);
            }
            if (gamesRecyclerView != null) {
                gamesRecyclerView.setVisibility(View.VISIBLE);

                adapter = new GameListAdapter(games, this);
                gamesRecyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onGameClick(Game game) {
        Intent intent = new Intent(this, GameDetailsActivity.class);
        intent.putExtra("GAME_ID", game.getId());
        startActivity(intent);
    }

    public void onAddNewGameClick(View view) {
        Intent intent = new Intent(this, PlayerStatsCounterActivity.class);
        startActivity(intent);
    }

    public void onViewPlayersClick(View view) {
        Intent intent = new Intent(this, PlayerListActivity.class);
        startActivity(intent);
    }
}
