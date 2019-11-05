package es.etologic.mahjongscoring2.domain.model

class PlayerRanking(val name: String, val score: String) : Comparable<PlayerRanking> {
    
    var points: String? = null
    
    override fun compareTo(other: PlayerRanking): Int = score.compareTo(other.score)
}
