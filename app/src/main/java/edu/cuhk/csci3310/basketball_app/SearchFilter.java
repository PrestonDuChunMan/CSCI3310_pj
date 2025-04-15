package edu.cuhk.csci3310.basketball_app;

import java.util.ArrayList;
import java.util.List;

public class SearchFilter {

    public static List<Game> filterGames(List<Game> games, String query) {
        if (query == null || query.trim().isEmpty()) return games;
        query = query.toLowerCase();
        List<Game> filtered = new ArrayList<>();
        for (Game game : games) {
            if (game.getName().toLowerCase().contains(query)) {
                filtered.add(game);
            }
        }
        return filtered;
    }

    public static List<String> filterPlayers(List<String> players, String query) {
        if (query == null || query.trim().isEmpty()) return players;
        query = query.toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String player : players) {
            if (player.toLowerCase().contains(query)) {
                filtered.add(player);
            }
        }
        return filtered;
    }
}
