package es.etologic.mahjongscoring2.domain.use_cases;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.entities.Player;
import io.reactivex.Single;

public class GetPlayerUseCase {

    private final PlayersRepository playersRepository;

    @Inject
    public GetPlayerUseCase(PlayersRepository playersRepository) { this.playersRepository = playersRepository; }

    public Single<Player> getPlayer(Long playerId) {
        return playersRepository.getOne(playerId);
    }
}
