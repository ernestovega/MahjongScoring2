package es.etologic.mahjongscoring2.domain.model

class RankingTable(
    val sortedPlayersRankings: List<PlayerRanking>,
    val bestHandPlayerPoints: String,
    val numRounds: Int,
    val sNumRounds: String,
    val duration: String
)

