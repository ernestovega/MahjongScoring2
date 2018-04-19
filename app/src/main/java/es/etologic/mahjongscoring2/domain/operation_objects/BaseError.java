package es.etologic.mahjongscoring2.domain.operation_objects;

public class BaseError {

    private static final String DEFAULT_ERROR_MESSAGE = "Unexpected error";
    private static final String CREATION_ERROR_MESSAGE = "Creation error";
    private static final String DELETION_ERROR_MESSAGE = "Deletion error";
    private static final int DEFAULT_ERROR_CODE = -1;
    private static final int INSERTION_ERROR_CODE = -2;
    private static final int DELETION_ERROR_CODE = -3;

    private int errorCode;
    private String errorMessage;

    public int getCode() { return errorCode; }
    public String getMessage() { return errorMessage; }

    public BaseError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static BaseError getDefaultError() { return new BaseError(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE); }
    public static BaseError getInsertionError() { return new BaseError(INSERTION_ERROR_CODE, CREATION_ERROR_MESSAGE); }
    public static BaseError getDeletionError() { return new BaseError(DELETION_ERROR_CODE, DELETION_ERROR_MESSAGE); }
}
