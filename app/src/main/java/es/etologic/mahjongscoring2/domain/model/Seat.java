package es.etologic.mahjongscoring2.domain.model;

public class Seat {

    private String Name;
    private boolean Penalty;
    private String Points;

    public String getName() {
        return Name;
    }

    public boolean isPenalty() {
        return Penalty;
    }

    public String getPoints() {
        return Points;
    }

    public Seat(String name, boolean penalty, String points) {
        Name = name;
        Penalty = penalty;
        Points = points;
    }
}
