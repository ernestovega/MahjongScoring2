package com.etologic.mahjongscoring2.business.use_cases

import android.app.Activity
import android.util.Log
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.data_source.repositories.InAppReviewRepository
import io.reactivex.Single
import javax.inject.Inject

class ShowInAppReviewUseCase @Inject constructor(
    private val inAppReviewRepository: InAppReviewRepository
) {

    internal operator fun invoke(activity: Activity): Single<Boolean> =
        inAppReviewRepository.requestLaunch(activity)
            .doOnSuccess {
                if (BuildConfig.DEBUG) {
                    Log.d("ShowInAppReviewUseCase", "Successful InAppReview request")
                }
            }
            .doOnError {
                if (BuildConfig.DEBUG) {
                    Log.e("ShowInAppReviewUseCase", null, it)
                }
            }
}
