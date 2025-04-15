package edu.cuhk.csci3310.basketball_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder> {

    private List<String> players;
    private GameDataManager dataManager;
    private OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onPlayerClick(String playerName);
    }

    public PlayerListAdapter(List<String> players, GameDataManager dataManager, OnPlayerClickListener listener) {
        this.players = players;
        this.dataManager = dataManager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        String playerName = players.get(position);
        holder.bind(playerName);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        TextView playerPreviewStatsTextView;

        PlayerViewHolder(View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            playerPreviewStatsTextView = itemView.findViewById(R.id.playerPreviewStatsTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlayerClick(players.get(position));
                }
            });
        }

        void bind(String playerName) {
            playerNameTextView.setText(playerName);
            GameDataManager.PlayerStats stats = dataManager.getPlayerAverageStats(playerName);
            String preview = String.format(Locale.getDefault(), "%.1f PPG | %.1f RPG | %.1f APG | %.1f%% FG | %.1f%% 3PT",
                    stats.points, stats.rebounds, stats.assists, stats.getFgPercentage(), stats.get3ptPercentage());
            playerPreviewStatsTextView.setText(preview);
        }
    }
}
