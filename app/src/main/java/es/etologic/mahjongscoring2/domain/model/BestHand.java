package es.etologic.mahjongscoring2.domain.model;

public class BestHand {

    private int playerInitialPosition;
    private String playerName;
    private int handValue;

    int getPlayerInitialPosition() {
        return playerInitialPosition;
    }
    public String getPlayerName() {
        return playerName;
    }
    public int getHandValue() {
        return handValue;
    }

    void setPlayerInitialPosition(int playerInitialPosition) {
        this.playerInitialPosition = playerInitialPosition;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }

    BestHand() {
        playerName = "";
        handValue = 0;
    }
}
