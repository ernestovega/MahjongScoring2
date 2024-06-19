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

package com.etologic.mahjongscoring2.app.screens.old_games

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseMainFragment
import com.etologic.mahjongscoring2.app.screens.goToPickFileToImport
import com.etologic.mahjongscoring2.app.utils.LanguageHelper
import com.etologic.mahjongscoring2.app.utils.goToChangeLanguage
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.utils.shareFiles
import com.etologic.mahjongscoring2.app.utils.shareText
import com.etologic.mahjongscoring2.app.utils.showShareGameDialog
import com.etologic.mahjongscoring2.business.model.entities.GameId
import com.etologic.mahjongscoring2.databinding.OldGamesFragmentBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OldGamesFragment : BaseMainFragment() {

    companion object {
        const val TAG = "OldGamesFragment"
        const val LAST_BACK_PRESSED_MIN_TIME: Long = 2000
    }

    private var changeLanguageItem: MenuItem? = null
    private var enableCalcsItem: MenuItem? = null
    private var disableCalcsItem: MenuItem? = null

    @Inject
    lateinit var rvAdapter: OldGamesRvAdapter

    private var _binding: OldGamesFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OldGamesViewModel by viewModels()

    @Inject
    lateinit var languageHelper: LanguageHelper

    private var lastBackPress: Long = 0

    override val onBackOrUpClick: () -> Unit = {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.oldGamesFragment) {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastBackPress > LAST_BACK_PRESSED_MIN_TIME) {
                Snackbar.make(binding.root, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show()
                lastBackPress = currentTimeMillis
            } else {
                requireActivity().finish()
            }
        } else {
            navController.navigateUp()
        }
    }

    override val toolbarMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.old_games_menu, menu)

            changeLanguageItem = menu.findItem(R.id.action_change_language)
            enableCalcsItem = menu.findItem(R.id.action_enable_diffs_calcs)
            disableCalcsItem = menu.findItem(R.id.action_disable_diffs_calcs)

            toggleDiffsEnabling((viewModel.oldGamesUiStateFlow.value as? OldGamesUiState.Loaded)?.isDiffsCalcsFeatureEnabled == true)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            with(requireActivity()) {
                when (menuItem.itemId) {
                    R.id.action_change_language -> goToChangeLanguage(languageHelper)
                    R.id.action_enable_diffs_calcs -> viewModel.toggleDiffsFeature(true)
                    R.id.action_disable_diffs_calcs -> viewModel.toggleDiffsFeature(false)
                    R.id.action_import_games -> pickFileResultLauncher.goToPickFileToImport()
                    R.id.action_export_games -> viewModel.exportGames(
                        directory = getExternalFilesDir(null),
                        showShareFiles = { files -> shareFiles(files) },
                    )

                    else -> return false
                }
                return true
            }
        }
    }

    val pickFileResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            activityResult.data?.data?.let { uri ->
                viewModel.importGames(uri) { requireContext().contentResolver }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = OldGamesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setOnClickListeners()
        startObservingViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvOldGames.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        binding.rvOldGames.layoutManager = layoutManager
        rvAdapter.setOldGameItemListener(object : OldGamesRvAdapter.GameItemListener {
            override fun onOldGameItemDeleteClicked(gameId: GameId) {
                AlertDialog.Builder(activity, R.style.AlertDialogStyleMM)
                    .setTitle(R.string.delete_game)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteGame(gameId) }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show()
            }

            override fun onOldGameItemShareClicked(gameId: GameId) {
                with(requireActivity()) {
                    showShareGameDialog { shareGameOption ->
                        viewModel.shareGame(
                            gameId = gameId,
                            option = shareGameOption,
                            directory = getExternalFilesDir(null),
                            showShareText = { text -> shareText(text) },
                            showShareFiles = { files -> shareFiles(files) },
                        )
                    }
                }
            }

            override fun onOldGameItemResumeClicked(gameId: GameId) {
                activityViewModel.activeGameId = gameId
                findNavController().navigate(R.id.action_oldGamesFragment_to_gameFragment)
            }
        })
        binding.rvOldGames.adapter = rvAdapter
    }

    private fun setOnClickListeners() {
        binding.fabOldGames.setOnSecureClickListener {
            findNavController().navigate(R.id.action_oldGamesFragment_to_setupNewGameDialogFragment)
        }
    }

    private fun startObservingViewModel() {
        viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(STARTED) { viewModel.oldGamesUiStateFlow.collect(::uiStateObserver) } }
    }

    private fun uiStateObserver(uiState: OldGamesUiState) {
        when (uiState) {
            is OldGamesUiState.Loading -> {}
            is OldGamesUiState.Error -> showErrorDialog(uiState.throwable)
            is OldGamesUiState.Loaded -> {
                rvAdapter.setGames(uiState.oldGamesList)
                binding.emptyLayoutOldGames.visibility = if (uiState.oldGamesList.isEmpty()) VISIBLE else GONE
            }
        }
    }

    private fun toggleDiffsEnabling(shouldShowDiffs: Boolean) {
        if (shouldShowDiffs) {
            enableCalcsItem?.isVisible = false
            disableCalcsItem?.isVisible = true
        } else {
            enableCalcsItem?.isVisible = true
            disableCalcsItem?.isVisible = false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
