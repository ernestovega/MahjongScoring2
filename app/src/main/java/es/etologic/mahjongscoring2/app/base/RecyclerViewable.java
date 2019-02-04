package es.etologic.mahjongscoring2.app.base;

public abstract class RecyclerViewable<T> {

    public abstract boolean compareIdTo(T object);

    public abstract boolean compareContentsTo(T object);

    public abstract T getCopy();
}