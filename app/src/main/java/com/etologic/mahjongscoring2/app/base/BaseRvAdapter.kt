/*
 * Copyright © 2018 LaLiga.
 * All rights reserved.
 */

package com.etologic.mahjongscoring2.app.base

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseRvAdapter<T : RecyclerViewable<T>> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    protected var collection: List<T> = ArrayList()
    
    fun updateCollection(newCollection: List<T>?) {
        if (newCollection == null) {
            saveNewCollectionCopy(ArrayList())
            notifyDataSetChanged()
        } else {
            val result = calculateDiff(DiffUtilCallback(collection, newCollection), true)
            saveNewCollectionCopy(newCollection)
            result.dispatchUpdatesTo(this)
        }
    }
    
    private fun saveNewCollectionCopy(newCollection: List<T>) {
        val newCollectionCopy = ArrayList<T>(newCollection.size)
        for (newItem in newCollection) {
            newCollectionCopy.add(newItem.getCopy())
        }
        collection = newCollectionCopy
    }
    
    //LIFECYCLE
    override fun getItemCount(): Int = collection.size
    
    //INNER CLASSES
    internal inner class DiffUtilCallback<V : RecyclerViewable<V>>(private val oldList: List<V>, private val newList: List<V>) : DiffUtil.Callback() {
        
        override fun getOldListSize(): Int {
            return oldList.size
        }
        
        override fun getNewListSize(): Int {
            return newList.size
        }
        
        override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
            return oldList[oldItemPos].compareIdTo(newList[newItemPos])
        }
        
        override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
            return oldList[oldItemPos].compareContentsTo(newList[newItemPos])
        }
    }
}
