package es.etologic.mahjongscoring2.app.game.game_table;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.model.SeatState;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

public class GameTableSeatFragment extends Fragment {

    //VIEWS
    @BindView(R.id.ivItemPlayerSeatWindIcon) ImageView ivSeatWindIcon;
    @BindView(R.id.tvItemPlayerName) TextView tvSeatName;
    @BindView(R.id.tvItemPlayerPoints) TextView tvSeatPoints;
    //RESOURCES
    @BindColor(android.R.color.white) int whiteColor;
    @BindColor(R.color.grayMM) int grayMMColor;
    @BindColor(R.color.colorAccent) int accentColor;
    @BindColor(R.color.purpleDarkTransparent) int purpleColor;
    @BindDrawable(R.drawable.ic_east) Drawable eastIcon;
    @BindDrawable(R.drawable.ic_south) Drawable southIcon;
    @BindDrawable(R.drawable.ic_west) Drawable westIcon;
    @BindDrawable(R.drawable.ic_north) Drawable northIcon;
    @BindDrawable(R.drawable.ic_penalty_white) Drawable penaltyIcon;
    @BindDrawable(R.drawable.ic_trophy_white_18dp) Drawable tsumoIcon;
    @BindDrawable(R.drawable.ic_medal_white) Drawable ronIcon;
    //FIELDS
    private Unbinder unbinder;
    private int penaltyPoints = 0;

    //PUBLIC METHODS
    void setWind(TableWinds wind) {
        ivSeatWindIcon.setImageDrawable(getWindIcon(wind));
    }
    Drawable getWindIcon(TableWinds wind) {
        switch (wind) {
            case EAST:
                return eastIcon;
            case SOUTH:
                return southIcon;
            case WEST:
                return westIcon;
            case NORTH:
                return northIcon;
            default:
                return null;
        }
    }
    void setName(String name) {
        tvSeatName.setText(name);
    }
    void setPoints(int points) {
        tvSeatPoints.setText(String.valueOf(points));
    }
    void setState(SeatState state) {
        switch (state){
            default:
            case NORMAL:
                changeViewColors(grayMMColor, grayMMColor, grayMMColor);
//                hideIcon();
                break;
            case SELECTED:
            case CALLING_RON:
            case CALLING_TSUMO:
                changeViewColors(accentColor, accentColor, accentColor);
                break;
        }
    }
    private void changeViewColors(int windColorFilter, int pointsColor, int nameColor) {
        ivSeatWindIcon.setColorFilter(windColorFilter);
        tvSeatName.setTextColor(pointsColor);
        tvSeatPoints.setTextColor(nameColor);
    }
    //    void addPenalty(Integer penaltyPoints) {
//        this.penaltyPoints += penaltyPoints;
//        showPlayerLayer(penalizedPlayerLayerColor);
//        showIcon(penaltyIcon);
//    }
//    void cancelPenalty() {
//        this.penaltyPoints = 0;
//        hidePlayerLayer();
//        hideIcon();
//    }
//    private void hidePlayerLayer() {
//        llSeatLayer.setVisibility(View.GONE);
//        ivSeatLayerIcon.setImageDrawable(null);
//    }
//    private void showPlayerLayer(@ColorInt int color) {
//        llSeatLayer.setBackgroundColor(color);
//        llSeatLayer.setVisibility(View.VISIBLE);
//    }
//    private void hideIcon() {
//        ivSeatLayerIcon.setImageDrawable(null);
//    }
//    private void showIcon(Drawable icon) {
//        ivSeatLayerIcon.setImageDrawable(icon);
//    }

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_table_seat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        if(unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
