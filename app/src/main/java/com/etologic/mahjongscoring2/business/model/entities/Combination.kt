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