/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.data_source.local_data_source.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.etologic.mahjongscoring2.business.model.entities.Table
import io.reactivex.Single

@Dao
interface TableDao {

    @Transaction
    @Query("SELECT * from Games ORDER BY startDate DESC")
    fun getTablesSortedByDateDesc(): Single<List<Table>>

    @Transaction
    @Query("SELECT * from Games WHERE gameId = :gameId ORDER BY startDate")
    fun getTable(gameId: Long): Single<Table>
}
