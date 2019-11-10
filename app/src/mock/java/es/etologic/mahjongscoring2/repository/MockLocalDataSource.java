package es.etologic.mahjongscoring2.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import es.etologic.mahjongscoring2.domain.model.Combination;
import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.Player;
import es.etologic.mahjongscoring2.domain.model.Round;

public class MockLocalDataSource implements ILocalDataSource {

    //region DB

    @Override
    public void clearDatabase() {}

    //endregion

    //region GAMES

    @Override
    public long insertGame(Game game) {
        return 6;
    }

    @Override
    public Game getGame(long gameId) {
        return null;
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
                Calendar.getInstance().getTime());
        game1.setRounds(getRandomRounds(1));
        games.add(game1);
        Game game2 = new Game(
                2,
                "Antonio Ayllón",
                "Héctor Escaso",
                "Eduardo Amador",
                "Ernesto Vega",
                Calendar.getInstance().getTime());
        game2.setRounds(getRandomRounds(2));
        games.add(game2);
        Game game3 = new Game(
                3,
                "Chino",
                "Marco Antonio Olmos Domínguez",
                "Héctor Escaso Gil",
                "Ernesto Vega de la Iglesia Soria",
                Calendar.getInstance().getTime());
        game3.setRounds(getRandomRounds(3));
        games.add(game3);
        Game game4 = new Game(
                4,
                "Ernesto Vega de la Iglesia Soria",
                "Chino",
                "Marco Antonio Olmos Domínguez",
                "Héctor Escaso Gil",
                Calendar.getInstance().getTime());
        game4.setRounds(getRandomRounds(4));
        games.add(game4);
        Game game5 = new Game(
                5,
                "Marco Antonio Olmos Domínguez",
                "Ernesto Vega de la Iglesia Kolganova",
                "Luis Miguel Froilán de todos los Santos",
                "Ernesto Vega de la Iglesia Soria",
                Calendar.getInstance().getTime());
        game5.setRounds(getRandomRounds(5));
        games.add(game5);
        return games;
    }

    @Override
    public boolean deleteGame(long gameId) {
        return true;
    }

    //endregion

    //region PLAYERS

    @Override
    public boolean insertPlayer(Player player) {
        return true;
    }

    @Override
    public Player getPlayer(String playerName) {
        return new Player(playerName);
    }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Chino"));
        players.add(new Player("Antonio Ayllón"));
        players.add(new Player("Héctor"));
        players.add(new Player("Héctor Escaso"));
        players.add(new Player("Héctor Escaso Gil"));
        players.add(new Player("Edu"));
        players.add(new Player("Eduardo Amador"));
        players.add(new Player("Eto"));
        players.add(new Player("Ernesto Vega"));
        players.add(new Player("Ernesto Vega de la Iglesia Soria"));
        players.add(new Player("Ernesto Vega de la Iglesia Kolganova"));
        players.add(new Player("Marco Antonio Olmos Domínguez"));
        players.add(new Player("Luis Miguel Froilán de todos los Santos"));
        return players;
    }

    //endregion

    //region COMBINATIONS

    @Override
    public List<Combination> getAllCombinations() {
        return new ArrayList<>();
    }

    //endregion

    //region Private

    private List<Round> getRandomRounds(int gameId) {
        Random random = new Random();
        List<Round> rounds = new ArrayList<>();
        for(int i = 1; i <= random.nextInt(16) + 1; i++) {
            Round round = new Round(gameId, i);
            int handPoints = random.nextInt(93) + 8;
            int winnerInitialPosition = random.nextInt(4) + 1;
            if(random.nextInt(2) == 0) {
                round.setAllPlayersTsumoPoints(winnerInitialPosition, handPoints);
            } else {
                int looserInitialPosition;
                do { looserInitialPosition = random.nextInt(4) + 1;
                } while(looserInitialPosition == winnerInitialPosition);
                round.setAllPlayersRonPoints(winnerInitialPosition, handPoints,
                        looserInitialPosition);
            }
            rounds.add(round);
        }
        return rounds;
    }

    //endregion
}