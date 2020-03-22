package com.etologic.mahjongscoring2.business.model.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.*
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.DateConverter
import java.util.*

@Entity(
    tableName = "Games",
    indices = [Index(value = ["gameId"], unique = true)]
)
class Game(
    @field:PrimaryKey(autoGenerate = true) val gameId: Long,
    var nameP1: String = "",
    var nameP2: String = "",
    var nameP3: String = "",
    var nameP4: String = "",
    @TypeConverters(DateConverter::class) val startDate: Date
) {
    
    companion object {
        private const val NOT_SET_GAME_ID: Long = 0
    }
    
    constructor() : this(
        NOT_SET_GAME_ID,
        "Player 1",
        "Player 2",
        "Player 3",
        "Player 4",
        Calendar.getInstance().time
    )
    
    val copy: Game
        get() = Game(
            gameId,
            nameP1,
            nameP2,
            nameP3,
            nameP4,
            startDate
        )
    
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