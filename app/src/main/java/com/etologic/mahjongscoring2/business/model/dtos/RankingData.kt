package com.etologic.mahjongscoring2.business.model.dtos

class RankingData(
    val sortedPlayersRankings: List<PlayerRanking>,
    val bestHandPlayerPoints: String,
    val bestHandPlayerName: String,
    val numRounds: Int,
    val sNumRounds: String
)

