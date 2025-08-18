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

package com.etologic.mahjongscoring2.app.screens.game.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.base.BaseGameDialogFragment
import com.etologic.mahjongscoring2.app.screens.game.GameUiState
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.utils.shareFiles
import com.etologic.mahjongscoring2.app.utils.shareText
import com.etologic.mahjongscoring2.app.utils.showShareGameDialog
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.UiGame
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.databinding.GameDialogRankingFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.String.format
import java.util.Locale.getDefault

@AndroidEntryPoint
class RankingDialogFragment : BaseGameDialogFragment() {

    companion object {
        const val TAG = "RankingDialogFragment"
    }

    private var _binding: GameDialogRankingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameDialogRankingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initViews((gameViewModel.gameUiStateFlow.value as GameUiState.Loaded).game)
    }

    private fun initViews(game: UiGame) {
        RankingTableHelper.generateRankingTable(game)
            ?.let(this::fillRankingViews)
    }

    private fun setListeners() {
        binding.btRankingDialogResume.setOnSecureClickListener {
            gameViewModel.resumeGame()
            dismiss()
        }
        binding.btRankingDialogShare.setOnSecureClickListener {
            with(requireContext()) {
                showShareGameDialog { shareGameOption ->
                    with(requireActivity()) {
                        gameViewModel.shareGame(
                            option = shareGameOption,
                            directory = getExternalFilesDir(null),
                            showShareText = { text -> shareText(text) },
                            showShareFiles = { files -> shareFiles(files) },
                        )
                    }
                }
            }
        }
            dismiss()
        binding.btRankingDialogClose.setOnSecureClickListener {
        }
    }

    private fun fillRankingViews(rankingData: RankingData) {
        with(binding) {
            val playerFirst = rankingData.sortedPlayersRankings[0]
            val playerSecond = rankingData.sortedPlayersRankings[1]
            val playerThird = rankingData.sortedPlayersRankings[2]
            val playerFourth = rankingData.sortedPlayersRankings[3]

            btRankingDialogResume.visibility = if (rankingData.numRounds < MAX_MCR_ROUNDS) VISIBLE else GONE

            tvRankingDialogPlayer1Name.text = playerFirst.name
            tvRankingDialogPlayer2Name.text = playerSecond.name
            tvRankingDialogPlayer3Name.text = playerThird.name
            tvRankingDialogPlayer4Name.text = playerFourth.name

            tvRankingDialogPlayer1Points.text = playerFirst.points
            tvRankingDialogPlayer2Points.text = playerSecond.points
            tvRankingDialogPlayer3Points.text = playerThird.points
            tvRankingDialogPlayer4Points.text = playerFourth.points

            tvRankingDialogPlayer1Score.text = format(getDefault(), "%+d", playerFirst.score)
            tvRankingDialogPlayer2Score.text = format(getDefault(), "%+d", playerSecond.score)
            tvRankingDialogPlayer3Score.text = format(getDefault(), "%+d", playerThird.score)
            tvRankingDialogPlayer4Score.text = format(getDefault(), "%+d", playerFourth.score)

            @SuppressLint("SetTextI18n")
            tvRankingDialogBestHandTitle.text = "${getString(R.string.best_hand)}:"
            tvRankingDialogBestHandPlayerPoints.text = rankingData.bestHandPlayerPoints
            tvRankingDialogBestHandPlayerName.text = rankingData.bestHandPlayerName
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}
