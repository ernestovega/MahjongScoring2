package es.etologic.mahjongscoring2.app.game.dialogs.winning_hand_dialog

import androidx.lifecycle.MutableLiveData
import es.etologic.mahjongscoring2.app.base.BaseViewModel
import es.etologic.mahjongscoring2.app.game.dialogs.winning_hand_dialog.WinningHandDialogFragmentViewModel.Companion.WinningHandPages.Companion.getType
import es.etologic.mahjongscoring2.app.model.ShowState.HIDE
import es.etologic.mahjongscoring2.app.model.ShowState.SHOW
import es.etologic.mahjongscoring2.domain.model.GameWithRounds
import es.etologic.mahjongscoring2.domain.use_cases.GetCurrentGameUseCase
import io.reactivex.schedulers.Schedulers

class WinningHandDialogFragmentViewModel internal constructor(
    private val getGetCurrentGameUseCase: GetCurrentGameUseCase
) : BaseViewModel() {
    
    companion object {
        enum class WinningHandPages(val pageIndex: Int) {
            HAND_ACTION(0),
            POINTS(1);
            
            companion object {
                private val map = values().associateBy(WinningHandPages::pageIndex)
                internal fun getType(type: Int) = map[type] ?: HAND_ACTION
            }
        }
    }
    
    private val currentPage = MutableLiveData<WinningHandPages>()
    private lateinit var currentGame: GameWithRounds
    
    internal fun setCurrentPage(pageIndex: Int) {
        currentPage.postValue(getType(pageIndex))
    }
    
    internal fun loadGame() {
        disposables.add(
            getGetCurrentGameUseCase.getCurrentGameWithRounds()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressState.postValue(SHOW) }
                .subscribe(
                    {
                        currentGame = it
                        progressState.postValue(HIDE)
                    },
                    {
                        error.postValue(it)
                        progressState.postValue(HIDE)
                    })
        )
    }
}
