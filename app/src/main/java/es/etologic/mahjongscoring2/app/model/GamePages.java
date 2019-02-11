package es.etologic.mahjongscoring2.app.model;

public enum GamePages{
    TABLE(0),
    LIST(1);

    private int code;

    GamePages(int code) { this.code = code; }

    public int getCode() { return code; }

    public static GamePages getFromCode(int code) {
        switch (code) {
            default: return TABLE;
            case 1: return LIST;
        }
    }
}
