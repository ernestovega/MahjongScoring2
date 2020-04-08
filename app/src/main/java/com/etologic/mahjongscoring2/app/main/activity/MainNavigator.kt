package com.etologic.mahjongscoring2.app.main.activity

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.LOLLIPOP
import com.etologic.mahjongscoring2.BuildConfig.APPLICATION_ID
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.R.anim.*
import com.etologic.mahjongscoring2.R.id.frameLayoutMain
import com.etologic.mahjongscoring2.app.game.activity.GameActivity
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.EMAIL_ADDRESS
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.EMAIL_SUBJECT
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.GREEN_BOOK_CODE
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.GREEN_BOOK_URL
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.MARKET_URI_BASE
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.PLAY_STORE_URL_BASE
import com.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.RATE_CODE
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens
import com.etologic.mahjongscoring2.app.main.activity.MainViewModel.MainScreens.*
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import com.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_activity.*

object MainNavigator {
    
    internal fun goToScreen(screen: MainScreens, activity: MainActivity) {
        when (screen) {
            OLD_GAMES -> goToOldGames(activity)
            GAME -> goToGame(activity)
            COMBINATIONS -> goToCombinations(activity)
            GREEN_BOOK -> goToGreenBook(activity)
            RATE -> goToRate(activity)
            CONTACT -> goToContact(activity)
            FINISH -> activity.onBackPressed()
        }
    }
    
    private fun goToOldGames(activity: MainActivity) {
        activity.supportFragmentManager.beginTransaction().apply {
            if (isEmpty) {
                setCustomAnimations(enter_from_left, exit_to_right, enter_from_right, exit_to_left)
                add(frameLayoutMain, OldGamesFragment(), OldGamesFragment.TAG)
                commit()
            }
        }
    }
    
    private fun goToGame(activity: MainActivity) {
        val intent = Intent(activity, GameActivity::class.java)
        activity.goToActivity(intent, GameActivity.CODE)
    }
    
    private fun goToCombinations(activity: MainActivity) {
        val intent = Intent(activity, CombinationsActivity::class.java)
        activity.goToActivity(intent, CombinationsActivity.CODE)
    }
    
    private fun goToGreenBook(activity: MainActivity) {
        val intent = Intent(ACTION_VIEW, Uri.parse(GREEN_BOOK_URL))
        activity.goToActivity(intent, GREEN_BOOK_CODE)
    }
    
    private fun goToRate(activity: MainActivity) {
        val uriMarket = Uri.parse(MARKET_URI_BASE + APPLICATION_ID)
        var intent = Intent(ACTION_VIEW, uriMarket)
        if (Build.VERSION.SDK_INT >= LOLLIPOP)
            intent.addFlags(FLAG_ACTIVITY_NO_HISTORY or FLAG_ACTIVITY_NEW_DOCUMENT or FLAG_ACTIVITY_MULTIPLE_TASK)
        else {
            @Suppress("DEPRECATION")
            intent.addFlags(FLAG_ACTIVITY_NO_HISTORY or FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or FLAG_ACTIVITY_MULTIPLE_TASK)
        }
        try {
            activity.goToActivity(intent, RATE_CODE)
        } catch (e: Exception) {
            val uriPlayStore = Uri.parse(PLAY_STORE_URL_BASE + APPLICATION_ID)
            intent = Intent(ACTION_VIEW, uriPlayStore)
            activity.goToActivity(intent, RATE_CODE)
        }
        
    }
    
    private fun goToContact(activity: MainActivity) {
        val intent = Intent(ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
        intent.putExtra(EXTRA_SUBJECT, EMAIL_SUBJECT)
        if (intent.resolveActivity(activity.packageManager) != null)
            try {
                activity.goToActivity(intent, MainActivity.CONTACT_CODE)
            } catch (e: Exception) {
                Snackbar.make(activity.drawerLayoutMain, R.string.no_email_apps_founded, Snackbar.LENGTH_LONG).show()
            }
    }
}
