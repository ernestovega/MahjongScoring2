package com.etologic.mahjongscoring2.data_source.repositories

import android.app.Activity
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode.INTERNAL_ERROR
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppReviewRepository @Inject constructor() {

    fun requestLaunch(activity: Activity): Single<Boolean> =
        Single.create { emitter ->
            val reviewManager = ReviewManagerFactory.create(activity.applicationContext)
            reviewManager.requestReviewFlow()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        reviewManager.launchReviewFlow(activity, task.result)
                            .addOnCompleteListener { _ ->
                                emitter.onSuccess(true)
                            }
                    } else {
                        emitter.onError(task.exception ?: ReviewException(INTERNAL_ERROR))
                    }
                }
        }
}