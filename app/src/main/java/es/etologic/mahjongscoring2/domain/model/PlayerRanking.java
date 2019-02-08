package es.etologic.mahjongscoring2.domain.model;

import android.support.annotation.NonNull;

public class PlayerRanking implements Comparable<PlayerRanking> {

    private final String name;
    private String points;
    private final String score;

    public String getName() {
        return name;
    }

    public String getPoints() {
        return points;
    }
    public void setPoints(String points) {
        this.points = points;
    }

    public String getScore() {
        return score;
    }

    public PlayerRanking(String name, String score) {
        this.name = name;
        this.score = score;
    }

    @Override public int compareTo(@NonNull PlayerRanking o) {
        return score.compareTo(o.getScore());
    }
}
