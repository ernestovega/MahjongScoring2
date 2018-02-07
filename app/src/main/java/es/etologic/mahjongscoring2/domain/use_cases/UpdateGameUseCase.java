package es.etologic.mahjongscoring2.domain.use_cases;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class UpdateGameUseCase extends
        UseCase<UpdateGameUseCase.RequestValues, UpdateGameUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public UpdateGameUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        boolean wasUpdated = dataProvider.updateGame(requestValues.getGame());
        if(wasUpdated) getUseCaseCallback().onSuccess(new ResponseValue());
        else getUseCaseCallback().onError(null);
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private Game game;

        public RequestValues(Game game) {
            this.game = game;
        }

        public Game getGame() {
            return game;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {}
}
