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
package com.etologic.mahjongscoring2.data_source.repositories

import com.etologic.mahjongscoring2.business.model.entities.Table
import com.etologic.mahjongscoring2.data_source.local_data_source.local.daos.TableDao
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TablesRepository
@Inject constructor() {
    
    @Inject
    lateinit var tableDao: TableDao
    
    internal fun getTable(gameId: Long): Single<Table> =
        tableDao.getTable(gameId)
            .map { it.initBestHandAndTotalsAndRoundNumbers() }
    
    internal fun getAllTables(): Single<List<Table>> =
        tableDao.getTablesSortedByDateDesc()
}
