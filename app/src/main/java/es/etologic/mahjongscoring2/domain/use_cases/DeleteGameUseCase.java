package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.BaseError;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class DeleteGameUseCase extends
        UseCase<DeleteGameUseCase.RequestValues, DeleteGameUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public DeleteGameUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        if(dataProvider.deleteGame(requestValues.getGameId())) {
            List<Game> games = dataProvider.getAllGames();
            getUseCaseCallback().onSuccess(new ResponseValue(games));
        } else {
            getUseCaseCallback().onError(BaseError.getDeletionError().getErrorMessage());
        }
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private long gameId;

        public long getGameId() {
            return this.gameId;
        }

        public RequestValues(long gameId) {
            this.gameId = gameId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Game> games;

        public List<Game> getGames() {
            return this.games;
        }

        ResponseValue(List<Game> gameId) {
            this.games = gameId;
        }
    }
}
