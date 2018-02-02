package es.etologic.mahjongscoring2.app.combinations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.etologic.mahjongscoring2.R;
import es.etologic.mahjongscoring2.domain.entities.Combination;

class CombinationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter {

    private List<Combination> combinations;

    class InvisibleViewHolder extends RecyclerView.ViewHolder {
        InvisibleViewHolder(View view) {
            super(view);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvItemHeaderTitle) TextView textViewTitle;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCombinationItemName) TextView tvName;
        @BindView(R.id.ivCombinationItemImage) ImageView ivImage;
        @BindView(R.id.tvCombinationItemDescription) TextView tvDescription;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    CombinationsAdapter() {
        this.combinations = new ArrayList<>();
    }

    void setCombinations(List<Combination> combinations) {
        this.combinations = combinations;
    }

    @Override
    public long getHeaderId(int position) {
        if(position < 14){
            return 0;
        }
        if(position < 25){
            return 14;
        }
        if(position < 30){
            return 25;
        }
        if(position < 37){
            return 30;
        }
        if(position < 48){
            return 37;
        }
        if(position < 54){
            return 48;
        }
        if(position < 61){
            return 54;
        }
        if(position < 71){
            return 61;
        }
        if(position < 75){
            return 71;
        }
        if(position < 78){
            return 75;
        }
        if(position < 85){
            return 78;
        }
        return 85;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_combination_header, parent, false);
        return new HeaderViewHolder(itemView);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((HeaderViewHolder)holder).textViewTitle.setText(
                combinations.get(position).getNameStringRes());
    }

    @Override
    public int getItemCount() {
        return combinations.size();
    }

    @Override
    public int getItemViewType(int position) {
        return combinations.get(position).getItemTypeValue();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CombinationItemTypes itemType = CombinationItemTypes.getType(viewType);
        View itemView;
        switch(itemType) {
            case HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_combination_invisible, parent, false);
                return new InvisibleViewHolder(itemView);
            default:
            case ITEM:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_combination, parent, false);
                return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Combination combination = combinations.get(position);
        CombinationItemTypes itemType =
                CombinationItemTypes.getType(holder.getItemViewType());
        switch(itemType) {
            case ITEM:
                fillItem((ItemViewHolder)holder, combination);
                break;
        }
    }

    private void fillItem(ItemViewHolder holder, Combination combination) {
        holder.tvName.setText(combination.getNameStringRes());
        if(combination.getImageResId() == -1) {
            holder.ivImage.setVisibility(View.GONE);
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(combination.getDescriptionStringRes());
        } else {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvDescription.setVisibility(View.GONE);
            holder.ivImage.setImageResource(combination.getImageResId());
        }
    }
}