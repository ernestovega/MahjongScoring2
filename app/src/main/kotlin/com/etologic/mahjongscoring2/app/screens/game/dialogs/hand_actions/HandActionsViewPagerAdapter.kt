/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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
package com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.hand_action_hu.HuFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.hand_action_penalty.PenaltyFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.hand_actions.HandActionsFragment

class HandActionsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> HandActionsFragment()
            1 -> HuFragment()
            else -> PenaltyFragment()
        }
}
