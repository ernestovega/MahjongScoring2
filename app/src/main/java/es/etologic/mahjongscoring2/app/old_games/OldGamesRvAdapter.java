package es.etologic.mahjongscoring2.app.old_games;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.domain.entities.OldGame;

class OldGamesRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Interface

    interface OldGameItemListener {
        void onOldGameItemDeleteClicked(int gameId);
        void onOldGameItemResumeClicked(int gameId);
    }

    //endregion

    //region Fields

    private OldGameItemListener itemClickListener;
    private List<OldGame> oldGames;

    //endregion

    //region Constructor

    OldGamesRvAdapter() {
        oldGames = new ArrayList<>();
    }

    //endregion

    //region Public

    void setOldGameItemListener(OldGameItemListener listener) {
        this.itemClickListener = listener;
    }

    void setOldGames(List<OldGame> oldGames) {
        this.oldGames = oldGames;
    }

    //endregion

    //region ViewHolder

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private int gameId;
        @BindView (R.id.tvOldGameItemStartDate) TextView tvStartDate;
        @BindView (R.id.tvOldGameItemEndDate) TextView tvEndDate;
        @BindView (R.id.tvOlgGameItemPlayerEastName) TextView tvEastPlayerName;
        @BindView (R.id.tvOlgGameItemPlayerSouthName) TextView tvSouthPlayerName;
        @BindView (R.id.tvOlgGameItemPlayerWestName) TextView tvWestPlayerName;
        @BindView (R.id.tvOlgGameItemPlayerNorthName) TextView tvNorthPlayerName;
        @BindView (R.id.tvOldGameItemPlayerEastPoints) TextView tvEastPlayerPoints;
        @BindView (R.id.tvOldGameItemPlayerSouthPoints) TextView tvSouthPlayerPoints;
        @BindView (R.id.tvOldGameItemPlayerWestPoints) TextView tvWestPlayerPoints;
        @BindView (R.id.tvOldGameItemPlayerNorthPoints) TextView tvNorthPlayerPoints;
        @BindView (R.id.tvOldGameItemBestHand) TextView tvBestHand;
        @BindView (R.id.tvOldGameItemRoundsNumber) TextView tvRoundNumber;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.btOldGameItemDelete) void onDeleteClick() {
            if(itemClickListener != null) {
                itemClickListener.onOldGameItemDeleteClicked(gameId);
            }
        }

        @OnClick(R.id.btOldGameItemResume) void onResumeClick() {
            if(itemClickListener != null) {
                itemClickListener.onOldGameItemResumeClicked(gameId);
            }
        }
    }

    //endregion

    //region Lifecycle

    @Override
    public int getItemCount() {
        return oldGames.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.oldgame_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OldGame oldGame = oldGames.get(position);
        ItemViewHolder itemViewHolder = ((ItemViewHolder)holder);

        itemViewHolder.gameId = oldGame.getId();
        itemViewHolder.tvStartDate.setText(oldGame.getStartDate());
        itemViewHolder.tvEndDate.setText(oldGame.getEndDate());
        itemViewHolder.tvEastPlayerName.setText(oldGame.getNameP1());
        itemViewHolder.tvSouthPlayerName.setText(oldGame.getNameP2());
        itemViewHolder.tvWestPlayerName.setText(oldGame.getNameP3());
        itemViewHolder.tvNorthPlayerName.setText(oldGame.getNameP4());
        itemViewHolder.tvEastPlayerPoints.setText(oldGame.getPointsP1());
        itemViewHolder.tvSouthPlayerPoints.setText(oldGame.getPointsP2());
        itemViewHolder.tvWestPlayerPoints.setText(oldGame.getPointsP3());
        itemViewHolder.tvNorthPlayerPoints.setText(oldGame.getPointsP4());
        itemViewHolder.tvRoundNumber.setText(oldGame.getRoundsNumber());
        itemViewHolder.tvEndDate.setText(oldGame.getEndDate());
        itemViewHolder.tvBestHand.setText(oldGame.getBestHand());
    }

    //endregion
}