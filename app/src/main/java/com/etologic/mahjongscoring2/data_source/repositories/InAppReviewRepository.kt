/*
 *     Copyright © 2023  Ernesto Vega de la Iglesia Soria
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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