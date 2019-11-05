package es.etologic.mahjongscoring2.app.game.game_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.etologic.mahjongscoring2.R
import es.etologic.mahjongscoring2.app.base.BaseRvAdapter
import es.etologic.mahjongscoring2.domain.model.Round
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.*
import kotlinx.android.synthetic.main.game_list_round_item.view.*

internal class GameListRvAdapter(context: Context) : BaseRvAdapter<Round>() {
    
    //Fields
    private val greenMM: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private val grayMM: Int = ContextCompat.getColor(context, R.color.grayMM)
    private val red: Int = ContextCompat.getColor(context, R.color.red)
    private val accent: Int = ContextCompat.getColor(context, R.color.colorAccent)
    
    //Lifecycle
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_list_round_item, parent, false)
        return ItemViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val round = collection[position]
        val myHolder = holder as ItemViewHolder
        fillTexts(round, myHolder)
        setWinnerColor(round, myHolder)
        setLooserColor(round, myHolder)
        setPenaltiesIcons(round, myHolder)
    }
    
    private fun fillTexts(item: Round, mHolder: ItemViewHolder) {
        mHolder.tvRoundNum.text = item.roundId.toString()
        mHolder.tvHandPoints.text = item.handPoints.toString()
        mHolder.tvHandPoints.setTextColor(if (item.isBestHand) accent else grayMM)
        mHolder.tvPointsP1.text = item.pointsP1.toString()
        mHolder.tvPointsP2.text = item.pointsP2.toString()
        mHolder.tvPointsP3.text = item.pointsP3.toString()
        mHolder.tvPointsP4.text = item.pointsP4.toString()
    }
    
    private fun setWinnerColor(item: Round, mHolder: ItemViewHolder) {
        mHolder.tvPointsP1.setTextColor(if (item.winnerInitialPosition == EAST) greenMM else grayMM)
        mHolder.tvPointsP2.setTextColor(if (item.winnerInitialPosition == SOUTH) greenMM else grayMM)
        mHolder.tvPointsP3.setTextColor(if (item.winnerInitialPosition == WEST) greenMM else grayMM)
        mHolder.tvPointsP4.setTextColor(if (item.winnerInitialPosition == NORTH) greenMM else grayMM)
    }
    
    private fun setLooserColor(item: Round, mHolder: ItemViewHolder) {
        when (item.discarderInitialPosition) {
            EAST -> mHolder.tvPointsP1.setTextColor(red)
            SOUTH -> mHolder.tvPointsP2.setTextColor(red)
            WEST -> mHolder.tvPointsP3.setTextColor(red)
            else -> mHolder.tvPointsP4.setTextColor(red)
        }
    }
    
    private fun setPenaltiesIcons(item: Round, mHolder: ItemViewHolder) {
        mHolder.ivPenaltyP1.visibility = if (item.penaltyP1 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP2.visibility = if (item.penaltyP2 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP3.visibility = if (item.penaltyP3 < 0) VISIBLE else GONE
        mHolder.ivPenaltyP4.visibility = if (item.penaltyP4 < 0) VISIBLE else GONE
    }
    /*@Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }*/
    //VIEWHOLDER
    internal inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        
        //@BindView (R.id.slGameList) SwipeLayout swipeLayout;
        //@BindView(R.id.ibDeleteItem) ImageButton ibDeleteItem;
        var tvRoundNum: TextView = view.tvGameListItemRoundNumber
        var tvHandPoints: TextView = view.tvGameListItemHandPoints
        var tvPointsP1: TextView = view.tvGameListItemRoundPointsP1
        var tvPointsP2: TextView = view.tvGameListItemRoundPointsP2
        var tvPointsP3: TextView = view.tvGameListItemRoundPointsP3
        var tvPointsP4: TextView = view.tvGameListItemRoundPointsP4
        var ivPenaltyP1: ImageView = view.ivGameListItemPenaltyIconP1
        var ivPenaltyP2: ImageView = view.ivGameListItemPenaltyIconP2
        var ivPenaltyP3: ImageView = view.ivGameListItemPenaltyIconP3
        var ivPenaltyP4: ImageView = view.ivGameListItemPenaltyIconP4
    }
    
}
