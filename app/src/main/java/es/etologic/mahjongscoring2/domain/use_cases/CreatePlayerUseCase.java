package es.etologic.mahjongscoring2.domain.use_cases;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.entities.Game;
import es.etologic.mahjongscoring2.domain.entities.Player;
import es.etologic.mahjongscoring2.domain.operation_objects.BaseError;
import es.etologic.mahjongscoring2.domain.operation_objects.OperationResult;
import io.reactivex.Observable;
import io.reactivex.Single;

public class CreatePlayerUseCase {

    private PlayersRepository playersRepository;

    @Inject
    public CreatePlayerUseCase(PlayersRepository playersRepository) { this.playersRepository = playersRepository; }

    public Single<Long> createPlayer(String playerName) {
        return playersRepository.insertOne(Player.getNewPlayer(playerName));
    }
}
