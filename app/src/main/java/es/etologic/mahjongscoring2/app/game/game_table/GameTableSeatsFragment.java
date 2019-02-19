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
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.model.Seat;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.SeatState.NORMAL;

public class GameTableSeatsFragment extends Fragment {

    //LISTENER
    interface TableSeatsListener {
        void onEastSeatClick();
        void onSouthSeatClick();
        void onWestSeatClick();
        void onNorthSeatClick();
    }
    //VIEWS
    @BindView(R.id.ivTableSeatEastSeatWindIcon) ImageView ivSeatEastWindIcon;
    @BindView(R.id.tvTableSeatEastName) TextView tvSeatEastName;
    @BindView(R.id.tvTableSeatEastPoints) TextView tvSeatEastPoints;
    @BindView(R.id.tvTableSeatEastPenaltyPoints) TextView tvSeatEastPenaltyPoints;
    @BindView(R.id.ivTableSeatSouthSeatWindIcon) ImageView ivSeatSouthWindIcon;
    @BindView(R.id.tvTableSeatSouthName) TextView tvSeatSouthName;
    @BindView(R.id.tvTableSeatSouthPoints) TextView tvSeatSouthPoints;
    @BindView(R.id.tvTableSeatSouthPenaltyPoints) TextView tvSeatSouthPenaltyPoints;
    @BindView(R.id.ivTableSeatWestSeatWindIcon) ImageView ivSeatWestWindIcon;
    @BindView(R.id.tvTableSeatWestName) TextView tvSeatWestName;
    @BindView(R.id.tvTableSeatWestPoints) TextView tvSeatWestPoints;
    @BindView(R.id.tvTableSeatWestPenaltyPoints) TextView tvSeatWestPenaltyPoints;
    @BindView(R.id.ivTableSeatNorthSeatWindIcon) ImageView ivSeatNorthWindIcon;
    @BindView(R.id.tvTableSeatNorthName) TextView tvSeatNorthName;
    @BindView(R.id.tvTableSeatNorthPoints) TextView tvSeatNorthPoints;
    @BindView(R.id.tvTableSeatNorthPenaltyPoints) TextView tvSeatNorthPenaltyPoints;
    //RESOURCES
    @BindDrawable(R.drawable.ic_east) Drawable eastIcon;
    @BindDrawable(R.drawable.ic_south) Drawable southIcon;
    @BindDrawable(R.drawable.ic_west) Drawable westIcon;
    @BindDrawable(R.drawable.ic_north) Drawable northIcon;
    @BindDrawable(R.drawable.ic_penalty_white) Drawable penaltyIcon;
    @BindDrawable(R.drawable.ic_trophy_white_18dp) Drawable tsumoIcon;
    @BindDrawable(R.drawable.ic_medal_white) Drawable ronIcon;
    @BindColor(android.R.color.white) int whiteColor;
    @BindColor(R.color.grayMM) int grayMMColor;
    @BindColor(R.color.colorAccent) int accentColor;
    @BindColor(R.color.purpleDarkTransparent) int purpleColor;
    @BindColor(R.color.redPenalty) int redPenalty;
    @BindColor(R.color.colorPrimary) int green;
    //FIELDS
    private Unbinder unbinder;
    private TableSeatsListener listener;

