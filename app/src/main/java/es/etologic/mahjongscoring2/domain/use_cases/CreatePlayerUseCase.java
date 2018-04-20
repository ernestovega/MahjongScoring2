package es.etologic.mahjongscoring2.domain.use_cases;

import android.arch.lifecycle.LiveData;

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class CreatePlayerUseCase {

    private PlayersRepository playersRepository;

    public CreatePlayerUseCase(PlayersRepository playersRepository) { this.playersRepository = playersRepository; }

    public OperationResult<Player, BaseError> execute(String playerName) {
        long playerId = playersRepository.insertOne(Player.getNewPlayer(playerName));
        if (playerId > 0) {
            Player player = playersRepository.getOne(playerId);
            return new OperationResult<>(player);
        }
        else return new OperationResult<>(BaseError.getInsertionError());
    }
}
