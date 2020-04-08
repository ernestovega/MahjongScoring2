/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
