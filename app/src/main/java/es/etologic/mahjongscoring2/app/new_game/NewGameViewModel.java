package es.etologic.mahjongscoring2.app.new_game;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import es.etologic.mahjongscoring2.app.base.BaseViewModel;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.entities.Player;

import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;

class NewGameViewModel extends BaseViewModel {

    //FIELDS
    private PlayersRepository playersRepository;
    private GamesRepository gamesRepository;
    private MutableLiveData<List<Player>> allPlayers = new MutableLiveData<List<Player>>() {};
    private MutableLiveData<Player> newPlayer = new MutableLiveData<Player>() {};
    private MutableLiveData<Long> newGameId = new MutableLiveData<Long>() {};
    private MutableLiveData<ShowState> toolbarProgress = new MutableLiveData<ShowState>() {};

    //GETTERS (OBSERVABLES)
    MutableLiveData<List<Player>> getAllPlayers() { return allPlayers; }
    MutableLiveData<Player> getNewPlayer() { return newPlayer; }
    MutableLiveData<Long> getNewGameId() { return newGameId; }
    MutableLiveData<ShowState> getToolbarProgress() { return toolbarProgress; }

    //CONSTRUCTOR
    NewGameViewModel(PlayersRepository playersRepository, GamesRepository gamesRepository) {
        this.playersRepository = playersRepository;
        this.gamesRepository = gamesRepository;
    }

    //METHODS
    void loadAllPlayers() {
        progressState.setValue(SHOW);
        List<Player> players = playersRepository.getAll();
        allPlayers.setValue(players);
        progressState.setValue(HIDE);
    }
    void createPlayer(String playerName) {
        progressState.setValue(SHOW);
        playersRepository.insertOne(playerName);
        allPlayers.setValue(playersRepository.getAll());
        progressState.setValue(HIDE);
    }
    void createGame(List<String> playersNames) {
        progressState.setValue(SHOW);
        gamesRepository.insertOne(playersNames);
        progressState.setValue(HIDE);
    }
}
