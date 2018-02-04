package es.etologic.mahjongscoring2.domain.entities;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class Combination {

    private final int points;
    private final @StringRes int name;
    private final @DrawableRes int image;
    private final @StringRes int description;

    public int getPoints() {
        return points;
    }

    public int getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getDescription() {
        return description;
    }

    public Combination(int points, int name, int image, int description) {
        this.points = points;
        this.name = name;
        this.image = image;
        this.description = description;
    }

    public Combination getCopy() {
        return new Combination(points, name, image, description);
    }
}