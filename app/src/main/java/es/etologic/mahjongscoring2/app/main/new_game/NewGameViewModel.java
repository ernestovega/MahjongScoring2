package es.etologic.mahjongscoring2.app.main.new_game;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.data.repositories.GamesRepository;
import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.entities.Player;

class NewGameViewModel extends ViewModel {

    //FIELDS
    private PlayersRepository playersRepository;
    private GamesRepository gamesRepository;
    private MutableLiveData<List<Player>> allPlayers = new MutableLiveData<>();
    private MutableLiveData<Player> newPlayer = new MutableLiveData<>();
    private MutableLiveData<Long> newGameId = new MutableLiveData<>();
    private MutableLiveData<ShowState> toolbarProgress = new MutableLiveData<>();

    //CONSTRUCTOR
    NewGameViewModel(PlayersRepository playersRepository, GamesRepository gamesRepository) {
        this.playersRepository = playersRepository;
        this.gamesRepository = gamesRepository;
    }

    //OBSERVABLES
    MutableLiveData<List<Player>> getAllPlayers() {
        return allPlayers;
    }
    MutableLiveData<Player> getNewPlayer() {
        return newPlayer;
    }
    MutableLiveData<Long> getNewGameId() {
        return newGameId;
    }
    MutableLiveData<ShowState> getToolbarProgress() {
        return toolbarProgress;
    }

    //METHODS
    void loadAllPlayers() {
//        progressState.setValue(SHOW);
        List<Player> players = playersRepository.getAll();
        allPlayers.setValue(players);
//        progressState.setValue(HIDE);
    }
    void createPlayer(String playerName) {
//        progressState.setValue(SHOW);
        playersRepository.insertOne(playerName);
        allPlayers.setValue(playersRepository.getAll());
//        progressState.setValue(HIDE);
    }
    void createGame(List<String> playersNames) {
//        progressState.setValue(SHOW);
        gamesRepository.insertOne(playersNames);
//        progressState.setValue(HIDE);
    }
}
