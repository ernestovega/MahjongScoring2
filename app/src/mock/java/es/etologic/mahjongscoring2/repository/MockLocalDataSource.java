package es.etologic.mahjongscoring2.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import es.etologic.mahjongscoring2.data.repository.local.ILocalDataSource;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.entities.Round;

public class MockLocalDataSource implements ILocalDataSource {

    //region IRemoteDataSource implementation

    @Override
    public void clearDataBase() {}

    //PLAYERS
    @Override
    public boolean savePlayers(List<Player> players) {
        return true;
    }

    //GAMES
    @Override
    public boolean saveGame(Game game) {
        return true;
    }

    @Override
    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        Game game1 = new Game(
                1,
                "Chino",
                "Héctor",
                "Edu",
                "Eto",
                new Date());
        game1.setRounds(getRandomRounds(1));
        games.add(game1);
        Game game2 = new Game(
                2,
                "Antonio Ayllón",
                "Héctor Escaso",
                "Eduardo Amador",
                "Ernesto Vega",
                new Date());
        game2.setRounds(getRandomRounds(2));
        games.add(game2);
        Game game3 = new Game(
                3,
                "Chino",
                "Marco Antonio Olmos Domínguez",
                "Héctor Escaso Gil",
                "Ernesto Vega de la Iglesia Soria",
                new Date());
        game3.setRounds(getRandomRounds(3));
        games.add(game3);
        Game game4 = new Game(
                4,
                "Ernesto Vega de la Iglesia Soria",
                "Chino",
                "Marco Antonio Olmos Domínguez",
                "Héctor Escaso Gil",
                new Date());
        game4.setRounds(getRandomRounds(4));
        games.add(game4);
        Game game5 = new Game(
                5,
                "Marco Antonio Olmos Domínguez",
                "Ernesto Vega de la Iglesia Kolganova",
                "Luis Miguel Froilán de todos los Santos",
                "Ernesto Vega de la Iglesia Soria",
                new Date());
        game5.setRounds(getRandomRounds(5));
        games.add(game5);
        return games;
    }

    private List<Round> getRandomRounds(int gameId) {
        Random random = new Random();
        List<Round> rounds = new ArrayList<>();
        for(int i = 1; i <= random.nextInt(16) + 1; i++) {
            Round round = new Round(gameId, i);
            round.setHandPoints(random.nextInt(193) + 8);
            round.setWinnerInitialPosition(random.nextInt(4) + 1);
            rounds.add(round);
        }
        return rounds;
    }

    //endregion
}