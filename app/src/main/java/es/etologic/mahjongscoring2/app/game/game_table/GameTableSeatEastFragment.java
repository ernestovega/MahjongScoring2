package es.etologic.mahjongscoring2.app.game.game_table;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.etologic.mahjongscoring2.R;

public class GameTableSeatEastFragment extends GameTableSeatFragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.game_table_seat_fragment, container, false);
        inflate.setRotation(0);
        return inflate;
    }
}
