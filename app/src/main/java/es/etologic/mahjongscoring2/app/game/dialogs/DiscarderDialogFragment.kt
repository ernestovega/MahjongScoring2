package es.etologic.mahjongscoring2.app.game.dialogs

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.R.drawable
import es.etologic.mahjongscoring2.app.game.base.BaseGameDialogFragment
import es.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment
import es.etologic.mahjongscoring2.app.game.game_table.GameTableSeatsFragment.TableSeatsListener
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.players_dialog_fragment.*

internal class DiscarderDialogFragment : BaseGameDialogFragment() {
    
    private lateinit var tableSeats: GameTableSeatsFragment
    private var eastIcon: Drawable? = null
    private var southIcon: Drawable? = null
    private var westIcon: Drawable? = null
    private var northIcon: Drawable? = null
    
    companion object {
        const val TAG = "DiscarderDialogFragment"
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.discarder_dialog_fragment, container, false)
        initResources(inflater)
        initTableSeats()
        setOnClickListeners()
        return view
    }
    
    private fun initResources(inflater: LayoutInflater) {
        eastIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_east)
        southIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_south)
        westIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_west)
        northIcon = ContextCompat.getDrawable(inflater.context, drawable.ic_north)
    }
    
    private fun initTableSeats() {
        tableSeats = childFragmentManager.findFragmentByTag(GameTableSeatsFragment.TAG) as GameTableSeatsFragment
    }
    
    private fun setOnClickListeners() {
        tableSeats.setTableSeatsListener(object : TableSeatsListener {
            override fun onEastSeatClick() {}
            override fun onSouthSeatClick() {}
            override fun onWestSeatClick() {}
            override fun onNorthSeatClick() {}
        })
    }
    
    internal fun setNames(names: Array<String>?) {
        if (names?.size == 4) {
            tietPlayersDialogEast.setText(names[EAST.code])
            tietPlayersDialogSouth.setText(names[SOUTH.code])
            tietPlayersDialogWest.setText(names[WEST.code])
            tietPlayersDialogNorth.setText(names[NORTH.code])
        }
    }
}
