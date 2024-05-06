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
package com.etologic.mahjongscoring2.data_source.local_data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.local_data_source.converters.DateConverter
import com.etologic.mahjongscoring2.data_source.local_data_source.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_source.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.model.DBGame

@Database(entities = [DBGame::class, Round::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val gamesDao: GamesDao
    abstract val roundsDao: RoundsDao
}

object Migration1to2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS Combinations")
        db.execSQL("DROP TABLE IF EXISTS Tables")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN roundDuration")

        db.execSQL("ALTER TABLE Games ADD COLUMN endDate INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN isEnded")
        db.execSQL("UPDATE Games SET endDate = startDate")

        db.execSQL("ALTER TABLE Games ADD COLUMN gameName TEXT DEFAULT ''")

        db.execSQL("ALTER TABLE Rounds ADD COLUMN winnerInitialSeat_new INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE Rounds ADD COLUMN discarderInitialSeat_new INTEGER DEFAULT NULL")
        db.execSQL("UPDATE Rounds SET winnerInitialSeat_new = winnerInitialSeat")
        db.execSQL("UPDATE Rounds SET discarderInitialSeat_new = discarderInitialSeat")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN winnerInitialSeat")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN discarderInitialSeat")
        db.execSQL("ALTER TABLE Rounds ADD COLUMN winnerInitialSeat INTEGER DEFAULT NULL")
        db.execSQL("ALTER TABLE Rounds ADD COLUMN discarderInitialSeat INTEGER DEFAULT NULL")
        db.execSQL("UPDATE Rounds SET winnerInitialSeat = winnerInitialSeat_new")
        db.execSQL("UPDATE Rounds SET discarderInitialSeat = discarderInitialSeat_new")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN winnerInitialSeat_new")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN discarderInitialSeat_new")
    }
}