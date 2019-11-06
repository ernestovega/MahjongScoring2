package es.etologic.mahjongscoring2.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import es.etologic.mahjongscoring2.data.local_data_source.local.converters.DateConverter
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import java.util.*

@Entity(
    tableName = "Games",
    indices = [Index(value = ["gameId"], unique = true)]
)
class Game(
    @field:PrimaryKey(autoGenerate = true) val gameId: Int,
    var nameP1: String = "",
    var nameP2: String = "",
    var nameP3: String = "",
    var nameP4: String = "",
    @TypeConverters(DateConverter::class) val startDate: Date,
    @TypeConverters(DateConverter::class) var endDate: Date? = null
) : GameRounds() {
    
    companion object {
        private const val NOT_SET_GAME_ID = 0
    }
    
    constructor(playersNames: List<String>) : this(
        NOT_SET_GAME_ID, playersNames[EAST.code], playersNames[SOUTH.code], playersNames[WEST.code],
        playersNames[NORTH.code], Calendar.getInstance().time
    )
    
    val copy: Game get() = Game(gameId, nameP1, nameP2, nameP3, nameP4, startDate)
    
    fun getPlayersNames(): Array<String> = arrayOf(nameP1, nameP2, nameP3, nameP4)
    
    fun getPlayerNameByInitialPosition(initialPosition: TableWinds): String {
        return when (initialPosition) {
            NONE -> ""
            EAST -> nameP1
            SOUTH -> nameP2
            WEST -> nameP3
            NORTH -> nameP4
        }
    }
}