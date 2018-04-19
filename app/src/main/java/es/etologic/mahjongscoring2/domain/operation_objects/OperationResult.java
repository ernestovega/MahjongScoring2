package es.etologic.mahjongscoring2.domain.operation_objects;

import static es.etologic.mahjongscoring2.domain.operation_objects.OperationState.FAIL;
import static es.etologic.mahjongscoring2.domain.operation_objects.OperationState.SUCCESS;

public class OperationResult<RESPONSE_TYPE, ERROR_TYPE extends BaseError> {

    private OperationState state;
    private RESPONSE_TYPE response;
    private ERROR_TYPE error;

    public OperationState getState() { return state; }
    public RESPONSE_TYPE getResponse() { return response; }
    public ERROR_TYPE getError() { return error; }

    public OperationResult(RESPONSE_TYPE response) {
        this.state = SUCCESS;
        this.response = response;
        this.error = null;
    }
    public OperationResult(ERROR_TYPE error) {
        this.state = FAIL;
        this.response = null;
        this.error = error;
    }
}
