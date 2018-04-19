package es.etologic.mahjongscoring2.domain.use_cases;

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;

public class CreatePlayerUseCase {

    private PlayersRepository playersRepository;

    public CreatePlayerUseCase(PlayersRepository playersRepository) { this.playersRepository = playersRepository; }

    public OperationResult<Player, BaseError> execute(String playerName) {
        long newPlayerId = playersRepository.insertOne(Player.getNewPlayer(playerName));
        Player newPlayer = playersRepository.getOne(newPlayerId);
        return newPlayerId > 0 ? new OperationResult<>(newPlayer) : new OperationResult<>(BaseError.getInsertionError());
    }
}
