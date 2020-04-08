/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.app.game.dialogs.roll_dice

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.extensions.setOnSecureClickListener
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import kotlinx.android.synthetic.main.game_dice_dialog_fragment.*
import java.util.*

internal class RollDiceDialogFragment : BaseGameDialogFragment() {
    
    companion object {
        const val TAG: String = "RollDiceDialogFragment"
    }
    
    private lateinit var diceSound: SoundPool       //For dice sound playing
    private var soundId: Int = 0               //Used to control sound stream return by SoundPool
    private lateinit var handler12: Handler            //Post message to start roll
    private lateinit var handler34: Handler            //Post message to start roll
    private val timer = Timer()  //Used to implement feedback to user
    private var isRolling = false   //Is die isRolling?
    
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
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.game_dice_dialog_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        handler12 = Handler {
            //Receives message from timer to start dice roll
            rollNewDice(ivDice1)
            rollNewDice(ivDice2)
            true
        }
        ivDice1?.setOnSecureClickListener { listener12() }
        ivDice2?.setOnSecureClickListener { listener12() }
        ivDice1?.setImageResource(R.drawable.dice3droll)
        ivDice2?.setImageResource(R.drawable.dice3droll)
        
        handler34 = Handler {
            //Receives message from timer to start dice roll
            rollNewDice(ivDice3)
            rollNewDice(ivDice4)
            true
        }
        ivDice3?.setOnSecureClickListener { listener34() }
        ivDice4?.setOnSecureClickListener { listener34() }
        ivDice3?.setImageResource(R.drawable.dice3droll)
        ivDice4?.setImageResource(R.drawable.dice3droll)
    }
    
    private fun listener12() {
        if (!isRolling) {
            isRolling = true
            //Show isRolling image
            ivDice1?.setImageResource(R.drawable.dice3droll)
            ivDice2?.setImageResource(R.drawable.dice3droll)
            
            diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            timer.schedule(Roll12(), 400)
        }
    }
    
    private fun listener34() {
        if (!isRolling) {
            isRolling = true
            //Show isRolling image
            ivDice3?.setImageResource(R.drawable.dice3droll)
            ivDice4?.setImageResource(R.drawable.dice3droll)
            
            diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            timer.schedule(Roll34(), 400)
        }
    }
    
    private fun rollNewDice(imageView: ImageView) {
        when (Random().nextInt(6)) {
            0 -> {
                imageView.setImageResource(R.drawable.one)
                imageView.contentDescription = getString(R.string.one)
            }
            1 -> {
                imageView.setImageResource(R.drawable.two)
                imageView.contentDescription = getString(R.string.two)
            }
            2 -> {
                imageView.setImageResource(R.drawable.three)
                imageView.contentDescription = getString(R.string.three)
            }
            3 -> {
                imageView.setImageResource(R.drawable.four)
                imageView.contentDescription = getString(R.string.four)
            }
            4 -> {
                imageView.setImageResource(R.drawable.five)
                imageView.contentDescription = getString(R.string.five)
            }
            5 -> {
                imageView.setImageResource(R.drawable.six)
                imageView.contentDescription = getString(R.string.six)
            }
            else -> {
                imageView.setImageResource(R.drawable.one)
                imageView.contentDescription = getString(R.string.one)
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
