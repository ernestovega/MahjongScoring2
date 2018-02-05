package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import es.etologic.mahjongscoring2.data.repository.DataProvider;
import es.etologic.mahjongscoring2.domain.DataSource;
import es.etologic.mahjongscoring2.domain.entities.Combination;
import es.etologic.mahjongscoring2.domain.threading.UseCase;

public class GetFilteredCombinationsUseCase extends
        UseCase<GetFilteredCombinationsUseCase.RequestValues, GetFilteredCombinationsUseCase.ResponseValue> {

    private final DataProvider dataProvider;

    public GetFilteredCombinationsUseCase(DataSource dataSource) {
        this.dataProvider = (DataProvider)dataSource;
    }

    @Override
    protected void executeUseCase(RequestValues filter) {
        getUseCaseCallback().onSuccess(
                new ResponseValue(dataProvider.getFilteredCombinations(filter.getFilter())));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String filter;

        public RequestValues(String filter) {
            this.filter = filter;
        }

        String getFilter() {
            return filter;
        }
    }

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
