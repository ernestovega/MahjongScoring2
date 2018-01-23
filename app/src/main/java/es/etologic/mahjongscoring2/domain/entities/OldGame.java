package es.etologic.mahjongscoring2.domain.entities;

public class OldGame {

    private int id;
    private String sId;
    private String startDate;
    private String endDate;
    private String roundsNumber;
    private String bestHand;
    private String nameP1;
    private String nameP2;
    private String nameP3;
    private String nameP4;
    private String pointsP1;
    private String pointsP2;
    private String pointsP3;
    private String pointsP4;

    //region Getters

    public int getId() {
        return id;
    }

    public String getSId() {
        return sId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRoundsNumber() {
        return roundsNumber;
    }

    public String getBestHand() {
        return bestHand;
    }

    public String getNameP1() {
        return nameP1;
    }

    public String getNameP2() {
        return nameP2;
    }

    public String getNameP3() {
        return nameP3;
    }

    public String getNameP4() {
        return nameP4;
    }

    public String getPointsP1() {
        return pointsP1;
    }

    public String getPointsP2() {
        return pointsP2;
    }

    public String getPointsP3() {
        return pointsP3;
    }

    public String getPointsP4() {
        return pointsP4;
    }

    //endregion

    public OldGame(int id, String sId, String startDate, String endDate, String roundsNumber,
                   String bestHand, String nameJ1, String nameJ2, String nameJ3, String nameJ4,
                   String pointsJ1, String pointsJ2, String pointsJ3, String pointsJ4) {
        this.id = id;
        this.sId = sId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roundsNumber = roundsNumber;
        this.bestHand = bestHand;
        this.nameP1 = nameJ1;
        this.nameP2 = nameJ2;
        this.nameP3 = nameJ3;
        this.nameP4 = nameJ4;
        this.pointsP1 = pointsJ1;
        this.pointsP2 = pointsJ2;
        this.pointsP3 = pointsJ3;
        this.pointsP4 = pointsJ4;
    }
}
