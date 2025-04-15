package edu.cuhk.csci3310.basketball_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game implements Serializable {
    private String name;
    private Date date;
    private List<Player> teamAPlayers;
    private List<Player> teamBPlayers;
    private long id;

    public Game(String name, List<Player> teamAPlayers, List<Player> teamBPlayers) {
        this.name = name;
        this.date = new Date();
        this.teamAPlayers = new ArrayList<>(teamAPlayers);
        this.teamBPlayers = new ArrayList<>(teamBPlayers);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Player> getTeamAPlayers() {
        return teamAPlayers;
    }

    public void setTeamAPlayers(List<Player> teamAPlayers) {
        this.teamAPlayers = teamAPlayers;
    }

    public List<Player> getTeamBPlayers() {
        return teamBPlayers;
    }

    public void setTeamBPlayers(List<Player> teamBPlayers) {
        this.teamBPlayers = teamBPlayers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
//for team stats details display
    public int getTeamPoints(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getPoints();
        }
        return total;
    }

    public int getTeamMadeFG(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getMadeFG();
        }
        return total;
    }

    public int getTeamFGA(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getFGA();
        }
        return total;
    }

    public float getTeamFGPercentage(String team) {
        int fga = getTeamFGA(team);
        if (fga == 0) return 0;
        return (float) getTeamMadeFG(team) / fga * 100;
    }

    public int getTeamMade3PT(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getMade3PT();
        }
        return total;
    }

    public int getTeam3PTA(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.get3PTA();
        }
        return total;
    }

    public float getTeam3PTPercentage(String team) {
        int tpta = getTeam3PTA(team);
        if (tpta == 0) return 0;
        return (float) getTeamMade3PT(team) / tpta * 100;
    }

    public int getTeamMadeFT(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getMadeFT();
        }
        return total;
    }

    public int getTeamFTA(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getFTA();
        }
        return total;
    }

    public float getTeamFTPercentage(String team) {
        int fta = getTeamFTA(team);
        if (fta == 0) return 0;
        return (float) getTeamMadeFT(team) / fta * 100;
    }

    public int getTeamRebounds(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getRebounds();
        }
        return total;
    }

    public int getTeamAssists(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getAssists();
        }
        return total;
    }

    public int getTeamSteals(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getSteals();
        }
        return total;
    }

    public int getTeamBlocks(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getBlocks();
        }
        return total;
    }

    public int getTeamTurnovers(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        int total = 0;
        for (Player p : players) {
            total += p.getTurnovers();
        }
        return total;
    }
//compare points first, then other stats
    public Player getTopPerformer(String team) {
        List<Player> players = team.equals("A") ? teamAPlayers : teamBPlayers;
        if (players.isEmpty()) return null;

        Player top = players.get(0);
        for (Player p : players) {
            if (p.getPoints() > top.getPoints()) {
                top = p;
            } else if (p.getPoints() == top.getPoints()) {

                if (p.getRebounds() + p.getAssists() > top.getRebounds() + top.getAssists()) {
                    top = p;
                }
            }
        }
        return top;
    }
}
