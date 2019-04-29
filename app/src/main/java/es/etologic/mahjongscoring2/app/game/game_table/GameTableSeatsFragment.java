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

import java.util.Locale;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.custom_views.VerticalTextView;
import es.etologic.mahjongscoring2.app.model.Seat;
import es.etologic.mahjongscoring2.app.model.SeatStates;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.SeatStates.NORMAL;
import static es.etologic.mahjongscoring2.app.model.SeatStates.SELECTED;

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
    @BindView(R.id.tvTableSeatSouthName) VerticalTextView tvSeatSouthName;
    @BindView(R.id.tvTableSeatSouthPoints) VerticalTextView tvSeatSouthPoints;
    @BindView(R.id.tvTableSeatSouthPenaltyPoints) VerticalTextView tvSeatSouthPenaltyPoints;
    @BindView(R.id.ivTableSeatWestSeatWindIcon) ImageView ivSeatWestWindIcon;
    @BindView(R.id.tvTableSeatWestName) TextView tvSeatWestName;
    @BindView(R.id.tvTableSeatWestPoints) TextView tvSeatWestPoints;
    @BindView(R.id.tvTableSeatWestPenaltyPoints) TextView tvSeatWestPenaltyPoints;
    @BindView(R.id.ivTableSeatNorthSeatWindIcon) ImageView ivSeatNorthWindIcon;
    @BindView(R.id.tvTableSeatNorthName) VerticalTextView tvSeatNorthName;
    @BindView(R.id.tvTableSeatNorthPoints) VerticalTextView tvSeatNorthPoints;
    @BindView(R.id.tvTableSeatNorthPenaltyPoints) VerticalTextView tvSeatNorthPenaltyPoints;
    //RESOURCES
    @BindDrawable(R.drawable.ic_east) Drawable eastIcon;
    @BindDrawable(R.drawable.ic_south) Drawable southIcon;
    @BindDrawable(R.drawable.ic_west) Drawable westIcon;
    @BindDrawable(R.drawable.ic_north) Drawable northIcon;
    @BindDrawable(R.drawable.ic_penalty_white) Drawable penaltyIcon;
    @BindDrawable(R.drawable.ic_trophy_white_18dp) Drawable tsumoIcon;
    @BindDrawable(R.drawable.ic_medal_white) Drawable ronIcon;
    @BindColor(android.R.color.white) int whiteColor;
    @BindColor(R.color.grayMM) int grayColor;
    @BindColor(R.color.colorAccent) int accentColor;
    @BindColor(R.color.red) int redColor;
    @BindColor(R.color.colorPrimary) int greenColor;
    //FIELDS
    private Unbinder unbinder;
    private TableSeatsListener listener;

    //EVENTS
    @OnClick(R.id.ivTableSeatEastSeatWindIcon) void onTableSeatEastSeatWindIconClick() { if (listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.tvTableSeatEastName) void onTableSeatEastNameClick() { if (listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.tvTableSeatEastPoints) void onTableSeatEastPointsClick() { if (listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.tvTableSeatEastPenaltyPoints) void onTableSeatEastPenaltyPointsClick() { if (listener != null) listener.onEastSeatClick();}
    @OnClick(R.id.ivTableSeatSouthSeatWindIcon) void onTableSeatSouthSeatWindIconClick() { if (listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.tvTableSeatSouthName) void onTableSeatSouthNameClick() { if (listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.tvTableSeatSouthPoints) void onTableSeatSouthPointsClick() { if (listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.tvTableSeatSouthPenaltyPoints) void onTableSeatSouthPenaltyPointsClick() { if (listener != null) listener.onSouthSeatClick();}
    @OnClick(R.id.ivTableSeatWestSeatWindIcon) void onTableSeatWestSeatWindIconClick() { if (listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.tvTableSeatWestName) void onTableSeatWestNameClick() { if (listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.tvTableSeatWestPoints) void onTableSeatWestPointsClick() { if (listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.tvTableSeatWestPenaltyPoints) void onTableSeatWestPenaltyPointsClick() { if (listener != null) listener.onWestSeatClick();}
    @OnClick(R.id.ivTableSeatNorthSeatWindIcon) void onTableSeatNorthSeatWindIconClick() { if (listener != null) listener.onNorthSeatClick();}
    @OnClick(R.id.tvTableSeatNorthName) void onTableSeatNorthNameClick() { if (listener != null) listener.onNorthSeatClick();}
    @OnClick(R.id.tvTableSeatNorthPoints) void onTableSeatNorthPointsClick() { if (listener != null) listener.onNorthSeatClick();}
    @OnClick(R.id.tvTableSeatNorthPenaltyPoints) void onTableSeatNorthPenaltyPointsClick() { if (listener != null) listener.onNorthSeatClick();}

    //PUBLIC METHODS
    void setTableSeatsListener(TableSeatsListener tableSeatsListener) {
        listener = tableSeatsListener;
    }

    void setEastSeat(Seat seat) {
        setWindIcon(ivSeatEastWindIcon, seat.getWind());
        setName(tvSeatEastName, seat.getName());
        setPoints(tvSeatEastPoints, seat.getPoints());
        setPenaltyPoints(tvSeatEastPenaltyPoints, seat.getPenalty());
        setState(ivSeatEastWindIcon, tvSeatEastName, tvSeatEastPoints, seat.getState());
    }
    private void setWindIcon(ImageView imageView, TableWinds wind) {
        switch (wind) {
            case EAST:
                imageView.setImageDrawable(eastIcon);
                break;
            case SOUTH:
                imageView.setImageDrawable(southIcon);
                break;
            case WEST:
                imageView.setImageDrawable(westIcon);
                break;
            case NORTH:
                imageView.setImageDrawable(northIcon);
                break;
            case NONE:
            default:
        }
    }
    private void setName(TextView textView, String name) {
        textView.setText(name);
    }
    private void setPoints(TextView textView, int points) {
        textView.setText(String.format(Locale.getDefault(), "%d", points));
    }
    private void setPenaltyPoints(TextView textView, int penaltyPoints) {
        textView.setText(String.format(Locale.getDefault(), "%+d", penaltyPoints));
        textView.setTextColor(penaltyPoints < 0 ? redColor : greenColor);
        textView.setVisibility(penaltyPoints != 0 ? VISIBLE : GONE);
    }
    private void setState(ImageView imageViewWind, TextView textViewName, TextView textViewPoints, SeatStates state) {
        if (state == NORMAL) {
            imageViewWind.clearColorFilter();
            textViewName.setTextColor(grayColor);
            textViewPoints.setTextColor(grayColor);
            imageViewWind.setEnabled(true);
            textViewName.setEnabled(true);
            textViewPoints.setEnabled(true);
        } else if (state == SELECTED) {
            imageViewWind.setColorFilter(accentColor);
            textViewName.setTextColor(accentColor);
            textViewPoints.setTextColor(accentColor);
            imageViewWind.setEnabled(true);
            textViewName.setEnabled(true);
            textViewPoints.setEnabled(true);
        } else {
            imageViewWind.setEnabled(false);
            textViewName.setEnabled(false);
            textViewPoints.setEnabled(false);
        }
    }
    void setSouthSeat(Seat seat) {
        setWindIcon(ivSeatSouthWindIcon, seat.getWind());
        setName(tvSeatSouthName, seat.getName());
        setPoints(tvSeatSouthPoints, seat.getPoints());
        setPenaltyPoints(tvSeatSouthPenaltyPoints, seat.getPenalty());
        setState(ivSeatSouthWindIcon, tvSeatSouthName, tvSeatSouthPoints, seat.getState());
    }
    void setWestSeat(Seat seat) {
        setWindIcon(ivSeatWestWindIcon, seat.getWind());
        setName(tvSeatWestName, seat.getName());
        setPoints(tvSeatWestPoints, seat.getPoints());
        setPenaltyPoints(tvSeatWestPenaltyPoints, seat.getPenalty());
        setState(ivSeatWestWindIcon, tvSeatWestName, tvSeatWestPoints, seat.getState());
    }
    void setNorthSeat(Seat seat) {
        setWindIcon(ivSeatNorthWindIcon, seat.getWind());
        setName(tvSeatNorthName, seat.getName());
        setPoints(tvSeatNorthPoints, seat.getPoints());
        setPenaltyPoints(tvSeatNorthPenaltyPoints, seat.getPenalty());
        setState(ivSeatNorthWindIcon, tvSeatNorthName, tvSeatNorthPoints, seat.getState());
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
