package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class GetPlayersUseCase extends
        UseCase<GetPlayersUseCase.RequestValues, GetPlayersUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public GetPlayersUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues ignored) {
        getUseCaseCallback().onSuccess(new ResponseValue(dataProvider.getAllPlayers()));
    }

    static final class RequestValues implements UseCase.RequestValues {}

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Player> players;

        ResponseValue(List<Player> players) {
            this.players = players;
        }

        public List<Player> getPlayers() {
            return this.players;
        }
    }
}
