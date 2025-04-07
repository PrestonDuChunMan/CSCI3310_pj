package edu.cuhk.csci3310.basketball_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder> {

    private List<String> players;
    private OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onPlayerClick(String playerName);
    }

    public PlayerListAdapter(List<String> players, OnPlayerClickListener listener) {
        this.players = players;
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

        PlayerViewHolder(View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlayerClick(players.get(position));
                }
            });
        }

        void bind(String playerName) {
            playerNameTextView.setText(playerName);
        }
    }
}
