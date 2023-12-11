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

package com.etologic.mahjongscoring2.business.use_cases

import android.app.Activity
import android.util.Log
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.data_source.repositories.InAppReviewRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ShowInAppReviewUseCase @Inject constructor(
    private val inAppReviewRepository: InAppReviewRepository
) {
    suspend operator fun invoke(activity: Activity) {
        inAppReviewRepository.requestLaunch(activity)
            .onEach { reviewInfo ->
                if (BuildConfig.DEBUG) {
                    if (reviewInfo != null) {
                        Log.d("ShowInAppReviewUseCase", "Successful InAppReview request")
                    } else {
                        Log.e("ShowInAppReviewUseCase", null, Exception("In-App Review not launched"))
                    }
                }
            }
            .first()
    }
}
