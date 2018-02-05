package es.etologic.mahjongscoring2.app.combinations;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.app.model.ShowState;
import es.etologic.mahjongscoring2.domain.entities.Combination;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static es.etologic.mahjongscoring2.app.model.ShowState.HIDE;
import static es.etologic.mahjongscoring2.app.model.ShowState.SHOW;
import static es.etologic.mahjongscoring2.domain.entities.Combination.CombinationDescriptionType.IMAGE;

class CombinationsRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Constants

    private static final int ANIMATION_DURATION = 500;

    //endregion

    //region Fields

    private Context context;
    private List<Combination> combinations;
    private ShowState imageOrDescriptionShowState;
    private int cardViewFullHeight;
    private int cardViewMinHeight;

    //endregion

    //region Constructor

    CombinationsRvAdapter() {
        combinations = new ArrayList<>();
        imageOrDescriptionShowState = HIDE;
    }

    //endregion

    //region Public

    void setCombinations(List<Combination> combinations) {
        saveCombinationsCopy(combinations);
        notifyDataSetChanged();
    }

    ShowState toggleImageOrDescription() {
        imageOrDescriptionShowState = imageOrDescriptionShowState == SHOW ? HIDE : SHOW;
        notifyDataSetChanged();
        return imageOrDescriptionShowState;
    }

    //endregion

    //region ViewHolder

    class CombinationItemViewHolder extends RecyclerView.ViewHolder {

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

            cardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                    .OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    cardView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if(cardView.getHeight() > cardViewFullHeight) {
                        cardViewFullHeight = cardView.getHeight();
                    }
                    return true;
                }
            });
        }

        @OnClick(R.id.cvCombinationItem) void onCombinationItemClick() {
            toggleImageOrDescriptionContainerVisibility();
        }

        private void toggleImageOrDescriptionContainerVisibility() {
            if (cardView.getHeight() == cardViewMinHeight) {
                showImageOrDescriptionIfCollapsed(cardView, flImageOrDescriptionContainer);
            } else {
                hideImageOrDescriptionIfExpanded(cardView, flImageOrDescriptionContainer);
            }
        }
    }

    //endregion

    //region Lifecycle

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
        cardViewMinHeight = (int) context.getResources().getDimension(
                R.dimen.combination_item_cardview_min_height);
    }

    @Override
    public int getItemCount() {
        return combinations.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.combination_item,
                parent, false);
        return new CombinationItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Combination combination = combinations.get(position);
        CombinationItemViewHolder myHolder = (CombinationItemViewHolder) holder;
        //region Points
        myHolder.tvPoints.setText(String.valueOf(combination.getCombinationPoints()));
        //endregion
        //region Name
        myHolder.tvName.setText(combination.getCombinationName());
        //endregion
        //region Position
        myHolder.tvPosition.setText(String.format(Locale.getDefault(),"#%d", position+1));
        //endregion
        //region Image or Description
        myHolder.flImageOrDescriptionContainer.setVisibility(
                imageOrDescriptionShowState == SHOW ? VISIBLE : GONE);
        if(combination.getCombinationDescriptionType() == IMAGE) {
            myHolder.ivImage.setImageResource(combination.getCombinationImage());
            myHolder.tvDescription.setVisibility(GONE);
            myHolder.ivImage.setVisibility(VISIBLE);
        } else {
            myHolder.tvDescription.setText(combination.getCombinationDescription());
            myHolder.ivImage.setVisibility(GONE);
            myHolder.tvDescription.setVisibility(VISIBLE);
        }
        //endregion
    }

    //endregion

    //region Private

    private void saveCombinationsCopy(List<Combination> combinations) {
        List<Combination> newCombinationsCopy = new ArrayList<>(combinations.size());
        for(Combination combination : combinations) {
            newCombinationsCopy.add(combination.getCopy());
        }
        this.combinations = newCombinationsCopy;
    }

    private void showImageOrDescriptionIfCollapsed(CardView cardView,
                                                   FrameLayout flImageOrDescription) {
        if(cardView.getHeight() == cardViewMinHeight) {
            ValueAnimator expandAnimation = ValueAnimator.ofInt(
                    cardView.getMeasuredHeightAndState(), cardViewFullHeight);
            expandAnimation.addUpdateListener(valueAnimator -> {
                int val = (Integer)valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = val;
                cardView.setLayoutParams(layoutParams);
            });
            expandAnimation.setDuration(ANIMATION_DURATION);
            AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
            fadeInAnimation.setDuration(ANIMATION_DURATION);
            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    flImageOrDescription.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            expandAnimation.start();
            flImageOrDescription.startAnimation(fadeInAnimation);
        }
    }

    private void hideImageOrDescriptionIfExpanded(CardView cardView,
                                                  FrameLayout flImageOrDescription) {
        if(cardView.getHeight() > cardViewMinHeight) {
            ValueAnimator collapseAnimation = ValueAnimator.ofInt(
                    cardView.getMeasuredHeightAndState(), cardViewMinHeight);
            collapseAnimation.addUpdateListener(valueAnimator -> {
                int val = (Integer)valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = val;
                cardView.setLayoutParams(layoutParams);
            });
            collapseAnimation.start();
            collapseAnimation.setDuration(ANIMATION_DURATION);
            AlphaAnimation fadeOutAnimation = new AlphaAnimation(1, 0);
            fadeOutAnimation.setDuration(ANIMATION_DURATION);
            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    flImageOrDescription.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            flImageOrDescription.startAnimation(fadeOutAnimation);
            collapseAnimation.start();
        }
    }

    //endregion
}