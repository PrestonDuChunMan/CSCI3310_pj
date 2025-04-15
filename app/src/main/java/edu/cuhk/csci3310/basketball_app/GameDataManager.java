package edu.cuhk.csci3310.basketball_app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataManager {
    private static final String PREF_NAME = "GameData";
    private static final String GAMES_KEY = "games";
    private static final String PLAYER_STATS_KEY = "player_stats";

    private Context context;
    private Gson gson;

    public GameDataManager(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void saveGame(Game game) {
        // Get existing games
        List<Game> games = getAllGames();

        // Generate ID if new game
        if (game.getId() == 0) {
            long maxId = 0;
            for (Game g : games) {
                if (g.getId() > maxId) {
                    maxId = g.getId();
                }
            }
            game.setId(maxId + 1);
        }

        // Update or add game
        boolean found = false;
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getId() == game.getId()) {
                games.set(i, game);
                found = true;
                break;
            }
        }

        if (!found) {
            games.add(game);
        }

        // Save to SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String gamesJson = gson.toJson(games);
        editor.putString(GAMES_KEY, gamesJson);
        editor.apply();

        // Also update player stats
        updatePlayerStats(game);
    }

    public List<Game> getAllGames() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String gamesJson = prefs.getString(GAMES_KEY, "");

        if (gamesJson.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Game>>() {}.getType();
        List<Game> games = gson.fromJson(gamesJson, type);

        // Sort by date (newest first)
        Collections.sort(games, (g1, g2) -> g2.getDate().compareTo(g1.getDate()));

        return games;
    }

    public Game getGameById(long id) {
        List<Game> games = getAllGames();
        for (Game game : games) {
            if (game.getId() == id) {
                return game;
            }
        }
        return null;
    }

    private void updatePlayerStats(Game game) {
        Map<String, List<PlayerGameStats>> playerStatsMap = getAllPlayerStats();

        // Process Team A players
        for (Player player : game.getTeamAPlayers()) {
            updatePlayerGameStats(playerStatsMap, player, game.getId(), "A");
        }

        // Process Team B players
        for (Player player : game.getTeamBPlayers()) {
            updatePlayerGameStats(playerStatsMap, player, game.getId(), "B");
        }

        // Save updated player stats
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String playerStatsJson = gson.toJson(playerStatsMap);
        editor.putString(PLAYER_STATS_KEY, playerStatsJson);
        editor.apply();
    }

    public void deleteGame(long gameId) {
        List<Game> games = getAllGames();
        Game gameToDelete = null;

        // Find the game to delete
        for (Game game : games) {
            if (game.getId() == gameId) {
                gameToDelete = game;
                break;
            }
        }

        if (gameToDelete != null) {
            // Remove the game from the list
            games.remove(gameToDelete);

            // Save updated games list
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String gamesJson = gson.toJson(games);
            editor.putString(GAMES_KEY, gamesJson);
            editor.apply();

            // Also remove this game's stats from player records
            removeGameStatsFromPlayers(gameId);
        }
    }

    private void removeGameStatsFromPlayers(long gameId) {
        Map<String, List<PlayerGameStats>> playerStatsMap = getAllPlayerStats();
        boolean mapUpdated = false;

        // For each player, remove stats related to this game
        for (String playerName : playerStatsMap.keySet()) {
            List<PlayerGameStats> statsList = playerStatsMap.get(playerName);
            if (statsList != null) {
                // Create an iterator to safely remove while iterating
                for (int i = statsList.size() - 1; i >= 0; i--) {
                    if (statsList.get(i).getGameId() == gameId) {
                        statsList.remove(i);
                        mapUpdated = true;
                    }
                }
            }
        }

        // Save updated player stats if needed
        if (mapUpdated) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String playerStatsJson = gson.toJson(playerStatsMap);
            editor.putString(PLAYER_STATS_KEY, playerStatsJson);
            editor.apply();
        }
    }

    public void updateGameName(long gameId, String newName) {
        Game game = getGameById(gameId);
        if (game != null) {
            game.setName(newName);
            saveGame(game);
        }
    }

    private void updatePlayerGameStats(Map<String, List<PlayerGameStats>> playerStatsMap,
                                       Player player, long gameId, String team) {
        String playerName = player.getName();

        if (!playerStatsMap.containsKey(playerName)) {
            playerStatsMap.put(playerName, new ArrayList<>());
        }

        List<PlayerGameStats> playerGameStatsList = playerStatsMap.get(playerName);

        // Check if stats for this game already exist
        boolean found = false;
        for (int i = 0; i < playerGameStatsList.size(); i++) {
            PlayerGameStats stats = playerGameStatsList.get(i);
            if (stats.getGameId() == gameId) {
                // Update existing stats
                stats.updateFromPlayer(player, team);
                found = true;
                break;
            }
        }

        if (!found) {
            // Add new game stats
            PlayerGameStats newStats = new PlayerGameStats(gameId, player, team);
            playerGameStatsList.add(newStats);
        }
    }

    public Map<String, List<PlayerGameStats>> getAllPlayerStats() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String playerStatsJson = prefs.getString(PLAYER_STATS_KEY, "");

        if (playerStatsJson.isEmpty()) {
            return new HashMap<>();
        }

        Type type = new TypeToken<Map<String, List<PlayerGameStats>>>() {}.getType();
        return gson.fromJson(playerStatsJson, type);
    }

    public List<PlayerGameStats> getPlayerStats(String playerName) {
        Map<String, List<PlayerGameStats>> playerStatsMap = getAllPlayerStats();
        return playerStatsMap.getOrDefault(playerName, new ArrayList<>());
    }

    public PlayerStats getPlayerAverageStats(String playerName) {
        List<PlayerGameStats> gameStatsList = getPlayerStats(playerName);

        if (gameStatsList.isEmpty()) {
            return new PlayerStats();
        }

        PlayerStats averageStats = new PlayerStats();
        int gamesPlayed = gameStatsList.size();

        for (PlayerGameStats gameStats : gameStatsList) {
            averageStats.points += gameStats.points;
            averageStats.madeFG += gameStats.madeFG;
            averageStats.fga += gameStats.fga;
            averageStats.made3PT += gameStats.made3PT;
            averageStats.tpta += gameStats.tpta;
            averageStats.madeFT += gameStats.madeFT;
            averageStats.fta += gameStats.fta;
            averageStats.rebounds += gameStats.rebounds;
            averageStats.assists += gameStats.assists;
            averageStats.steals += gameStats.steals;
            averageStats.blocks += gameStats.blocks;
            averageStats.turnovers += gameStats.turnovers;
        }

        // Calculate averages
        averageStats.points /= gamesPlayed;
        averageStats.madeFG /= gamesPlayed;
        averageStats.fga /= gamesPlayed;
        averageStats.made3PT /= gamesPlayed;
        averageStats.tpta /= gamesPlayed;
        averageStats.madeFT /= gamesPlayed;
        averageStats.fta /= gamesPlayed;
        averageStats.rebounds /= gamesPlayed;
        averageStats.assists /= gamesPlayed;
        averageStats.steals /= gamesPlayed;
        averageStats.blocks /= gamesPlayed;
        averageStats.turnovers /= gamesPlayed;

        return averageStats;
    }

    public static class PlayerGameStats {
        private long gameId;
        private String team;
        private int points;
        private int madeFG;
        private int fga;
        private int made3PT;
        private int tpta;
        private int madeFT;
        private int fta;
        private int rebounds;
        private int assists;
        private int steals;
        private int blocks;
        private int turnovers;

        public PlayerGameStats() {
        }

        public PlayerGameStats(long gameId, Player player, String team) {
            this.gameId = gameId;
            this.team = team;
            updateFromPlayer(player, team);
        }

        public void updateFromPlayer(Player player, String team) {
            this.team = team;
            this.points = player.getPoints();

            // Field goals stats (includes 3PT)
            this.madeFG = player.getMadeFG();
            this.fga = player.getFGA();

            // 3PT stats (subset of FG)
            this.made3PT = player.getMade3PT();
            this.tpta = player.get3PTA();

            // Free throw stats
            this.madeFT = player.getMadeFT();
            this.fta = player.getFTA();

            // Other stats
            this.rebounds = player.getRebounds();
            this.assists = player.getAssists();
            this.steals = player.getSteals();
            this.blocks = player.getBlocks();
            this.turnovers = player.getTurnovers();
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        // Getters and setters for other fields
        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public int getMadeFG() {
            return madeFG;
        }

        public void setMadeFG(int madeFG) {
            this.madeFG = madeFG;
        }

        public int getFga() {
            return fga;
        }

        public void setFga(int fga) {
            this.fga = fga;
        }

        public float getFgPercentage() {
            return fga > 0 ? (float) madeFG / fga * 100 : 0;
        }

        public int getMade3PT() {
            return made3PT;
        }

        public void setMade3PT(int made3PT) {
            this.made3PT = made3PT;
        }

        public int getTpta() {
            return tpta;
        }

        public void setTpta(int tpta) {
            this.tpta = tpta;
        }

        public float get3ptPercentage() {
            return tpta > 0 ? (float) made3PT / tpta * 100 : 0;
        }

        public int getMadeFT() {
            return madeFT;
        }

        public void setMadeFT(int madeFT) {
            this.madeFT = madeFT;
        }

        public int getFta() {
            return fta;
        }

        public void setFta(int fta) {
            this.fta = fta;
        }

        public float getFtPercentage() {
            return fta > 0 ? (float) madeFT / fta * 100 : 0;
        }

        public int getRebounds() {
            return rebounds;
        }

        public void setRebounds(int rebounds) {
            this.rebounds = rebounds;
        }

        public int getAssists() {
            return assists;
        }

        public void setAssists(int assists) {
            this.assists = assists;
        }

        public int getSteals() {
            return steals;
        }

        public void setSteals(int steals) {
            this.steals = steals;
        }

        public int getBlocks() {
            return blocks;
        }

        public void setBlocks(int blocks) {
            this.blocks = blocks;
        }

        public int getTurnovers() {
            return turnovers;
        }

        public void setTurnovers(int turnovers) {
            this.turnovers = turnovers;
        }
    }

    public static class PlayerStats {
        public float points;
        public float madeFG;
        public float fga;
        public float made3PT;
        public float tpta;
        public float madeFT;
        public float fta;
        public float rebounds;
        public float assists;
        public float steals;
        public float blocks;
        public float turnovers;

        public float getFgPercentage() {
            return fga > 0 ? madeFG / fga * 100 : 0;
        }

        public float get3ptPercentage() {
            return tpta > 0 ? made3PT / tpta * 100 : 0;
        }

        public float getFtPercentage() {
            return fta > 0 ? madeFT / fta * 100 : 0;
        }
    }
}
