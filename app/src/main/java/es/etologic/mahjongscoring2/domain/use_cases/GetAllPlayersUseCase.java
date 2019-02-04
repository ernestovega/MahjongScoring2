package es.etologic.mahjongscoring2.domain.use_cases;

import java.util.List;

import javax.inject.Inject;

import es.etologic.mahjongscoring2.data.repositories.PlayersRepository;
import es.etologic.mahjongscoring2.domain.model.Player;
import io.reactivex.Single;

public class GetAllPlayersUseCase {

    private PlayersRepository playersRepository;

    @Inject
    public GetAllPlayersUseCase(PlayersRepository playersRepository) { this.playersRepository = playersRepository; }

    public Single<List<Player>> getAll() {
        return playersRepository.getAllPlayers();
    }
}
