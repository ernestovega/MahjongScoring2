package es.etologic.mahjongscoring2.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Players",
    indices = [Index(value = ["playerName"], unique = true)]
)
class Player(@field:PrimaryKey(autoGenerate = true) val playerId: Int, val playerName: String) {
    
    companion object {
        
        private const val NOT_SET_PLAYER_ID = 0
        
        fun getNewPlayer(playerName: String): Player = Player(NOT_SET_PLAYER_ID, playerName)
    }
}
