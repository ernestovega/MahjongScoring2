package es.etologic.mahjongscoring2.app.combinations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.domain.entities.Combination;

class CombinationsRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Fields

    private List<Combination> combinations;

    //endregion

    //region Constructor

    CombinationsRvAdapter() {
        this.combinations = new ArrayList<>();
    }

    //endregion

    //region Public

    void setCombinations(List<Combination> combinations) {
        this.combinations = combinations;
    }

    //endregion

    //region ViewHolder

    class CombinationItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCombinationItemPoints) TextView tvPoints;
        @BindView(R.id.tvCombinationItemName) TextView tvName;
        @BindView(R.id.tvCombinationItemPosition) TextView tvPosition;
        @BindView(R.id.ivCombinationItemImage) ImageView ivImage;
        @BindView(R.id.tvCombinationItemDescription) TextView tvDescription;

        CombinationItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    //endregion

    //region Lifecycle

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
        myHolder.tvPoints.setText(String.valueOf(combination.getPoints()));
        //endregion
        //region Name
        myHolder.tvName.setText(combination.getName());
        //endregion
        //region Position
        myHolder.tvPosition.setText(String.format(Locale.getDefault(),"#%d", position+1));
        //endregion
        //region Image or Description
        if(combination.getImage() < 0) {
            myHolder.tvDescription.setText(combination.getDescription());
            myHolder.ivImage.setVisibility(View.GONE);
            myHolder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            myHolder.ivImage.setImageResource(combination.getImage());
            myHolder.tvDescription.setVisibility(View.GONE);
            myHolder.ivImage.setVisibility(View.VISIBLE);
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
        combinations = newCombinationsCopy;
    }

    //endregion
}