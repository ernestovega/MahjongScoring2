package es.etologic.mahjongscoring2.domain.entities;

public interface DataCallback<R, E> {
    void onSuccess(R response);
    void onFailure(E errorBase);
}
