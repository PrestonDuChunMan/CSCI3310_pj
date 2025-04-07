package edu.cuhk.csci3310.basketball_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    private List<Game> games;
    private OnGameClickListener listener;

    public interface OnGameClickListener {
        void onGameClick(Game game);
    }

    public GameListAdapter(List<Game> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameNameTextView;
        TextView gameDateTextView;
        TextView gameScoreTextView;

        GameViewHolder(View itemView) {
            super(itemView);
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView);
            gameDateTextView = itemView.findViewById(R.id.gameDateTextView);
            gameScoreTextView = itemView.findViewById(R.id.gameScoreTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onGameClick(games.get(position));
                }
            });
        }

        void bind(Game game) {
            gameNameTextView.setText(game.getName());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            gameDateTextView.setText(sdf.format(game.getDate()));

            int teamAPoints = game.getTeamPoints("A");
            int teamBPoints = game.getTeamPoints("B");
            gameScoreTextView.setText("Team A: " + teamAPoints + " - Team B: " + teamBPoints);
        }
    }
}
