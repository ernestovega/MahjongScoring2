package es.etologic.mahjongscoring2.app.old_games;

import android.content.Context;
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
import es.etologic.mahjongscoring2.app.utils.StringUtils;
import es.etologic.mahjongscoring2.domain.entities.BestHand;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.app.utils.DateUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

class OldGamesRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Interface

    interface GameItemListener {
        void onOldGameItemDeleteClicked(long gameId);
        void onOldGameItemResumeClicked(long gameId);
    }

    //endregion

    //region Constants

    private static final int NUM_GAME_PLAYERS = 4;

    //endregion

    //region Fields

    private Context context;
    private GameItemListener itemClickListener;
    private List<Game> games;

    //endregion

    //region Constructor

    OldGamesRvAdapter() {
        games = new ArrayList<>();
    }

    //endregion

    //region Public

    void setOldGameItemListener(GameItemListener listener) {
        this.itemClickListener = listener;
    }

    void setGames(List<Game> newGames) {
        if (games == null) {
            saveNewGamesCopy(newGames);
            notifyItemRangeInserted(0, newGames.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(
                    new GameItemDiffUtilCallback(newGames, games), true);
            saveNewGamesCopy(newGames);
            result.dispatchUpdatesTo(this);
        }
    }

    //endregion

    //region ViewHolder

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private long gameId;
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
        @BindView (R.id.llOldGameItemItemRoundsNumber) LinearLayout llRoundNumberContainer;
        @BindView (R.id.tvOldGameItemRoundsNumber) TextView tvRoundNumber;
        @BindView (R.id.llOldGameItemItemBestHand) LinearLayout llBestHandContainer;
        @BindView (R.id.tvOldGameItemBestHandPlayerName) TextView tvBestHandPlayerName;
        @BindView (R.id.tvOldGameItemBestHandValue) TextView tvBestHandValue;

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
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.oldgame_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = ((ItemViewHolder)holder);
        final Game game = games.get(position);
        String[] playersTotalPoints = game.getPlayersTotalPoints();
        BestHand bestHand = game.getBestHand();

        //GameId
        itemViewHolder.gameId = game.getGameId();

        //Dates
        itemViewHolder.tvStartDate.setText(
                game.getStartDate() == null ? "-" : DateUtils.getPrettyDate(game.getStartDate()));
        itemViewHolder.tvEndDate.setText(
                game.getEndDate() == null ? "-" : DateUtils.getPrettyDate(game.getEndDate()));

        //Names
        itemViewHolder.tvEastPlayerName.setText(
                game.getNameP1() == null ? "-" : game.getNameP1());
        itemViewHolder.tvSouthPlayerName.setText(
                game.getNameP2() == null ? "-" : game.getNameP2());
        itemViewHolder.tvWestPlayerName.setText(
                game.getNameP3() == null ? "-" : game.getNameP3());
        itemViewHolder.tvNorthPlayerName.setText(
                game.getNameP4() == null ? "-" : game.getNameP4());

        //Points
        itemViewHolder.tvEastPlayerPoints.setText(
                playersTotalPoints[0] == null ? "-" : playersTotalPoints[0]);
        itemViewHolder.tvSouthPlayerPoints.setText(
                playersTotalPoints[1] == null ? "-" : playersTotalPoints[1]);
        itemViewHolder.tvWestPlayerPoints.setText(
                playersTotalPoints[2] == null ? "-" : playersTotalPoints[2]);
        itemViewHolder.tvNorthPlayerPoints.setText(
                playersTotalPoints[3] == null ? "-" : playersTotalPoints[3]);

        //Rounds
        if(game.getRounds() == null || game.getRounds().isEmpty()) {
            itemViewHolder.llRoundNumberContainer.setVisibility(GONE);
        } else {
            itemViewHolder.llRoundNumberContainer.setVisibility(VISIBLE);
            itemViewHolder.tvRoundNumber.setText(String.valueOf(game.getRounds().size()));
        }

        //Best Hand
        if(bestHand == null || StringUtils.isEmpty(bestHand.getPlayerName()) ||
                bestHand.getHandValue() <= 0) {
            itemViewHolder.llBestHandContainer.setVisibility(GONE);
        } else {
            itemViewHolder.llBestHandContainer.setVisibility(VISIBLE);
            itemViewHolder.tvBestHandPlayerName.setText(bestHand.getPlayerName());
            itemViewHolder.tvBestHandValue.setText(String.valueOf(bestHand.getHandValue()));
        }
    }

    //endregion

    //region Private

    private void saveNewGamesCopy(List<Game> newGames) {
        List<Game> newGamesCopy = new ArrayList<>(newGames.size());
        for(Game game : newGames) {
            newGamesCopy.add(game.getCopy());
        }
        games = newGamesCopy;
    }

    //endregion
}