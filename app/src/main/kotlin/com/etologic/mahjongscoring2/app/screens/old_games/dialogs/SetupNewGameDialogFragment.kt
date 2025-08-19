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

package com.etologic.mahjongscoring2.app.screens.old_games.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.MainViewModel
import com.etologic.mahjongscoring2.app.screens.old_games.OldGamesFragment
import com.etologic.mahjongscoring2.app.screens.old_games.OldGamesViewModel
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.hideKeyboard
import com.etologic.mahjongscoring2.app.utils.KeyboardUtils.showKeyboard
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.databinding.DialogEditNamesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupNewGameDialogFragment : AppCompatDialogFragment() {

    companion object {
        const val TAG = "SetupNewGameDialogFragment"
    }

    private var _binding: DialogEditNamesFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val oldGamesViewModel by viewModels<OldGamesViewModel>(ownerProducer = {
        requireParentFragment().childFragmentManager.fragments.filterIsInstance<OldGamesFragment>().firstOrNull()
            ?: throw IllegalStateException()
    })

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
        setOnClickListeners()
        printNames()
        binding.tietNamesDialogGameName.showKeyboard(requireActivity().window)
        binding.btNamesDialogSave.text = getString(R.string.start)
    }

    private fun printNames() {
        binding.tietNamesDialogEast.setText(getString(R.string.player_one))
        binding.tietNamesDialogSouth.setText(getString(R.string.player_two))
        binding.tietNamesDialogWest.setText(getString(R.string.player_three))
        binding.tietNamesDialogNorth.setText(getString(R.string.player_four))
    }

    private fun setOnClickListeners() {
        with(binding) {
            tietNamesDialogEast.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogEast.selectAll() }
            tietNamesDialogSouth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogSouth.selectAll() }
            tietNamesDialogWest.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogWest.selectAll() }
            tietNamesDialogNorth.setOnFocusChangeListener { _, isFocused -> if (isFocused) tietNamesDialogNorth.selectAll() }
            btNamesDialogCancel.setOnSecureClickListener { dismiss() }
            btNamesDialogSave.setOnSecureClickListener {
                oldGamesViewModel.createGame(
                    gameName = tietNamesDialogGameName.text?.toString() ?: "",
                    nameP1 = tietNamesDialogEast.text?.toString() ?: getString(R.string.player_one),
                    nameP2 = tietNamesDialogSouth.text?.toString() ?: getString(R.string.player_two),
                    nameP3 = tietNamesDialogWest.text?.toString() ?: getString(R.string.player_three),
                    nameP4 = tietNamesDialogNorth.text?.toString() ?: getString(R.string.player_four),
                    onSuccess = { gameId ->
                        activityViewModel.activeGameId = gameId
                        findNavController().navigate(SetupNewGameDialogFragmentDirections.actionSetupNewGameDialogFragmentToNavGraphGame())
                        dismiss()
                    }
                )
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
