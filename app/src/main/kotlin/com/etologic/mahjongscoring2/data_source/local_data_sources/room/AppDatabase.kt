/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.etologic.mahjongscoring2.data_source.local_data_sources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.converters.DateConverter
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.model.DbGame
import com.etologic.mahjongscoring2.data_source.model.DbRound

@Database(entities = [DbGame::class, DbRound::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val gamesDao: GamesDao
    abstract val roundsDao: RoundsDao
}

object Migration1to2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {

        //Remove "Combinations" and "Tables" tables and "Rounds.roundDuration" column
        db.execSQL("DROP TABLE IF EXISTS Combinations")
        db.execSQL("DROP TABLE IF EXISTS Tables")

        // Replace "Rounds.isEnded" column by new "Games.endDate" column
        db.execSQL("ALTER TABLE Games ADD COLUMN endDate INTEGER")
        db.execSQL("UPDATE Games SET endDate = startDate")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN isEnded")

        // Add "Games.gameName" column
        db.execSQL("ALTER TABLE Games ADD COLUMN gameName TEXT NOT NULL DEFAULT ''")
        db.execSQL("UPDATE Games SET gameName = ''")

        // Make "Rounds.winnerInitialSeat" and "Rounds.discarderInitialSeat" columns nullable
        db.execSQL("ALTER TABLE Rounds ADD COLUMN winnerInitialSeat_new INTEGER")
        db.execSQL("ALTER TABLE Rounds ADD COLUMN discarderInitialSeat_new INTEGER")
        db.execSQL("UPDATE Rounds SET winnerInitialSeat_new = winnerInitialSeat")
        db.execSQL("UPDATE Rounds SET discarderInitialSeat_new = discarderInitialSeat")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN winnerInitialSeat")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN discarderInitialSeat")
        db.execSQL("ALTER TABLE Rounds RENAME COLUMN winnerInitialSeat_new TO winnerInitialSeat")
        db.execSQL("ALTER TABLE Rounds RENAME COLUMN discarderInitialSeat_new TO discarderInitialSeat")

        // Remove "Rounds.pointsPX" columns
        db.execSQL("ALTER TABLE Rounds DROP COLUMN pointsP1")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN pointsP2")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN pointsP3")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN pointsP4")
    }
}