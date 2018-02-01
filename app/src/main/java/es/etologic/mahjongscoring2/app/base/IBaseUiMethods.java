package es.etologic.mahjongscoring2.app.base;

import android.support.annotation.IntegerRes;
import android.view.View;

public interface IBaseUiMethods {

    void showCommonProgress();
    void hideCommonProgress();
    void showCommonSnackbar(String message);
    void showCommonSnackbar(String message, @IntegerRes int length,
                            String buttonText, View.OnClickListener action);
}
