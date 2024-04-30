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
package com.etologic.mahjongscoring2.app.game.dialogs.names

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.EAST
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NORTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.SOUTH
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.WEST
import com.etologic.mahjongscoring2.databinding.GameNamesDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NamesDialogFragment : AppCompatDialogFragment() {

    companion object {
        const val TAG = "NamesDialogFragment"
    }

    private var _binding: GameNamesDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameNamesDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        printNames()
        binding.tietNamesDialogGameName.showKeyboard(requireActivity().window)
    }

    private fun printNames() {
        val names = activityViewModel.game.dbGame.getPlayersNames()
        binding.tietNamesDialogEast.setText(names[EAST.code])
        binding.tietNamesDialogSouth.setText(names[SOUTH.code])
        binding.tietNamesDialogWest.setText(names[WEST.code])
        binding.tietNamesDialogNorth.setText(names[NORTH.code])
    }

    private fun setOnClickListeners() {
        with(binding) {
            tietNamesDialogEast.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogEast.selectAll() }
            tietNamesDialogSouth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogSouth.selectAll() }
            tietNamesDialogWest.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogWest.selectAll() }
            tietNamesDialogNorth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogNorth.selectAll() }
            btNamesDialogCancel.setOnSecureClickListener { dismiss() }
            btNamesDialogSave.setOnSecureClickListener {
                activityViewModel.savePlayersNames(
                    gameName = tietNamesDialogGameName.text,
                    nameP1 = tietNamesDialogEast.text,
                    nameP2 = tietNamesDialogSouth.text,
                    nameP3 = tietNamesDialogWest.text,
                    nameP4 = tietNamesDialogNorth.text
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
