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
package com.etologic.mahjongscoring2.data_source.local_data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.etologic.mahjongscoring2.business.model.entities.Round
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.DateConverter
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.GamesDao
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.RoundsDao
import com.etologic.mahjongscoring2.data_source.model.DBGame

@Database(entities = [DBGame::class, Round::class], version = 5)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val gamesDao: GamesDao
    abstract val roundsDao: RoundsDao
}

object Migration1to2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS Combinations")
        db.execSQL("DROP TABLE IF EXISTS Tables")
    }
}

object Migration2to3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Games ADD COLUMN endDate INTEGER")
        db.execSQL("ALTER TABLE Rounds DROP COLUMN isEnded")
        db.execSQL("UPDATE Games SET endDate = strftime('%s','now')")
    }
}

object Migration3to4 : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Games ADD COLUMN gameName TEXT")
    }
}

object Migration4to5 : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Games ADD COLUMN winnerInitialSeat_new INTEGER")
        db.execSQL("ALTER TABLE Games ADD COLUMN discarderInitialSeat_new INTEGER")
        db.execSQL("UPDATE Games SET winnerInitialSeat_new = winnerInitialSeat")
        db.execSQL("UPDATE Games SET discarderInitialSeat_new = discarderInitialSeat")
        db.execSQL("DROP COLUMN winnerInitialSeat")
        db.execSQL("DROP COLUMN discarderInitialSeat")
        db.execSQL("ALTER TABLE Games ADD COLUMN winnerInitialSeat INTEGER")
        db.execSQL("ALTER TABLE Games ADD COLUMN discarderInitialSeat INTEGER")
        db.execSQL("UPDATE Games SET winnerInitialSeat = winnerInitialSeat_new")
        db.execSQL("UPDATE Games SET discarderInitialSeat = discarderInitialSeat_new")
        db.execSQL("DROP COLUMN winnerInitialSeat_new")
        db.execSQL("DROP COLUMN discarderInitialSeat_new")
    }
}