package es.etologic.mahjongscoring2.app.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.*;

public class KeyboardUtils {

    public static void showKeyboard(Context context, View viewInputMethod) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
