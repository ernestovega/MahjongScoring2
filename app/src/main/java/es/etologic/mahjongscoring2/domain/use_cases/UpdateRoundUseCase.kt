package es.etologic.mahjongscoring2.domain.use_cases

import es.etologic.mahjongscoring2.data.repositories.GamesRepository
import es.etologic.mahjongscoring2.data.repositories.RoundsRepository
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import es.etologic.mahjongscoring2.domain.model.Round
import io.reactivex.Single
import javax.inject.Inject

class UpdateRoundUseCase @Inject
constructor(private val roundsRepository: RoundsRepository, private val gamesRepository: GamesRepository) {
    
    internal fun getGameRounds(gameId: Long): Single<List<Round>> {
        return roundsRepository.getRoundsByGame(gameId)
    }
    
    internal fun addRound(round: Round): Single<GameWithRounds> = roundsRepository.insertOne(round)
        .flatMap { gamesRepository.getOneWithRounds(round.gameId.toLong()) } //FixMe: ¿ToLong???
    
    private fun saveRonRound(requestedPoints: Int) {
//        mCurrentRound.handPoints = requestedPoints
//        mCurrentRound.winnerInitialPosition = selectedPlayerSeat.initialSeat
//        val discarderInitialSeat = GameRoundsUtils.getPlayerInitialSeatByCurrentSeat(mDiscarderCurrentSeat, mCurrentRound.roundId)
//        mCurrentRound.discarderInitialPosition = discarderInitialSeat
//        mCurrentRound.setAllPlayersRonPoints(selectedPlayerSeat.initialSeat, requestedPoints, discarderInitialSeat)
//        saveCurrentRoundAndStartNext()
    }
    
    private fun saveTsumoRound(requestedPoints: Int) {
//        mCurrentRound.handPoints = requestedPoints
//        mCurrentRound.winnerInitialPosition = selectedPlayerSeat.initialSeat
//        mCurrentRound.setAllPlayersTsumoPoints(selectedPlayerSeat.initialSeat, requestedPoints)
//        saveCurrentRoundAndStartNext()
    }
    
    private fun saveDrawRound() {
//        mCurrentRound.applyAllPlayersPenalties()
//        //        mCurrentRound.setRoundDuration();
//        disposables.add(
//            updateRoundsUseCase.addRound(mCurrentRound)
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe { progressState.postValue(SHOW) }
//                .doOnEvent { _, _ -> progressState.postValue(HIDE) }
//                .subscribe({ this.getGameSuccess(it) }, { error.postValue(it) })
//        )
    }
}
