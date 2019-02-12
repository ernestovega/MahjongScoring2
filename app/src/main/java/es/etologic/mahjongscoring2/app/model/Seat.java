package es.etologic.mahjongscoring2.app.model;

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static es.etologic.mahjongscoring2.app.model.SeatState.NORMAL;

public class Seat {

    private TableWinds wind;
    private String name;
    private Integer points;
    private int penalty;
    private SeatState state;

    public TableWinds getWind() {
        return wind;
    }
    public String getName() {
        return name;
    }
    public Integer getPoints() {
        return points;
    }
    public int getPenalty() {
        return penalty;
    }
    public SeatState getState() {
        return state;
    }

    public void setWind(TableWinds wind) {
        this.wind = wind;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }
    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }
    public void setState(SeatState state) {
        this.state = state;
    }

    public Seat(TableWinds wind, String name, Integer points) {
        this.wind = wind;
        this.name = name;
        this.points = points;
        this.penalty = 0;
        this.state = NORMAL;
    }
}
