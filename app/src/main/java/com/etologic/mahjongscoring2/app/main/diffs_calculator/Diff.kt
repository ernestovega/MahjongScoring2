package com.etologic.mahjongscoring2.app.main.diffs_calculator

import com.etologic.mahjongscoring2.business.model.dtos.getNeededPointsByDirectHu
import com.etologic.mahjongscoring2.business.model.dtos.getNeededPointsByIndirectHu
import com.etologic.mahjongscoring2.business.model.dtos.getNeededPointsBySelfPick

internal class Diff(
    val pointsNeeded: Int
) {
    val selfPick: Int = pointsNeeded.getNeededPointsBySelfPick()
    val directHu: Int = pointsNeeded.getNeededPointsByDirectHu()
    val indirectHu: Int = pointsNeeded.getNeededPointsByIndirectHu()
}