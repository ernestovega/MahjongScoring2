package es.etologic.mahjongscoring2.app.main.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import com.google.android.material.snackbar.Snackbar
import es.etologic.mahjongscoring2.BuildConfig
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.game.activity.GameActivity
import es.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.EMAIL_ADDRESS
import es.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.EMAIL_SUBJECT
import es.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.GREEN_BOOK_URL
import es.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.MARKET_URI_BASE
import es.etologic.mahjongscoring2.app.main.activity.MainActivity.Companion.PLAY_STORE_URL_BASE
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens
import es.etologic.mahjongscoring2.app.main.activity.MainActivityViewModel.MainScreens.*
import es.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import es.etologic.mahjongscoring2.app.main.old_games.OldGamesFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainNavigator {
    
    companion object {
    
        fun goToScreen(screen: MainScreens, activity: MainActivity) {
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
            val supportFragmentManager = activity.supportFragmentManager
            if (supportFragmentManager.backStackEntryCount == 0) {
                val fragment = OldGamesFragment()
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                fragmentTransaction.add(R.id.frameLayoutMain, fragment).addToBackStack(null)
                fragmentTransaction.commit()
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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GREEN_BOOK_URL))
            activity.goToActivity(intent, MainActivity.GREEN_BOOK_CODE)
        }
    
        private fun goToRate(activity: MainActivity) {
            val uriMarket = Uri.parse(MARKET_URI_BASE + BuildConfig.APPLICATION_ID)
            var intent = Intent(Intent.ACTION_VIEW, uriMarket)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            else {
                @Suppress("DEPRECATION")
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            try {
                activity.goToActivity(intent, MainActivity.RATE_CODE)
            } catch (e: Exception) {
                val uriPlayStore = Uri.parse(PLAY_STORE_URL_BASE + BuildConfig.APPLICATION_ID)
                intent = Intent(Intent.ACTION_VIEW, uriPlayStore)
                activity.goToActivity(intent, MainActivity.RATE_CODE)
            }
        
        }
    
        private fun goToContact(activity: MainActivity) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
            intent.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)
            if (intent.resolveActivity(activity.packageManager) != null)
                try {
                    activity.goToActivity(intent, MainActivity.CONTACT_CODE)
                } catch (e: Exception) {
                    Snackbar.make(activity.drawerLayoutMain, R.string.no_email_apps_founded, Snackbar.LENGTH_LONG).show()
                }
        }
    }
}