package es.etologic.mahjongscoring2.app.model;

import android.support.v7.util.DiffUtil;

import java.util.List;

import es.etologic.mahjongscoring2.app.utils.DateTimeUtils;
import es.etologic.mahjongscoring2.domain.entities.BestHand;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Round;

public class GameItemDiffUtilCallback extends DiffUtil.Callback {

    private static final int NUM_GAME_PLAYERS = 4;
    private List<Game> oldList;
    private List<Game> newList;

    public GameItemDiffUtilCallback(List<Game> newList, List<Game> oldList) {
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
        return oldList.get(oldItemPosition).getGameId() ==
                       newList.get(newItemPosition).getGameId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Game newGame = newList.get(newItemPosition);
        Game oldGame = oldList.get(oldItemPosition);
        return oldGame.getGameId() == newGame.getGameId() &&
                       oldGame.getNameP1().equals(newGame.getNameP1()) &&
                       oldGame.getNameP2().equals(newGame.getNameP2()) &&
                       oldGame.getNameP3().equals(newGame.getNameP3()) &&
                       oldGame.getNameP4().equals(newGame.getNameP4()) &&
                       DateTimeUtils.areEqual(oldGame.getCreationDate(), newGame.getCreationDate()) &&
                       arePlayersTotalsPointsEquals(oldGame.getPlayersTotalPoints(),
                               newGame.getPlayersTotalPoints()) &&
                       areBestHandsEqual(oldGame.getBestHand(), newGame.getBestHand())
                       && Round.areEqual(oldGame.getRounds(), newGame.getRounds());
    }

    private boolean arePlayersTotalsPointsEquals(String[] oldPlayersTotalPoints,
                                                 String[] newPlayersTotalPoints) {
        for(int i = 0; i < NUM_GAME_PLAYERS; i++) {
            if(!oldPlayersTotalPoints[i].equals(newPlayersTotalPoints[i])) {
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
