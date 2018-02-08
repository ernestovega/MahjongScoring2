package es.etologic.mahjongscoring2.domain.use_cases;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Round;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class UpdateRoundUseCase extends
        UseCase<UpdateRoundUseCase.RequestValues, UpdateRoundUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public UpdateRoundUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        boolean wasUpdated = dataProvider.updateRound(requestValues.getRound());
        if(wasUpdated) getUseCaseCallback().onSuccess(new ResponseValue());
        else getUseCaseCallback().onError(null);
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private Round round;

        public RequestValues(Round round) {
            this.round = round;
        }

        public Round getRound() {
            return round;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {}
}
