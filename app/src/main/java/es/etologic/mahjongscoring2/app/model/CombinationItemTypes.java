package es.etologic.mahjongscoring2.app.model;

public enum CombinationItemTypes {

    HEADER,
    ITEM;

    public static int getValue(CombinationItemTypes viewType) {
        switch(viewType) {
            case HEADER:
                return 0;
            default:
            case ITEM:
                return 1;
        }
    }

    public static CombinationItemTypes getType(int value) {
        switch(value) {
            case 0:
                return HEADER;
            default:
            case 1:
                return ITEM;
        }
    }
}
