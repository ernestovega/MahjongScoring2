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
package com.etologic.mahjongscoring2.business.model.entities

import androidx.annotation.DrawableRes
import androidx.room.*
import com.etologic.mahjongscoring2.business.model.entities.Combination.CombinationDescriptionType.DESCRIPTION
import com.etologic.mahjongscoring2.business.model.entities.Combination.CombinationDescriptionType.IMAGE
import com.etologic.mahjongscoring2.data_source.local_data_source.local.converters.CombinationDescriptionTypeConverter

@Entity(tableName = "Combinations", indices = [Index(value = ["combinationName"], unique = true)])
class Combination(
    val combinationPoints: Int,
    @field:PrimaryKey(autoGenerate = true) val combinationId: Long,
    var combinationName: String = "",
    @DrawableRes var combinationImage: Int = 0,
    var combinationDescription: String = "",
    @TypeConverters(CombinationDescriptionTypeConverter::class) val combinationDescriptionType: CombinationDescriptionType
) {

    enum class CombinationDescriptionType(val code: Int) {
        IMAGE(0),
        DESCRIPTION(1)
    }

    @Ignore
    constructor(combinationPoints: Int, combinationId: Long, combinationName: String, combinationImage: Int)
            : this(combinationPoints, combinationId, combinationName, combinationImage, "", IMAGE)

    @Ignore
    constructor(combinationPoints: Int, combinationId: Long, combinationName: String, combinationDescription: String)
            : this(combinationPoints, combinationId, combinationName, 0, combinationDescription, DESCRIPTION)

    fun getCopy(): Combination {
        return if (combinationDescriptionType == IMAGE) Combination(
            this.combinationPoints,
            combinationId,
            combinationName,
            combinationImage
        )
        else Combination(
            combinationPoints,
            combinationId,
            combinationName,
            combinationDescription
        )
    }
}