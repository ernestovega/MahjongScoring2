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
package com.etologic.mahjongscoring2.app.game.dialogs.roll_dice

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.VISIBLE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.activity.GameViewModel
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment.DiceNumber.FIVE
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment.DiceNumber.FOUR
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment.DiceNumber.ONE
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment.DiceNumber.SIX
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment.DiceNumber.THREE
import com.etologic.mahjongscoring2.app.game.dialogs.roll_dice.RollDiceDialogFragment.DiceNumber.TWO
import com.etologic.mahjongscoring2.databinding.GameDiceDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class RollDiceDialogFragment : AppCompatDialogFragment() {

    companion object {
        const val TAG: String = "RollDiceDialogFragment"
    }

    private enum class DiceNumber(val code: Int) {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6);

        companion object {
            fun getRandom(): DiceNumber =
                when (Random().nextInt(6) + 1) {
                    1 -> ONE
                    2 -> TWO
                    3 -> THREE
                    4 -> FOUR
                    5 -> FIVE
                    else -> SIX
                }
        }
    }

    private lateinit var diceSound: SoundPool       //For dice sound playing
    private var soundId: Int = 0               //Used to control sound stream return by SoundPool
    private lateinit var handler12: Handler            //Post message to start roll
    private lateinit var handler34: Handler            //Post message to start roll
    private val timer = Timer()  //Used to implement feedback to user
    private var isRolling = false   //Is dice isRolling?
    private var zero: String? = null

    //When pause completed message sent to callback
    private inner class Roll12 : TimerTask() {

        override fun run() {
            handler12.sendEmptyMessage(0)
        }
    }

    private inner class Roll34 : TimerTask() {

        override fun run() {
            handler34.sendEmptyMessage(0)
        }
    }

    private var _binding: GameDiceDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameDiceDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        zero = resources.getString(R.string.zero)
        initSound()
        initDice()
    }

    private fun initSound() {
        val aa = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        diceSound = SoundPool.Builder().setAudioAttributes(aa).build()
        soundId = diceSound.load(context, R.raw.shake_dice, 1)
    }

    private fun initDice() {
        with(binding) {
            handler12 = Handler(Looper.getMainLooper()) {
                //Receives message from timer to start dice roll
                rollNewDice(ivDice1)
                rollNewDice(ivDice2)

                tvDiceDialogSecond.text = getSecondThrowerName()
                if (tvDiceDialogSecond.visibility != VISIBLE) tvDiceDialogSecond.visibility =
                    VISIBLE
                if (ivDice3.visibility != VISIBLE) ivDice3.visibility = VISIBLE
                if (ivDice4.visibility != VISIBLE) ivDice4.visibility = VISIBLE

                true
            }
            ivDice1.setOnSecureClickListener { listener12() }
            ivDice2.setOnSecureClickListener { listener12() }
            ivDice1.setImageResource(R.drawable.dice3droll)
            ivDice2.setImageResource(R.drawable.dice3droll)
            ivDice1.contentDescription = zero
            ivDice2.contentDescription = zero
            ivDice1.tag = 0
            ivDice2.tag = 0

            tvDiceDialogFirst.text = activityViewModel.game.getCurrentEastSeatPlayerName() ?: getString(R.string.first)

            handler34 = Handler(Looper.getMainLooper()) {
                //Receives message from timer to start dice roll
                rollNewDice(ivDice3)
                rollNewDice(ivDice4)
                true
            }
            ivDice3.setOnSecureClickListener { listener34() }
            ivDice4.setOnSecureClickListener { listener34() }
            ivDice3.setImageResource(R.drawable.dice3droll)
            ivDice4.setImageResource(R.drawable.dice3droll)
            ivDice3.tag = zero
            ivDice4.tag = zero
            ivDice3.contentDescription = zero
            ivDice4.contentDescription = zero
        }
    }

    private fun getSecondThrowerName(): String {
        val uiGame = activityViewModel.game
        return when (binding.ivDice1.tag as Int + binding.ivDice2.tag as Int) {
            5, 9 -> uiGame.getCurrentEastSeatPlayerName()
            2, 6, 10 -> uiGame.getCurrentSouthSeatPlayerName()
            3, 7, 11 -> uiGame.getCurrentWestSeatPlayerName()
            4, 8, 12 -> uiGame.getCurrentNorthSeatPlayerName()
            else -> null
        } ?: getString(R.string.second)
    }

    private fun listener12() {
        with(binding) {
            if (!isRolling) {
                isRolling = true

                tvDiceDialogSecond.text = ""
                ivDice1.setImageResource(R.drawable.dice3droll)
                ivDice2.setImageResource(R.drawable.dice3droll)
                ivDice3.setImageResource(R.drawable.dice3droll)
                ivDice4.setImageResource(R.drawable.dice3droll)
                ivDice1.contentDescription = zero
                ivDice2.contentDescription = zero
                ivDice3.tag = zero
                ivDice4.tag = zero
                ivDice1.tag = 0
                ivDice2.tag = 0
                ivDice3.contentDescription = zero
                ivDice4.contentDescription = zero

                diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
                diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)

                timer.schedule(Roll12(), 400)
            }
        }
    }

    private fun listener34() {
        with(binding) {
            if (!isRolling) {
                isRolling = true
                //Show isRolling image
                ivDice3.setImageResource(R.drawable.dice3droll)
                ivDice4.setImageResource(R.drawable.dice3droll)
                ivDice3.contentDescription = zero
                ivDice4.contentDescription = zero
                ivDice3.tag = 0
                ivDice4.tag = 0

                diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
                diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
                timer.schedule(Roll34(), 400)
            }
        }
    }

    private fun rollNewDice(imageView: ImageView) {
        when (DiceNumber.getRandom()) {
            ONE -> {
                imageView.setImageResource(R.drawable.one)
                imageView.contentDescription = getString(R.string.one)
                imageView.tag = ONE.code
            }

            TWO -> {
                imageView.setImageResource(R.drawable.two)
                imageView.contentDescription = getString(R.string.two)
                imageView.tag = TWO.code
            }

            THREE -> {
                imageView.setImageResource(R.drawable.three)
                imageView.contentDescription = getString(R.string.three)
                imageView.tag = THREE.code
            }

            FOUR -> {
                imageView.setImageResource(R.drawable.four)
                imageView.contentDescription = getString(R.string.four)
                imageView.tag = FOUR.code
            }

            FIVE -> {
                imageView.setImageResource(R.drawable.five)
                imageView.contentDescription = getString(R.string.five)
                imageView.tag = FIVE.code
            }

            SIX -> {
                imageView.setImageResource(R.drawable.six)
                imageView.contentDescription = getString(R.string.six)
                imageView.tag = SIX.code
            }
        }
        isRolling = false  //user can press again
    }

    override fun dismiss() {
        diceSound.pause(soundId)
        super.dismiss()
    }

    override fun onDestroy() {
        diceSound.release()
        timer.cancel()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}
