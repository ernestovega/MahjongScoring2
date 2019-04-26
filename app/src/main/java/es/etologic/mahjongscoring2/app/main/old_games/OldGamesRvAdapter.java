package es.etologic.mahjongscoring2.app.main.old_games;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.model.GameItemDiffUtilCallback;
import es.etologic.mahjongscoring2.app.utils.DateTimeUtils;
import es.etologic.mahjongscoring2.app.utils.StringUtils;
import es.etologic.mahjongscoring2.domain.model.BestHand;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.resolveSize;

class OldGamesRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //INTERFACE
    interface GameItemListener {
        void onOldGameItemDeleteClicked(long gameId);
        void onOldGameItemResumeClicked(long gameId);
    }

    //FIELDS
    private GameItemListener itemClickListener;
    private List<GameWithRounds> games;

    //CONTRUCTOR
    OldGamesRvAdapter() {
        games = new ArrayList<>();
    }

    //METHODS
    void setOldGameItemListener(GameItemListener listener) {
        this.itemClickListener = listener;
    }
    void setGames(List<GameWithRounds> newGames) {
        if(games == null) {
            saveNewGamesCopy(newGames);
            notifyItemRangeInserted(0, newGames.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                    new GameItemDiffUtilCallback(newGames, games), true);
            saveNewGamesCopy(newGames);
            result.dispatchUpdatesTo(this);
        }
    }
    private void saveNewGamesCopy(List<GameWithRounds> newGames) {
        List<GameWithRounds> newGamesCopy = new ArrayList<>(newGames.size());
        for(GameWithRounds gameWithRounds : newGames) {
            newGamesCopy.add(gameWithRounds.getCopy());
        }
        games = newGamesCopy;
    }

    //LIFECYCLE
    @Override
    public int getItemCount() {
        return games.size();
    }
    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.oldgame_item, parent, false);
        return new OldGameItemViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OldGameItemViewHolder itemViewHolder = ((OldGameItemViewHolder) holder);
        final GameWithRounds gameWithRounds = games.get(position);
        long duration = gameWithRounds.getDuration();
        BestHand bestHand = gameWithRounds.getBestHand();
        setFields(itemViewHolder, gameWithRounds, duration, bestHand);
    }
    private void setFields(OldGameItemViewHolder itemViewHolder, GameWithRounds gameWithRounds, long duration, BestHand bestHand) {
        itemViewHolder.gameId = gameWithRounds.getGame().getGameId();
        itemViewHolder.tvStartDate.setText(gameWithRounds.getGame().getCreationDate() == null ? "-" : DateTimeUtils.getPrettyDate(gameWithRounds.getGame().getCreationDate()));
        itemViewHolder.tvDuration.setText(DateTimeUtils.getPrettyTime(duration));
        itemViewHolder.tvEastPlayerName.setText(gameWithRounds.getGame().getNameP1() == null ? "-" : gameWithRounds.getGame().getNameP1());
        itemViewHolder.tvSouthPlayerName.setText(gameWithRounds.getGame().getNameP2() == null ? "-" : gameWithRounds.getGame().getNameP2());
        itemViewHolder.tvWestPlayerName.setText(gameWithRounds.getGame().getNameP3() == null ? "-" : gameWithRounds.getGame().getNameP3());
        itemViewHolder.tvNorthPlayerName.setText(gameWithRounds.getGame().getNameP4() == null ? "-" : gameWithRounds.getGame().getNameP4());
        String[] playersTotalPoints = gameWithRounds.getPlayersTotalPointsString();
        itemViewHolder.tvEastPlayerPoints.setText(playersTotalPoints[0] == null ? "-" : playersTotalPoints[0]);
        itemViewHolder.tvSouthPlayerPoints.setText(playersTotalPoints[1] == null ? "-" : playersTotalPoints[1]);
        itemViewHolder.tvWestPlayerPoints.setText(playersTotalPoints[2] == null ? "-" : playersTotalPoints[2]);
        itemViewHolder.tvNorthPlayerPoints.setText(playersTotalPoints[3] == null ? "-" : playersTotalPoints[3]);
        itemViewHolder.tvRoundNumber.setText(String.valueOf(gameWithRounds.getRounds().size()));
        setBestHand(itemViewHolder, bestHand);
    }
    private void setBestHand(OldGameItemViewHolder itemViewHolder, BestHand bestHand) {
        if(bestHand == null || StringUtils.isEmpty(bestHand.getPlayerName()) ||
                   bestHand.getHandValue() <= 0) {
            itemViewHolder.llBestHandContainer.setVisibility(GONE);
        } else {
            itemViewHolder.llBestHandContainer.setVisibility(VISIBLE);
            itemViewHolder.tvBestHandPlayerName.setText(bestHand.getPlayerName());
            itemViewHolder.tvBestHandValue.setText(String.valueOf(bestHand.getHandValue()));
        }
    }

    //VIEWHOLDER
    class OldGameItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvOldGameItemStartDate) TextView tvStartDate;
        @BindView(R.id.tvOldGameItemDuration) TextView tvDuration;
        @BindView(R.id.tvOlgGameItemPlayerEastName) TextView tvEastPlayerName;
        @BindView(R.id.tvOlgGameItemPlayerSouthName) TextView tvSouthPlayerName;
        @BindView(R.id.tvOlgGameItemPlayerWestName) TextView tvWestPlayerName;
        @BindView(R.id.tvOlgGameItemPlayerNorthName) TextView tvNorthPlayerName;
        @BindView(R.id.tvOldGameItemPlayerEastPoints) TextView tvEastPlayerPoints;
        @BindView(R.id.tvOldGameItemPlayerSouthPoints) TextView tvSouthPlayerPoints;
        @BindView(R.id.tvOldGameItemPlayerWestPoints) TextView tvWestPlayerPoints;
        @BindView(R.id.tvOldGameItemPlayerNorthPoints) TextView tvNorthPlayerPoints;
        @BindView(R.id.tvOldGameItemRoundsNumber) TextView tvRoundNumber;
        @BindView(R.id.llOldGameItemItemBestHand) LinearLayout llBestHandContainer;
        @BindView(R.id.tvOldGameItemBestHandPlayerName) TextView tvBestHandPlayerName;
        @BindView(R.id.tvOldGameItemBestHandValue) TextView tvBestHandValue;
        private long gameId;

        OldGameItemViewHolder(View view) {
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
}