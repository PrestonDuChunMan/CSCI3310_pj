package edu.cuhk.csci3310.basketball_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class GameStatsAdapter extends RecyclerView.Adapter<GameStatsAdapter.StatsViewHolder> {
    private List<Player> players;
    private OnPlayerClickListener listener;
    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    public GameStatsAdapter(List<Player> players, OnPlayerClickListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_game_stats_scrollable, parent, false);
        return new StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        Player player = players.get(position);
        holder.bind(player);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class StatsViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        HorizontalScrollView playerStatsScrollView;
        TextView pointsTextView, fgTextView, fgPercentageTextView, tptTextView, tptPercentageTextView,
                ftTextView, ftPercentageTextView, reboundsTextView, assistsTextView, stealsTextView,
                blocksTextView, turnoversTextView;

        StatsViewHolder(View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            playerStatsScrollView = itemView.findViewById(R.id.playerStatsScrollView);
            pointsTextView = itemView.findViewById(R.id.pointsTextView);
            fgTextView = itemView.findViewById(R.id.fgTextView);
            fgPercentageTextView = itemView.findViewById(R.id.fgPercentageTextView);
            tptTextView = itemView.findViewById(R.id.tptTextView);
            tptPercentageTextView = itemView.findViewById(R.id.tptPercentageTextView);
            ftTextView = itemView.findViewById(R.id.ftTextView);
            ftPercentageTextView = itemView.findViewById(R.id.ftPercentageTextView);
            reboundsTextView = itemView.findViewById(R.id.reboundsTextView);
            assistsTextView = itemView.findViewById(R.id.assistsTextView);
            stealsTextView = itemView.findViewById(R.id.stealsTextView);
            blocksTextView = itemView.findViewById(R.id.blocksTextView);
            turnoversTextView = itemView.findViewById(R.id.turnoversTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlayerClick(players.get(position));
                }
            });
        }

        void bind(Player player) {
            playerNameTextView.setText(player.getName());
            pointsTextView.setText(String.valueOf(player.getPoints()));
            String fgString = player.getMadeFG() + "/" + player.getFGA();
            fgTextView.setText(fgString);
            fgPercentageTextView.setText(String.format(Locale.getDefault(), "%.1f%%", player.getFGPercentage()));
            String tptString = player.getMade3PT() + "/" + player.get3PTA();
            tptTextView.setText(tptString);
            tptPercentageTextView.setText(String.format(Locale.getDefault(), "%.1f%%", player.get3PTPercentage()));
            String ftString = player.getMadeFT() + "/" + player.getFTA();
            ftTextView.setText(ftString);
            ftPercentageTextView.setText(String.format(Locale.getDefault(), "%.1f%%", player.getFTPercentage()));
            reboundsTextView.setText(String.valueOf(player.getRebounds()));
            assistsTextView.setText(String.valueOf(player.getAssists()));
            stealsTextView.setText(String.valueOf(player.getSteals()));
            blocksTextView.setText(String.valueOf(player.getBlocks()));
            turnoversTextView.setText(String.valueOf(player.getTurnovers()));
        }
    }
}
