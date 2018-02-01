package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.Calendar;
import java.util.List;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.BaseError;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class CreateGameUseCase extends
        UseCase<CreateGameUseCase.RequestValues, CreateGameUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public CreateGameUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Game gameToInsert = getGameToInsert(requestValues);
        Long newGameId = dataProvider.createGame(gameToInsert);
        if(newGameId != null && newGameId > 0) {
            getUseCaseCallback().onSuccess(new ResponseValue(newGameId));
        } else {
            getUseCaseCallback().onError(BaseError.getInsertionError().getErrorMessage());
        }
    }

    private Game getGameToInsert(RequestValues requestValues) {
        String nameP1 = requestValues.getPlayers().get(0).getPlayerName();
        String nameP2 = requestValues.getPlayers().get(1).getPlayerName();
        String nameP3 = requestValues.getPlayers().get(2).getPlayerName();
        String nameP4 = requestValues.getPlayers().get(3).getPlayerName();
        return new Game(Game.NOT_SET_GAME_ID, nameP1, nameP2, nameP3, nameP4,
                Calendar.getInstance().getTime());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private List<Player> players;

        public List<Player> getPlayers() {
            return this.players;
        }

        public RequestValues(List<Player> players) {
            this.players = players;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Long gameId;

        public Long getGameId() {
            return this.gameId;
        }

        ResponseValue(Long gameId) {
            this.gameId = gameId;
        }
    }
}
