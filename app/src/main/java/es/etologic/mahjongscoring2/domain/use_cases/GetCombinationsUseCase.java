package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class GetCombinationsUseCase extends
        UseCase<GetCombinationsUseCase.RequestValues, GetCombinationsUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public GetCombinationsUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues ignored) {
        getUseCaseCallback().onSuccess(new ResponseValue(dataProvider.getAllCombinations()));
    }

    static final class RequestValues implements UseCase.RequestValues {}

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Combination> combinations;

        ResponseValue(List<Combination> combinations) {
            this.combinations = combinations;
        }

        public List<Combination> getCombinations() {
            return this.combinations;
        }
    }
}
