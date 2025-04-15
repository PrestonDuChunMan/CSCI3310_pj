package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.ItemTouchHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class GameStatsDisplayActivity extends AppCompatActivity implements GameListAdapter.OnGameClickListener {

    private EditText searchGameEditText;
    private RecyclerView gamesRecyclerView;
    private GameListAdapter adapter;
    private TextView emptyStateTextView;
    private GameDataManager dataManager;
    private List<Game> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stats_display);

        searchGameEditText = findViewById(R.id.searchGameEditText);
        initializeViews();
        dataManager = new GameDataManager(this);
        loadGames();
        setupSwipeActions();
        setupSearch();
    }

    private void setupSearch() {
        searchGameEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(android.text.Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Game> filtered = SearchFilter.filterGames(games, s.toString());
                adapter.updateGames(filtered);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGames();
    }

    private void initializeViews() {
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        if (gamesRecyclerView != null) {
            gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadGames() {
        games = dataManager.getAllGames();

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

    private void setupSwipeActions() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (position != RecyclerView.NO_POSITION && position < games.size()) {
                    Game swipedGame = games.get(position);

                    if (direction == ItemTouchHelper.LEFT) {
                        deleteGame(swipedGame, position);
                    } else {
                        showEditNameDialog(swipedGame);
                    }
                }
            }

            @Override
            public void onChildDraw(android.graphics.Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
//set swipe color
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX < 0) {
                        android.graphics.drawable.ColorDrawable background =
                                new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#FF5252"));
                        background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(),
                                itemView.getRight(), itemView.getBottom());
                        background.draw(c);
                    }
                    else if (dX > 0) {
                        android.graphics.drawable.ColorDrawable background =
                                new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#2196F3"));
                        background.setBounds(itemView.getLeft(), itemView.getTop(),
                                itemView.getLeft() + (int)dX, itemView.getBottom());
                        background.draw(c);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(gamesRecyclerView);
    }

    private void deleteGame(final Game game, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Game")
                .setMessage("Delete game record, update players stats")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Game deletedGame = game;
                        dataManager.deleteGame(game.getId());
                        games.remove(position);
                        adapter.notifyItemRemoved(position);
                        if (games.isEmpty()) {
                            emptyStateTextView.setVisibility(View.VISIBLE);
                            gamesRecyclerView.setVisibility(View.GONE);
                        }

                        Snackbar snackbar = Snackbar.make(gamesRecyclerView,
                                "Game deleted", Snackbar.LENGTH_LONG);

                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dataManager.saveGame(deletedGame);
                                loadGames();
                                emptyStateTextView.setVisibility(View.GONE);
                                gamesRecyclerView.setVisibility(View.VISIBLE);
                            }
                        });

                        snackbar.show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemChanged(position);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showEditNameDialog(final Game game) {
        final EditText input = new EditText(this);
        input.setText(game.getName());
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(this)
                .setTitle("Edit Game Name")
                .setView(input)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            dataManager.updateGameName(game.getId(), newName);
                            loadGames();

                            Toast.makeText(GameStatsDisplayActivity.this,
                                    "Game name updated", Toast.LENGTH_SHORT).show();
                        } else {
                            // Name cannot be empty, revert changes
                            Toast.makeText(GameStatsDisplayActivity.this,
                                    "Game name cannot be empty", Toast.LENGTH_SHORT).show();
                            loadGames();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadGames();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onGameClick(Game game) {//directs to game details
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
