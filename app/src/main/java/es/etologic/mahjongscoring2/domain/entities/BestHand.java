package es.etologic.mahjongscoring2.domain.entities;

public class BestHand {

    private String playerName;
    private int handValue;

    public String getPlayerName() {
        return playerName;
    }

    public int getHandValue() {
        return handValue;
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
