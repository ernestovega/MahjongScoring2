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

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.dialogs.hand_actions.HandActionsViewPagerAdapter.HandActions
import com.etologic.mahjongscoring2.databinding.GameDialogHandActionsParentFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HandActionsDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "HandActionsDialogFragment"
    }

    private var isDialogCancelled = true
    private var _binding: GameDialogHandActionsParentFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameDialogHandActionsParentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialogMM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        with(binding.viewPagerHandActionsParentDialog) {
            adapter = HandActionsViewPagerAdapter(this@HandActionsDialogFragment)
            isUserInputEnabled = false
        }
    }

    fun showPage(handAction: HandActions) {
        with(binding.viewPagerHandActionsParentDialog) {
            (adapter as HandActionsViewPagerAdapter).selectedHandAction = handAction
            setCurrentItem(1, true)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (isDialogCancelled) {
            gameViewModel.unselectSeats()
        }
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}