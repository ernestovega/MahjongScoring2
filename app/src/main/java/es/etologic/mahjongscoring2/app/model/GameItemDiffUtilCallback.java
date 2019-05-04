package es.etologic.mahjongscoring2.app.model;

import android.support.v7.util.DiffUtil;

import java.util.List;

import es.etologic.mahjongscoring2.app.utils.DateTimeUtils;
import es.etologic.mahjongscoring2.domain.model.BestHand;
import es.etologic.mahjongscoring2.domain.model.GameWithRounds;
import es.etologic.mahjongscoring2.domain.model.Round;

public class GameItemDiffUtilCallback extends DiffUtil.Callback {

    private static final int NUM_GAME_PLAYERS = 4;
    private List<GameWithRounds> oldList;
    private List<GameWithRounds> newList;

    public GameItemDiffUtilCallback(List<GameWithRounds> newList, List<GameWithRounds> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getGame().getGameId() ==
                newList.get(newItemPosition).getGame().getGameId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        GameWithRounds newGame = newList.get(newItemPosition);
        GameWithRounds oldGame = oldList.get(oldItemPosition);
        return oldGame.getGame().getGameId() == newGame.getGame().getGameId() &&
                oldGame.getGame().getNameP1().equals(newGame.getGame().getNameP1()) &&
                oldGame.getGame().getNameP2().equals(newGame.getGame().getNameP2()) &&
                oldGame.getGame().getNameP3().equals(newGame.getGame().getNameP3()) &&
                oldGame.getGame().getNameP4().equals(newGame.getGame().getNameP4()) &&
                DateTimeUtils.areEqual(oldGame.getGame().getStartDate(), newGame.getGame().getStartDate()) &&
                arePlayersTotalsPointsEquals(oldGame.getPlayersTotalPointsString(),
                        newGame.getPlayersTotalPointsString()) &&
                areBestHandsEqual(oldGame.getBestHand(), newGame.getBestHand())
                && Round.areEqual(oldGame.getRounds(), newGame.getRounds());
    }

    private boolean arePlayersTotalsPointsEquals(String[] oldPlayersTotalPoints,
                                                 String[] newPlayersTotalPoints) {
        for (int i = 0; i < NUM_GAME_PLAYERS; i++) {
            if (!oldPlayersTotalPoints[i].equals(newPlayersTotalPoints[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean areBestHandsEqual(BestHand oldBestHand, BestHand newBestHand) {
        return oldBestHand.getPlayerName().equals(newBestHand.getPlayerName()) &&
                oldBestHand.getHandValue() == newBestHand.getHandValue();
    }
}
