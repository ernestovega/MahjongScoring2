package es.etologic.mahjongscoring2.app.base

abstract class RecyclerViewable<T> {
    
    abstract fun getCopy(): T
    
    abstract fun compareIdTo(`object`: T): Boolean
    
    abstract fun compareContentsTo(`object`: T): Boolean
}