package es.etologic.mahjongscoring2.domain.use_cases;

import android.arch.lifecycle.LiveData;

import java.util.List;

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class GetAllPlayersUseCase {

    private PlayersRepository playersRepository;

    public GetAllPlayersUseCase(PlayersRepository playersRepository) { this.playersRepository = playersRepository; }

    public LiveData<List<Player>> execute() {
        return playersRepository.getAll();
    }
}
