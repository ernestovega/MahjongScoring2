package es.etologic.mahjongscoring2.app.model;

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

public class Seat {

    private TableWinds wind;
    private String name;
    private int points;
    private int penalty;
    private SeatStates state;

    public TableWinds getWind() {
        return wind;
    }
    public String getName() {
        return name;
    }
    public int getPoints() {
        return points;
    }
    public int getPenalty() {
        return penalty;
    }
    public SeatStates getState() {
        return state;
    }

    public void setWind(TableWinds wind) {
        this.wind = wind;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }
    public void setState(SeatStates state) {
        this.state = state;
    }

    public Seat(TableWinds wind, String name, int points, int penalty, SeatStates state) {
        this.wind = wind;
        this.name = name;
        this.points = points;
        this.penalty = 0;
        this.state = state;
    }

    public Seat(TableWinds wind, String name, int points, SeatStates state) {
        this.wind = wind;
        this.name = name;
        this.points = points;
        this.penalty = 0;
        this.state = state;
    }
}
