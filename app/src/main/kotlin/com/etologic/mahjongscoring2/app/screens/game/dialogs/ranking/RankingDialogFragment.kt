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
package com.etologic.mahjongscoring2.app.screens.game.dialogs.ranking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.utils.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.screens.game.GameViewModel
import com.etologic.mahjongscoring2.app.utils.showShareGameDialog
import com.etologic.mahjongscoring2.business.model.dtos.RankingData
import com.etologic.mahjongscoring2.business.model.entities.UiGame.Companion.MAX_MCR_ROUNDS
import com.etologic.mahjongscoring2.databinding.GameTableRankingDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.String.format
import java.util.Locale.getDefault

@AndroidEntryPoint
class RankingDialogFragment : AppCompatDialogFragment() {

    companion object {
        const val TAG = "RankingDialogFragment"
    }

    private var _binding: GameTableRankingDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameTableRankingDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        RankingTableHelper.generateRankingTable(activityViewModel.gameFlow.value)
            ?.let(this::fillRankingViews)
    }

    private fun setOnClickListeners() {
        binding.btRankingDialogResume.setOnSecureClickListener {
            activityViewModel.resumeGame()
            dismiss()
        }
        binding.btRankingDialogShare.setOnSecureClickListener {
            with (requireContext()) {
                showShareGameDialog { shareGameOption ->
                    activityViewModel.shareGame(
                        option = shareGameOption,
                        getExternalFilesDir = { getExternalFilesDir(null) },
                    )
                }
            }
        }
        binding.btRankingDialogOk.setOnSecureClickListener {
            dismiss()
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
