/*
 * Copyright © 2018 LaLiga.
 * All rights reserved.
 */

package es.etologic.mahjongscoring2.app.base;


import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.util.DiffUtil.calculateDiff;

public abstract class BaseRvAdapter<T extends RecyclerViewable<T>> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //FIELDS
    protected List<T> collection;

    //METHODS
    public void setCollection(List<T> newCollection) {
        if (collection == null || newCollection == null) {
            saveNewCollectionCopy(new ArrayList<>());
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = calculateDiff(new DiffUtilCallback<>(collection, newCollection), true);
            saveNewCollectionCopy(newCollection);
            result.dispatchUpdatesTo(this);
        }
    }
    private void saveNewCollectionCopy(List<T> newCollection) {
        List<T> newCollectionCopy = new ArrayList<>(newCollection.size());
        for (T newItem : newCollection) {
            newCollectionCopy.add(newItem.getCopy());
        }
        collection = newCollectionCopy;
    }

    //LIFECYCLE
    @Override
    public int getItemCount() {
        return collection == null ? 0 : collection.size();
    }

    //INNER CLASSES
    class DiffUtilCallback<V extends RecyclerViewable<V>> extends DiffUtil.Callback {

        private List<V> oldList;
        private List<V> newList;

        DiffUtilCallback(List<V> oldCollection, List<V> newCollection) {
            oldList = oldCollection;
            newList = newCollection;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPos, int newItemPos) { return oldList.get(oldItemPos).compareIdTo(newList.get(newItemPos)); }

        @Override
        public boolean areContentsTheSame(int oldItemPos, int newItemPos) { return oldList.get(oldItemPos).compareContentsTo(newList.get(newItemPos)); }
    }
}
