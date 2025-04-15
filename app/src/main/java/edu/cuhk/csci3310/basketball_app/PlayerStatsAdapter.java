package edu.cuhk.csci3310.basketball_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayerStatsAdapter extends RecyclerView.Adapter<PlayerStatsAdapter.PlayerViewHolder> {

    private List<Player> players;
    private final StatIncrementListener statIncrementListener;
    private final ButtonAnimationListener buttonAnimationListener;
    private final DecimalFormat percentFormat = new DecimalFormat("0.0%");
    private HorizontalScrollView headerScrollView;
    private boolean isUserScrolling = false;

    // Interface for handling stat increments
    public interface StatIncrementListener {
        void onClick(Player player, String statType, int value);
    }

    // Interface for handling button animation
    public interface ButtonAnimationListener {
        void onButtonPressed(Button button);
    }

    public PlayerStatsAdapter(List<Player> players, StatIncrementListener listener,
                              ButtonAnimationListener animationListener) {
        this.players = players;
        this.statIncrementListener = listener;
        this.buttonAnimationListener = animationListener;
    }

    public void updatePlayers(List<Player> newPlayers) {
        this.players = newPlayers;
        notifyDataSetChanged();
    }

    /**
     * Set the header scroll view for synchronized scrolling
     */
    public void setHeaderScrollView(HorizontalScrollView headerScrollView) {
        this.headerScrollView = headerScrollView;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_stats, parent, false);
        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.bind(player);

        // Synchronize scroll state with header initially
        if (headerScrollView != null) {
            holder.playerStatsScrollView.post(() -> {
                holder.playerStatsScrollView.scrollTo(headerScrollView.getScrollX(), 0);
            });
        }

        // Set scroll listener for this row
        holder.playerStatsScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!isUserScrolling) {
                isUserScrolling = true;

                // Synchronize header
                if (headerScrollView != null) {
                    headerScrollView.scrollTo(scrollX, 0);
                }

                // Synchronize all other visible rows
                for (int i = 0; i < getItemCount(); i++) {
                    if (i != position) {
                        PlayerViewHolder otherHolder = (PlayerViewHolder)
                                ((RecyclerView) holder.itemView.getParent()).findViewHolderForAdapterPosition(i);
                        if (otherHolder != null) {
                            otherHolder.playerStatsScrollView.scrollTo(scrollX, 0);
                        }
                    }
                }

                isUserScrolling = false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {
        private final TextView playerNameTextView;
        private final HorizontalScrollView playerStatsScrollView;

        // Stats TextViews
        private final TextView pointsTextView;
        private final TextView madeFGTextView;
        private final TextView missedFGTextView;
        private final TextView fgaTextView;
        private final TextView fgPercentageTextView;
        private final TextView made3PTTextView;
        private final TextView missed3PTTextView;
        private final TextView tptaTextView;
        private final TextView tptPercentageTextView;
        private final TextView madeFTTextView;
        private final TextView missedFTTextView;
        private final TextView ftaTextView;
        private final TextView ftPercentageTextView;
        private final TextView reboundsTextView;
        private final TextView assistsTextView;
        private final TextView stealsTextView;
        private final TextView blocksTextView;
        private final TextView turnoversTextView;

        // Increment Buttons
        private final Button madeFGButton;
        private final Button missedFGButton;
        private final Button made3PTButton;
        private final Button missed3PTButton;
        private final Button madeFTButton;
        private final Button missedFTButton;
        private final Button reboundsButton;
        private final Button assistsButton;
        private final Button stealsButton;
        private final Button blocksButton;
        private final Button turnoversButton;
        private final Button deletePlayerButton;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            playerStatsScrollView = itemView.findViewById(R.id.playerStatsScrollView);

            // Stat values
            pointsTextView = itemView.findViewById(R.id.pointsTextView);
            madeFGTextView = itemView.findViewById(R.id.madeFGTextView);
            missedFGTextView = itemView.findViewById(R.id.missedFGTextView);
            fgaTextView = itemView.findViewById(R.id.fgaTextView);
            fgPercentageTextView = itemView.findViewById(R.id.fgPercentageTextView);
            made3PTTextView = itemView.findViewById(R.id.made3PTTextView);
            missed3PTTextView = itemView.findViewById(R.id.missed3PTTextView);
            tptaTextView = itemView.findViewById(R.id.tptaTextView);
            tptPercentageTextView = itemView.findViewById(R.id.tptPercentageTextView);
            madeFTTextView = itemView.findViewById(R.id.madeFTTextView);
            missedFTTextView = itemView.findViewById(R.id.missedFTTextView);
            ftaTextView = itemView.findViewById(R.id.ftaTextView);
            ftPercentageTextView = itemView.findViewById(R.id.ftPercentageTextView);
            reboundsTextView = itemView.findViewById(R.id.reboundsTextView);
            assistsTextView = itemView.findViewById(R.id.assistsTextView);
            stealsTextView = itemView.findViewById(R.id.stealsTextView);
            blocksTextView = itemView.findViewById(R.id.blocksTextView);
            turnoversTextView = itemView.findViewById(R.id.turnoversTextView);

            // Increment buttons
            madeFGButton = itemView.findViewById(R.id.madeFGButton);
            missedFGButton = itemView.findViewById(R.id.missedFGButton);
            made3PTButton = itemView.findViewById(R.id.made3PTButton);
            missed3PTButton = itemView.findViewById(R.id.missed3PTButton);
            madeFTButton = itemView.findViewById(R.id.madeFTButton);
            missedFTButton = itemView.findViewById(R.id.missedFTButton);
            reboundsButton = itemView.findViewById(R.id.reboundsButton);
            assistsButton = itemView.findViewById(R.id.assistsButton);
            stealsButton = itemView.findViewById(R.id.stealsButton);
            blocksButton = itemView.findViewById(R.id.blocksButton);
            turnoversButton = itemView.findViewById(R.id.turnoversButton);
            deletePlayerButton = itemView.findViewById(R.id.deletePlayerButton);
        }

        public void bind(final Player player) {
            playerNameTextView.setText(player.getName());

            // Set stat values
            pointsTextView.setText(String.valueOf(player.getPoints()));

            madeFGTextView.setText(String.valueOf(player.getMadeFG()));
            missedFGTextView.setText(String.valueOf(player.getMissedFG()));
            int fga = player.getMadeFG() + player.getMissedFG();
            fgaTextView.setText(String.valueOf(fga));

            double fgPercentage = fga > 0 ? (double) player.getMadeFG() / fga : 0;
            fgPercentageTextView.setText(percentFormat.format(fgPercentage));

            made3PTTextView.setText(String.valueOf(player.getMade3PT()));
            missed3PTTextView.setText(String.valueOf(player.getMissed3PT()));
            int tpta = player.getMade3PT() + player.getMissed3PT();
            tptaTextView.setText(String.valueOf(tpta));

            double tptPercentage = tpta > 0 ? (double) player.getMade3PT() / tpta : 0;
            tptPercentageTextView.setText(percentFormat.format(tptPercentage));

            madeFTTextView.setText(String.valueOf(player.getMadeFT()));
            missedFTTextView.setText(String.valueOf(player.getMissedFT()));
            int fta = player.getMadeFT() + player.getMissedFT();
            ftaTextView.setText(String.valueOf(fta));

            double ftPercentage = fta > 0 ? (double) player.getMadeFT() / fta : 0;
            ftPercentageTextView.setText(percentFormat.format(ftPercentage));

            reboundsTextView.setText(String.valueOf(player.getRebounds()));
            assistsTextView.setText(String.valueOf(player.getAssists()));
            stealsTextView.setText(String.valueOf(player.getSteals()));
            blocksTextView.setText(String.valueOf(player.getBlocks()));
            turnoversTextView.setText(String.valueOf(player.getTurnovers()));

            // Set button click listeners
            setupButton(madeFGButton, player, "madeFG");
            setupButton(missedFGButton, player, "missedFG");
            setupButton(made3PTButton, player, "made3PT");
            setupButton(missed3PTButton, player, "missed3PT");
            setupButton(madeFTButton, player, "madeFT");
            setupButton(missedFTButton, player, "missedFT");
            setupButton(reboundsButton, player, "rebounds");
            setupButton(assistsButton, player, "assists");
            setupButton(stealsButton, player, "steals");
            setupButton(blocksButton, player, "blocks");
            setupButton(turnoversButton, player, "turnovers");

            deletePlayerButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && statIncrementListener != null) {
                    statIncrementListener.onClick(player, "delete", 0);
                }
            });


        }

        private void setupButton(Button button, Player player, String statType) {
            button.setOnClickListener(v -> {
                statIncrementListener.onClick(player, statType, 1);
                buttonAnimationListener.onButtonPressed(button);
            });
        }
    }
}
