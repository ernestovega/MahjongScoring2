package es.etologic.mahjongscoring2.domain.entities;

public class Combination {

    private int itemType;
    private int nameStringRes;
    private int imageResId;
    private int descriptionStringRes;

    public int getItemTypeValue() {
        return itemType;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getNameStringRes() {
        return nameStringRes;
    }

    public int getDescriptionStringRes() {
        return descriptionStringRes;
    }

    public Combination(int itemType, int nameStringRes, int imageResId, int descriptionStringRes) {
        this.itemType = itemType;
        this.nameStringRes = nameStringRes;
        this.imageResId = imageResId;
        this.descriptionStringRes = descriptionStringRes;
    }
}