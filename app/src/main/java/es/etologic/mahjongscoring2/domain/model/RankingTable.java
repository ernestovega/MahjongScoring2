package es.etologic.mahjongscoring2.domain.model;

import java.util.List;

public final class RankingTable {

    private List<PlayerRanking> SortedPlayersRankings;
    private String BestHandPlayerPoints;
    private int NumRounds;
    private String SNumRounds;
    private String Duration;

    public List<PlayerRanking> getSortedPlayersRankings() {
        return SortedPlayersRankings;
    }

    public String getBestHandPlayerPoints() {
        return BestHandPlayerPoints;
    }

    public int getNumRounds() {
        return NumRounds;
    }

    public String getSNumRounds() {
        return SNumRounds;
    }

    public String getDuration() {
        return Duration;
    }

    public RankingTable(List<PlayerRanking> sortedPlayersRankings, String bestHandPlayerPoints,
                        int numRounds, String sNumRounds, String duration) {
        SortedPlayersRankings = sortedPlayersRankings;
        BestHandPlayerPoints = bestHandPlayerPoints;
        NumRounds = numRounds;
        SNumRounds = sNumRounds;
        Duration = duration;
    }
}

