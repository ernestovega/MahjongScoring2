package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class GetGamesUseCase extends
        UseCase<GetGamesUseCase.RequestValues, GetGamesUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public GetGamesUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues ignored) {
        getUseCaseCallback().onSuccess(new ResponseValue(dataProvider.getAllGames()));
    }

    static final class RequestValues implements UseCase.RequestValues {}

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Game> games;

        ResponseValue(List<Game> games) {
            this.games = games;
        }

        public List<Game> getGames() {
            return this.games;
        }
    }
}
