package es.etologic.mahjongscoring2.domain.use_cases;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.BaseError;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class CreatePlayerUseCase extends
        UseCase<CreatePlayerUseCase.RequestValues, CreatePlayerUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public CreatePlayerUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Player playerToInsert = new Player(requestValues.getPlayerName());
        if(dataProvider.createPlayer(playerToInsert)) {
            Player newPlayer = dataProvider.getPlayer(requestValues.getPlayerName());
            getUseCaseCallback().onSuccess(new ResponseValue(newPlayer));
        } else {
            getUseCaseCallback().onError(BaseError.getInsertionError().getMessage());
        }
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private String playerName;

        public String getPlayerName() {
            return this.playerName;
        }

        public RequestValues(String playerName) {
            this.playerName = playerName;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Player player;

        public Player getPlayer() {
            return this.player;
        }

        ResponseValue(Player player) {
            this.player = player;
        }
    }
}