    //EVENTS
    @OnClick(R.id.ivTableSeatEastSeatWindIcon) void onTableSeatEastSeatWindIconClick() { if(listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.tvTableSeatEastName) void onTableSeatEastNameClick() { if(listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.tvTableSeatEastPoints) void onTableSeatEastPointsClick() { if(listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.tvTableSeatEastPenaltyPoints) void onTableSeatEastPenaltyPointsClick() { if(listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.ivTableSeatSouthSeatWindIcon) void onTableSeatSouthSeatWindIconClick() { if(listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.tvTableSeatSouthName) void onTableSeatSouthNameClick() { if(listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.tvTableSeatSouthPoints) void onTableSeatSouthPointsClick() { if(listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.tvTableSeatSouthPenaltyPoints) void onTableSeatSouthPenaltyPointsClick() { if(listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.ivTableSeatWestSeatWindIcon) void onTableSeatWestSeatWindIconClick() { if(listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.tvTableSeatWestName) void onTableSeatWestNameClick() { if(listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.tvTableSeatWestPoints) void onTableSeatWestPointsClick() { if(listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.tvTableSeatWestPenaltyPoints) void onTableSeatWestPenaltyPointsClick() { if(listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.ivTableSeatNorthSeatWindIcon) void onTableSeatNorthSeatWindIconClick() { if(listener != null) listener.onNorthSeatClick();}
    @OnClick(R.id.tvTableSeatNorthName) void onTableSeatNorthNameClick() { if(listener != null) listener.onNorthSeatClick();}
    @OnClick(R.id.tvTableSeatNorthPoints) void onTableSeatNorthPointsClick() { if(listener != null) listener.onNorthSeatClick();}
    @OnClick(R.id.tvTableSeatNorthPenaltyPoints) void onTableSeatNorthPenaltyPointsClick() { if(listener != null) listener.onNorthSeatClick();}

    //PUBLIC METHODS
    void setTableSeatsListener(TableSeatsListener tableSeatsListener) {
        listener = tableSeatsListener;
    }

    void setEastSeat(Seat seat) {
        ivSeatEastWindIcon.setImageDrawable(getWindIcon(seat.getWind()));
        tvSeatEastName.setText(seat.getName());
        tvSeatEastPoints.setText(String.valueOf(seat.getPoints()));
        tvSeatEastPenaltyPoints.setText(String.valueOf(seat.getPenalty()));
        tvSeatEastPenaltyPoints.setTextColor(seat.getPenalty() < 0 ? redPenalty : green);
//        tvSeatEastPenaltyPoints.setVisibility(seat.getPenalty() != 0 ? VISIBLE : INVISIBLE);
        if(seat.getState() == NORMAL) {
            ivSeatEastWindIcon.clearColorFilter();
            tvSeatEastName.setTextColor(grayMMColor);
            tvSeatEastPoints.setTextColor(grayMMColor);
        } else {
            ivSeatEastWindIcon.setColorFilter(accentColor);
            tvSeatEastName.setTextColor(accentColor);
            tvSeatEastPoints.setTextColor(accentColor);
        }
    }
    void setSouthSeat(Seat seat) {
        ivSeatSouthWindIcon.setImageDrawable(getWindIcon(seat.getWind()));
        tvSeatSouthName.setText(seat.getName());
        tvSeatSouthPoints.setText(String.valueOf(seat.getPoints()));
        tvSeatSouthPenaltyPoints.setText(String.valueOf(seat.getPenalty()));
        tvSeatSouthPenaltyPoints.setTextColor(seat.getPenalty() < 0 ? redPenalty : green);
//        tvSeatSouthPenaltyPoints.setVisibility(seat.getPenalty() != 0 ? VISIBLE : INVISIBLE);
        if(seat.getState() == NORMAL) {
            ivSeatSouthWindIcon.clearColorFilter();
            tvSeatSouthName.setTextColor(grayMMColor);
            tvSeatSouthPoints.setTextColor(grayMMColor);
        } else {
            ivSeatSouthWindIcon.setColorFilter(accentColor);
            tvSeatSouthName.setTextColor(accentColor);
            tvSeatSouthPoints.setTextColor(accentColor);
        }
    }
    void setWestSeat(Seat seat) {
        ivSeatWestWindIcon.setImageDrawable(getWindIcon(seat.getWind()));
        tvSeatWestName.setText(seat.getName());
        tvSeatWestPoints.setText(String.valueOf(seat.getPoints()));
        tvSeatWestPenaltyPoints.setText(String.valueOf(seat.getPenalty()));
        tvSeatWestPenaltyPoints.setTextColor(seat.getPenalty() < 0 ? redPenalty : green);
//        tvSeatWestPenaltyPoints.setVisibility(seat.getPenalty() != 0 ? VISIBLE : INVISIBLE);
        if(seat.getState() == NORMAL) {
            ivSeatWestWindIcon.clearColorFilter();
            tvSeatWestName.setTextColor(grayMMColor);
            tvSeatWestPoints.setTextColor(grayMMColor);
        } else {
            ivSeatWestWindIcon.setColorFilter(accentColor);
            tvSeatWestName.setTextColor(accentColor);
            tvSeatWestPoints.setTextColor(accentColor);
        }
    }
    void setNorthSeat(Seat seat) {
        ivSeatNorthWindIcon.setImageDrawable(getWindIcon(seat.getWind()));
        tvSeatNorthName.setText(seat.getName());
        tvSeatNorthPoints.setText(String.valueOf(seat.getPoints()));
        tvSeatNorthPenaltyPoints.setText(String.valueOf(seat.getPenalty()));
        tvSeatNorthPenaltyPoints.setTextColor(seat.getPenalty() < 0 ? redPenalty : green);
//        tvSeatNorthPenaltyPoints.setVisibility(seat.getPenalty() != 0 ? VISIBLE : INVISIBLE);
        if(seat.getState() == NORMAL) {
            ivSeatNorthWindIcon.clearColorFilter();
            tvSeatNorthName.setTextColor(grayMMColor);
            tvSeatNorthPoints.setTextColor(grayMMColor);
        } else {
            ivSeatNorthWindIcon.setColorFilter(accentColor);
            tvSeatNorthName.setTextColor(accentColor);
            tvSeatNorthPoints.setTextColor(accentColor);
        }
    }
    private Drawable getWindIcon(TableWinds wind) {
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

    //LIFECYCLE
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_table_seats_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
