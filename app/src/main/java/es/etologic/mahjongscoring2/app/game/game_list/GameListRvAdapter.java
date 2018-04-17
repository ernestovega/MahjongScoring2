package es.etologic.mahjongscoring2.app.game.game_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.domain.entities.Round;

/*import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;*/

class GameListRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        /*extends RecyclerSwipeAdapter*/ {

    //region Fields

    private Context context;
    private List<Round> rounds;

    //endregion

    //region Public

    void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    //endregion

    //region ViewHolder
    @Override
    public int getItemCount() {
        return rounds.size();
    }

    //endregion

    //region Lifecycle
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.game_list_round_item, parent, false);
        return new ItemViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Round item = rounds.get(position);
        final ItemViewHolder myHolder = (ItemViewHolder) holder;
        fillTexts(item, myHolder);
        setWinnerColor(item, myHolder);
        setLooserColor(item, myHolder);
        setPenaltiesIcons(item, myHolder);
        setEvents(myHolder);
    }
    private void fillTexts(Round item, ItemViewHolder mHolder) {
        mHolder.tvRoundNum.setText(String.valueOf(item.getRoundId()));
        mHolder.tvHandPoints.setText(String.valueOf(item.getHandPoints()));
        mHolder.tvPointsP1.setText(String.valueOf(item.getPointsP1()));
        mHolder.tvPointsP2.setText(String.valueOf(item.getPointsP2()));
        mHolder.tvPointsP3.setText(String.valueOf(item.getPointsP3()));
        mHolder.tvPointsP4.setText(String.valueOf(item.getPointsP4()));
    }

    /*@Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }*/

    //endregion

    //region Private
    private void setWinnerColor(Round item, ItemViewHolder mHolder) {
        if(item.getWinnerInitialPosition() > 0) {
            int greenMM = ContextCompat.getColor(context, R.color.colorPrimary);
            switch(item.getWinnerInitialPosition()) {
                case 1:
                    mHolder.tvPointsP1.setTextColor(greenMM);
                    break;
                case 2:
                    mHolder.tvPointsP2.setTextColor(greenMM);
                    break;
                case 3:
                    mHolder.tvPointsP3.setTextColor(greenMM);
                    break;
                case 4:
                    mHolder.tvPointsP4.setTextColor(greenMM);
                    break;
            }
        }
    }
    private void setLooserColor(Round item, ItemViewHolder mHolder) {
        if(item.getLooserInitialPosition() > 0) {
            int red = ContextCompat.getColor(context, R.color.red);
            switch(item.getLooserInitialPosition()) {
                case 1:
                    mHolder.tvPointsP1.setTextColor(red);
                    break;
                case 2:
                    mHolder.tvPointsP2.setTextColor(red);
                    break;
                case 3:
                    mHolder.tvPointsP3.setTextColor(red);
                    break;
                case 4:
                    mHolder.tvPointsP4.setTextColor(red);
                    break;
            }
        }
    }
    private void setPenaltiesIcons(Round item, ItemViewHolder mHolder) {
        mHolder.ivPenaltyP1.setVisibility(item.getPenaltyP1() > 0 ? View.VISIBLE : View.GONE);
        mHolder.ivPenaltyP2.setVisibility(item.getPenaltyP2() > 0 ? View.VISIBLE : View.GONE);
        mHolder.ivPenaltyP3.setVisibility(item.getPenaltyP3() > 0 ? View.VISIBLE : View.GONE);
        mHolder.ivPenaltyP4.setVisibility(item.getPenaltyP4() > 0 ? View.VISIBLE : View.GONE);
    }
    private void setEvents(ItemViewHolder myHolder) {
        //ToDo!
        myHolder.ibDeleteItem.setOnClickListener(v ->
                                                         Toast.makeText(context, "Delete Round clicked!", Toast.LENGTH_LONG).show());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        //        @BindView (R.id.slGameList) SwipeLayout swipeLayout;
        @BindView(R.id.ibDeleteItem)
        ImageButton ibDeleteItem;
        @BindView(R.id.tvGameListItemRoundNumber)
        TextView tvRoundNum;
        @BindView(R.id.tvGameListItemHandPoints)
        TextView tvHandPoints;
        @BindView(R.id.tvGameListItemRoundPointsP1)
        TextView tvPointsP1;
        @BindView(R.id.tvGameListItemRoundPointsP2)
        TextView tvPointsP2;
        @BindView(R.id.tvGameListItemRoundPointsP3)
        TextView tvPointsP3;
        @BindView(R.id.tvGameListItemRoundPointsP4)
        TextView tvPointsP4;
        @BindView(R.id.ivGameListItemPenaltyIconP1)
        ImageView ivPenaltyP1;
        @BindView(R.id.ivGameListItemPenaltyIconP2)
        ImageView ivPenaltyP2;
        @BindView(R.id.ivGameListItemPenaltyIconP3)
        ImageView ivPenaltyP3;
        @BindView(R.id.ivGameListItemPenaltyIconP4)
        ImageView ivPenaltyP4;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    //endregion
}
