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
package com.etologic.mahjongscoring2.app.screens.game.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.DialogEditNamesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditNamesDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "EditNamesDialogFragment"
    }

    private var _binding: DialogEditNamesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditNamesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tietNamesDialogGameName.showKeyboard(requireActivity().window)
        binding.btNamesDialogSave.text = getString(R.string.save)
        startObservingTable()
    }

    private fun startObservingTable() {
        Log.d("EditNamesDialogFragment", "GameViewModel: ${gameViewModel.hashCode()} - parentFragment: ${parentFragment.hashCode()}")
        viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(STARTED) { gameViewModel.gameFlow.first().let { initViews(it) } } }
    }

    private fun initViews(game: UiGame) {
        with(binding) {
            tietNamesDialogGameName.setText(game.gameName)
            val names = game.playersNames
            tietNamesDialogEast.setText(names[EAST.code])
            tietNamesDialogSouth.setText(names[SOUTH.code])
            tietNamesDialogWest.setText(names[WEST.code])
            tietNamesDialogNorth.setText(names[NORTH.code])
        }
        setListeners(game)
    }

    private fun setListeners(game: UiGame) {
        with(binding) {
            tietNamesDialogEast.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogEast.selectAll() }
            tietNamesDialogSouth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogSouth.selectAll() }
            tietNamesDialogWest.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogWest.selectAll() }
            tietNamesDialogNorth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogNorth.selectAll() }
            btNamesDialogCancel.setOnSecureClickListener { dismiss() }
            btNamesDialogSave.setOnSecureClickListener {
                gameViewModel.saveGameNames(
                    game = game,
                    gameName = tietNamesDialogGameName.text?.toString() ?: "",
                    nameP1 = tietNamesDialogEast.text?.toString() ?: getString(R.string.player_one),
                    nameP2 = tietNamesDialogSouth.text?.toString() ?: getString(R.string.player_two),
                    nameP3 = tietNamesDialogWest.text?.toString() ?: getString(R.string.player_three),
                    nameP4 = tietNamesDialogNorth.text?.toString() ?: getString(R.string.player_four)
                )
                dismiss()
            }
        }
    }

    override fun dismiss() {
        binding.tietNamesDialogGameName.hideKeyboard()
        super.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        binding.tietNamesDialogGameName.hideKeyboard()
        super.onDismiss(dialog)
    }
}
