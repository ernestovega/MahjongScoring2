package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.domain.entities.OldGame;
import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class GetOldGamesUseCase extends UseCase<GetOldGamesUseCase.RequestValues, GetOldGamesUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public GetOldGamesUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
    }

    static final class RequestValues implements UseCase.RequestValues {}

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<OldGame> oldGames;

        public ResponseValue(List<OldGame> oldGames) {
            this.oldGames = oldGames;
        }

        public List<OldGame> getOldGames() {
            return this.oldGames;
        }
    }
}
