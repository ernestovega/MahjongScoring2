package es.etologic.mahjongscoring2.app.old_games;

import android.support.v7.util.DiffUtil;
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
import es.etologic.mahjongscoring2.domain.entities.BestHand;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.utils.DateUtils;

class OldGamesRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Interface

    interface GameItemListener {
        void onOldGameItemDeleteClicked(int gameId);
        void onOldGameItemResumeClicked(int gameId);
    }

    //endregion

    //region Fields

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
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return games.size();
                }

                @Override
                public int getNewListSize() {
                    return newGames.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return games.get(oldItemPosition).getGameId() ==
                            newGames.get(newItemPosition).getGameId() &&
                            games.get(oldItemPosition).getNameP1().equals(
                                    newGames.get(newItemPosition).getNameP1()) &&
                            games.get(oldItemPosition).getNameP2().equals(
                                    newGames.get(newItemPosition).getNameP2()) &&
                            games.get(oldItemPosition).getNameP3().equals(
                                    newGames.get(newItemPosition).getNameP3()) &&
                            games.get(oldItemPosition).getNameP4().equals(
                                    newGames.get(newItemPosition).getNameP4()) &&
                            games.get(oldItemPosition).getStartDate().equals(
                                    newGames.get(newItemPosition).getStartDate()) &&
                            games.get(oldItemPosition).getEndDate().equals(
                                    newGames.get(newItemPosition).getEndDate()) &&
                            games.get(oldItemPosition).getRounds() ==
                                    newGames.get(newItemPosition).getRounds();
                            //TODO: Mejorar comparacion de rounds
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Game newGame = newGames.get(newItemPosition);
                    Game oldGame = games.get(oldItemPosition);
                    return newGame.getGameId() == oldGame.getGameId() &&
                            newGame.getNameP1().equals(oldGame.getNameP1()) &&
                            newGame.getNameP2().equals(oldGame.getNameP2()) &&
                            newGame.getNameP3().equals(oldGame.getNameP3()) &&
                            newGame.getNameP4().equals(oldGame.getNameP4()) &&
                            newGame.getStartDate().equals(oldGame.getStartDate()) &&
                            newGame.getEndDate().equals(oldGame.getEndDate()) &&
                            newGame.getRounds() == oldGame.getRounds();
                            //TODO: Mejorar comparacion de rounds
                    }
            });
            saveNewGamesCopy(newGames);
            result.dispatchUpdatesTo(this);
        }
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

        itemViewHolder.gameId = game.getGameId();
        itemViewHolder.tvStartDate.setText(DateUtils.getPrettyDate(game.getStartDate()));
        itemViewHolder.tvEndDate.setText(DateUtils.getPrettyDate(game.getEndDate()));
        itemViewHolder.tvEastPlayerName.setText(game.getNameP1());
        itemViewHolder.tvSouthPlayerName.setText(game.getNameP2());
        itemViewHolder.tvWestPlayerName.setText(game.getNameP3());
        itemViewHolder.tvNorthPlayerName.setText(game.getNameP4());
        itemViewHolder.tvEastPlayerPoints.setText(playersTotalPoints[0]);
        itemViewHolder.tvSouthPlayerPoints.setText(playersTotalPoints[0]);
        itemViewHolder.tvWestPlayerPoints.setText(playersTotalPoints[0]);
        itemViewHolder.tvNorthPlayerPoints.setText(playersTotalPoints[0]);
        itemViewHolder.tvRoundNumber.setText(String.valueOf(game.getRounds().size()));
        itemViewHolder.tvBestHand.setText(
                String.format("%s\n%s", bestHand.getHandValue(), bestHand.getPlayerName()));
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