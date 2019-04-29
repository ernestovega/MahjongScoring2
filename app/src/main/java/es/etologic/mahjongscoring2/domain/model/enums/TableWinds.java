package es.etologic.mahjongscoring2.domain.model.enums;

public enum TableWinds {
    NONE(0),
    EAST(1),
    SOUTH(2),
    WEST(3),
    NORTH(4);

    private int code;

    TableWinds(int code) { this.code = code; }

    public int getCode() { return code; }

    public int getIndex() { return code - 1; }

    public static TableWinds getFromCode(int code) {
        switch (code) {
            default:
                return NONE;
            case 1:
                return EAST;
            case 2:
                return SOUTH;
            case 3:
                return WEST;
            case 4:
                return NORTH;
        }
    }
}
