package edu.cuhk.csci3310.basketball_app;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private int points;
    private int madeFG;
    private int missedFG;
    private int made3PT;
    private int missed3PT;
    private int madeFT;
    private int missedFT;
    private int rebounds;
    private int assists;
    private int steals;
    private int blocks;
    private int turnovers;

    public Player(String name) {
        this.name = name;
        this.points = 0;
        this.madeFG = 0;
        this.missedFG = 0;
        this.made3PT = 0;
        this.missed3PT = 0;
        this.madeFT = 0;
        this.missedFT = 0;
        this.rebounds = 0;
        this.assists = 0;
        this.steals = 0;
        this.blocks = 0;
        this.turnovers = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public int getMissedFG() {
        return missedFG;
    }

    public void setMissedFG(int missedFG) {
        this.missedFG = missedFG;
    }

    public int getFGA() {
        // FGA includes both 2PT and 3PT attempts
        return (madeFG + missedFG);
    }

    public float getFGPercentage() {
        if (getFGA() == 0) return 0;
        return (float) madeFG / getFGA() * 100;
    }

    public int getMade3PT() {
        return made3PT;
    }

    public void setMade3PT(int made3PT) {
        this.made3PT = made3PT;
    }

    public int getMissed3PT() {
        return missed3PT;
    }

    public void setMissed3PT(int missed3PT) {
        this.missed3PT = missed3PT;
    }

    public int get3PTA() {
        return made3PT + missed3PT;
    }

    public float get3PTPercentage() {
        if (get3PTA() == 0) return 0;
        return (float) made3PT / get3PTA() * 100;
    }

    public int getMadeFT() {
        return madeFT;
    }

    public void setMadeFT(int madeFT) {
        this.madeFT = madeFT;
    }

    public int getMissedFT() {
        return missedFT;
    }

    public void setMissedFT(int missedFT) {
        this.missedFT = missedFT;
    }

    public int getFTA() {
        return madeFT + missedFT;
    }

    public float getFTPercentage() {
        if (getFTA() == 0) return 0;
        return (float) madeFT / getFTA() * 100;
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

    public void calculatePoints() {
        // 2 points for each FG that's not a 3PT, 3 points for each 3PT, 1 point for each FT
        this.points = ((madeFG - made3PT) * 2) + (made3PT * 3) + madeFT;
    }
}
