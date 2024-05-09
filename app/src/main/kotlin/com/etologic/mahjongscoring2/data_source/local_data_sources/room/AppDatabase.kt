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
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbGame
import com.etologic.mahjongscoring2.data_source.local_data_sources.room.model.DbRound

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

        // Add "Games.gameName" column
        db.execSQL("ALTER TABLE Games ADD COLUMN gameName TEXT NOT NULL DEFAULT ''")
        db.execSQL("UPDATE Games SET gameName = ''")

        // Remove "Rounds.roundDuration", "Rounds.isEnded" and "Rounds.pointsPX" columns.
        // Make "Rounds.winnerInitialSeat" and "Rounds.discarderInitialSeat" columns nullable
        db.execSQL("CREATE TABLE IF NOT EXISTS `Rounds_temp` (`gameId` INTEGER NOT NULL, `roundId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `winnerInitialSeat` INTEGER, `discarderInitialSeat` INTEGER, `handPoints` INTEGER NOT NULL, `penaltyP1` INTEGER NOT NULL, `penaltyP2` INTEGER NOT NULL, `penaltyP3` INTEGER NOT NULL, `penaltyP4` INTEGER NOT NULL, FOREIGN KEY(`gameId`) REFERENCES `Games`(`gameId`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
        db.execSQL("INSERT INTO `Rounds_temp` SELECT `gameId`,`roundId`,`winnerInitialSeat`,`discarderInitialSeat`,`handPoints`,`penaltyP1`,`penaltyP2`,`penaltyP3`,`penaltyP4` FROM `Rounds`")
        db.execSQL("DROP TABLE `Rounds`")
        db.execSQL("ALTER TABLE `Rounds_temp` RENAME TO `Rounds`")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Rounds_gameId_roundId` ON `Rounds` (`gameId`, `roundId`)")
    }


}