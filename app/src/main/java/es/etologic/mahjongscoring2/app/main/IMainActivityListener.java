package es.etologic.mahjongscoring2.app.main;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

public interface IMainActivityListener {

    void openEndDrawer();

    void closeEndDrawer();

    void replaceFragment(Fragment fragment);
}
