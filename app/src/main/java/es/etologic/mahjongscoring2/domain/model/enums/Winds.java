package es.etologic.mahjongscoring2.domain.model.enums;

public enum Winds {

    EAST(1),
    SOUTH(2),
    WEST(3),
    NORTH(4);

    private int code;

    Winds(int code) { this.code = code; }

    public int getCode() { return code; }
}
