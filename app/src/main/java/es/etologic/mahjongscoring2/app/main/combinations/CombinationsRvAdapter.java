package es.etologic.mahjongscoring2.app.main.combinations;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.model.Combination;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.model.Combination.CombinationDescriptionType.IMAGE;

class CombinationsRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Constants
    //    private static final int ANIMATION_DURATION = 500;

    //region Fields
    private List<Combination> combinations;
    private ShowState imageOrDescriptionShowState;
    private int cardViewMinHeight;
    //    private int cardViewFullHeight;

    //region Constructor
    CombinationsRvAdapter() {
        combinations = new ArrayList<>();
        imageOrDescriptionShowState = HIDE;
    }

    //region Public
    void setCombinations(List<Combination> combinations) {
        saveCombinationsCopy(combinations);
        notifyDataSetChanged();
    }
    private void saveCombinationsCopy(List<Combination> combinations) {
        List<Combination> newCombinationsCopy = new ArrayList<>(combinations.size());
        for (Combination combination : combinations) {
            newCombinationsCopy.add(combination.getCopy());
        }
        this.combinations = newCombinationsCopy;
    }

    ShowState toggleImageOrDescription() {
        imageOrDescriptionShowState = imageOrDescriptionShowState == SHOW ? HIDE : SHOW;
        notifyItemRangeChanged(0, combinations.size() - 1);
        return imageOrDescriptionShowState;
    }

    //region Lifecycle
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        cardViewMinHeight = (int) recyclerView.getContext().getResources().getDimension(
                R.dimen.combination_item_cardview_min_height);
        //        cardViewFullHeight = (int) context.getResources().getDimension(
        //                R.dimen.combination_item_cardview_full_height);
    }

    @Override
    public int getItemCount() {
        return combinations.size();
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.combination_item, parent, false);
        return new CombinationItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Combination combination = combinations.get(position);
        CombinationItemViewHolder myHolder = (CombinationItemViewHolder) holder;
        myHolder.tvPoints.setText(String.valueOf(combination.getCombinationPoints()));
        myHolder.tvName.setText(combination.getCombinationName());
        myHolder.tvPosition.setText(String.format(Locale.getDefault(), "#%d", position + 1));

        if (combination.getCombinationDescriptionType() == IMAGE) {
            myHolder.ivImage.setImageResource(combination.getCombinationImage());
            myHolder.tvDescription.setVisibility(GONE);
            myHolder.ivImage.setVisibility(VISIBLE);
        } else {
            myHolder.tvDescription.setText(combination.getCombinationDescription());
            myHolder.ivImage.setVisibility(GONE);
            myHolder.tvDescription.setVisibility(VISIBLE);
        }

        myHolder.flImageOrDescriptionContainer.setVisibility(imageOrDescriptionShowState == SHOW ? VISIBLE : GONE);
    }

    //region Private
    class CombinationItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llCombinationItemContainer) LinearLayout llContainer;
        @BindView(R.id.cvCombinationItem) CardView cardView;
        @BindView(R.id.tvCombinationItemPoints) TextView tvPoints;
        @BindView(R.id.tvCombinationItemName) TextView tvName;
        @BindView(R.id.tvCombinationItemPosition) TextView tvPosition;
        @BindView(R.id.flCombinationItemImageOrDescriptionContainer) FrameLayout flImageOrDescriptionContainer;
        @BindView(R.id.ivCombinationItemImage) ImageView ivImage;
        @BindView(R.id.tvCombinationItemDescription) TextView tvDescription;

        CombinationItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.cvCombinationItem) void onCombinationItemClick() {
            flImageOrDescriptionContainer.setVisibility(cardView.getHeight() == cardViewMinHeight ? VISIBLE : GONE);
        }
    }
}