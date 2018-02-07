package es.etologic.mahjongscoring2.domain.use_cases;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class GetGameUseCase extends
        UseCase<GetGameUseCase.RequestValues, GetGameUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public GetGameUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Game game = dataProvider.getGame(requestValues.getGameId());
        getUseCaseCallback().onSuccess(new ResponseValue(game));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private long gameId;

        public RequestValues(long gameId) {
            this.gameId = gameId;
        }

        public long getGameId() {
            return gameId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Game game;

        ResponseValue(Game game) {
            this.game = game;
        }

        public Game getGame() {
            return this.game;
        }
    }
}
