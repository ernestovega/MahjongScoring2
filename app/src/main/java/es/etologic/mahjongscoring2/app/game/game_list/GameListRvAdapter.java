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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.base.BaseRvAdapter;
import es.etologic.mahjongscoring2.domain.model.Round;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.EAST;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NORTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.SOUTH;
import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.WEST;

/*import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;*/

class GameListRvAdapter extends BaseRvAdapter<Round> {

    //Fields
    private int greenMM;
    private int grayMM;
    private int red;

    //Constructor
    @Inject
    GameListRvAdapter(Context context) {
        greenMM = ContextCompat.getColor(context, R.color.colorPrimary);
        grayMM = ContextCompat.getColor(context, R.color.grayMM);
        red = ContextCompat.getColor(context, R.color.red);
    }

    //Lifecycle
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_round_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Round round = collection.get(position);
        final ItemViewHolder myHolder = (ItemViewHolder) holder;
        fillTexts(round, myHolder);
        setWinnerColor(round, myHolder);
        setLooserColor(round, myHolder);
        setPenaltiesIcons(round, myHolder);
    }
    private void fillTexts(Round item, ItemViewHolder mHolder) {
        mHolder.tvRoundNum.setText(String.valueOf(item.getRoundId()));
        mHolder.tvHandPoints.setText(String.valueOf(item.getHandPoints()));
        mHolder.tvPointsP1.setText(String.valueOf(item.getPointsP1()));
        mHolder.tvPointsP2.setText(String.valueOf(item.getPointsP2()));
        mHolder.tvPointsP3.setText(String.valueOf(item.getPointsP3()));
        mHolder.tvPointsP4.setText(String.valueOf(item.getPointsP4()));
    }
    private void setWinnerColor(Round item, ItemViewHolder mHolder) {
        mHolder.tvPointsP1.setTextColor(item.getWinnerInitialPosition() == EAST ?  greenMM : grayMM);
        mHolder.tvPointsP2.setTextColor(item.getWinnerInitialPosition() == SOUTH ? greenMM : grayMM);
        mHolder.tvPointsP3.setTextColor(item.getWinnerInitialPosition() == WEST ?  greenMM : grayMM);
        mHolder.tvPointsP4.setTextColor(item.getWinnerInitialPosition() == NORTH ? greenMM : grayMM);
    }
    private void setLooserColor(Round item, ItemViewHolder mHolder) {
        switch(item.getDiscarderInitialPosition()) {
            case EAST: mHolder.tvPointsP1.setTextColor(red); break;
            case SOUTH: mHolder.tvPointsP2.setTextColor(red); break;
            case WEST: mHolder.tvPointsP3.setTextColor(red); break;
            case NORTH: mHolder.tvPointsP4.setTextColor(red); break;
        }
    }
    private void setPenaltiesIcons(Round item, ItemViewHolder mHolder) {
        mHolder.ivPenaltyP1.setVisibility(item.getPenaltyP1() > 0 ? VISIBLE : GONE);
        mHolder.ivPenaltyP2.setVisibility(item.getPenaltyP2() > 0 ? VISIBLE : GONE);
        mHolder.ivPenaltyP3.setVisibility(item.getPenaltyP3() > 0 ? VISIBLE : GONE);
        mHolder.ivPenaltyP4.setVisibility(item.getPenaltyP4() > 0 ? VISIBLE : GONE);
    }

    /*@Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }*/

    //VIEWHOLDER
    class ItemViewHolder extends RecyclerView.ViewHolder {

        //@BindView (R.id.slGameList) SwipeLayout swipeLayout;
        @BindView(R.id.ibDeleteItem) ImageButton ibDeleteItem;
        @BindView(R.id.tvGameListItemRoundNumber) TextView tvRoundNum;
        @BindView(R.id.tvGameListItemHandPoints) TextView tvHandPoints;
        @BindView(R.id.tvGameListItemRoundPointsP1) TextView tvPointsP1;
        @BindView(R.id.tvGameListItemRoundPointsP2) TextView tvPointsP2;
        @BindView(R.id.tvGameListItemRoundPointsP3) TextView tvPointsP3;
        @BindView(R.id.tvGameListItemRoundPointsP4) TextView tvPointsP4;
        @BindView(R.id.ivGameListItemPenaltyIconP1) ImageView ivPenaltyP1;
        @BindView(R.id.ivGameListItemPenaltyIconP2) ImageView ivPenaltyP2;
        @BindView(R.id.ivGameListItemPenaltyIconP3) ImageView ivPenaltyP3;
        @BindView(R.id.ivGameListItemPenaltyIconP4) ImageView ivPenaltyP4;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
