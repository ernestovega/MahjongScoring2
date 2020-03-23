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
import com.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import kotlinx.android.synthetic.main.game_dice_dialog_fragment.*
import java.util.*

internal class RollDiceDialogFragment : BaseGameDialogFragment(), View.OnClickListener {
    
    companion object {
        const val TAG: String = "RollDiceDialogFragment"
    }
    
    private lateinit var diceSound: SoundPool       //For dice sound playing
    private var soundId: Int = 0               //Used to control sound stream return by SoundPool
    private lateinit var handler: Handler            //Post message to start roll
    private val timer = Timer()  //Used to implement feedback to user
    private var isRolling = false   //Is die isRolling?
    
    override fun onClick(v: View?) {
        if (!isRolling) {
            isRolling = true
            //Show isRolling image
            ivDice1.setImageResource(R.drawable.dice3droll)
            ivDice2.setImageResource(R.drawable.dice3droll)
            
            diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            diceSound.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
            timer.schedule(Roll(), 400)
        }
    }
    
    //When pause completed message sent to callback
    private inner class Roll : TimerTask() {
        
        override fun run() {
            handler.sendEmptyMessage(0)
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
        handler = Handler {
            //Receives message from timer to start dice roll
            rollNewDice(ivDice1)
            rollNewDice(ivDice2)
            true
        }
        ivDice1.setOnClickListener(this)
        ivDice2.setOnClickListener(this)
        ivDice1.setImageResource(R.drawable.dice3droll)
        ivDice2.setImageResource(R.drawable.dice3droll)
    }
    
    private fun rollNewDice(imageView: ImageView) {
        when (Random().nextInt(6)) {
            0 -> imageView.setImageResource(R.drawable.one)
            1 -> imageView.setImageResource(R.drawable.two)
            2 -> imageView.setImageResource(R.drawable.three)
            3 -> imageView.setImageResource(R.drawable.four)
            4 -> imageView.setImageResource(R.drawable.five)
            5 -> imageView.setImageResource(R.drawable.six)
            else -> imageView.setImageResource(R.drawable.one)
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