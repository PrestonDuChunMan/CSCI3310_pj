package edu.cuhk.csci3310.basketball_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PlayerGameStatsAdapter extends RecyclerView.Adapter<PlayerGameStatsAdapter.GameStatsViewHolder> {

    private List<GameDataManager.PlayerGameStats> gameStats;
    private GameDataManager dataManager;

    public PlayerGameStatsAdapter(List<GameDataManager.PlayerGameStats> gameStats, GameDataManager dataManager) {
        this.gameStats = gameStats;
        this.dataManager = dataManager;
    }

    @NonNull
    @Override
    public GameStatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_game_history, parent, false);
        return new GameStatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameStatsViewHolder holder, int position) {
        GameDataManager.PlayerGameStats stats = gameStats.get(position);
        holder.bind(stats);
    }

    @Override
    public int getItemCount() {
        return gameStats.size();
    }

    class GameStatsViewHolder extends RecyclerView.ViewHolder {
        TextView gameNameTextView;
        TextView statsTextView;

        GameStatsViewHolder(View itemView) {
            super(itemView);
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView);
            statsTextView = itemView.findViewById(R.id.statsTextView);
        }

        void bind(GameDataManager.PlayerGameStats stats) {
            // Get game name
            Game game = dataManager.getGameById(stats.getGameId());
            String gameName = game != null ? game.getName() : "Game " + stats.getGameId();
            gameNameTextView.setText(gameName + " (Team " + stats.getTeam() + ")");

            // Format stats string
            String statsText = String.format(Locale.getDefault(),
                    "%d PTS (%d/%d FG, %d/%d 3PT, %d/%d FT), %d REB, %d AST, %d STL, %d BLK, %d TO",
                    stats.getPoints(),
                    stats.getMadeFG(), stats.getFga(),
                    stats.getMade3PT(), stats.getTpta(),
                    stats.getMadeFT(), stats.getFta(),
                    stats.getRebounds(),
                    stats.getAssists(),
                    stats.getSteals(),
                    stats.getBlocks(),
                    stats.getTurnovers());

            statsTextView.setText(statsText);
        }
    }
}
