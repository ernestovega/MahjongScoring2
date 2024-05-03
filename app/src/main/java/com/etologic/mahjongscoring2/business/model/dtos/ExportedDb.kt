/*
 *     Copyright © 2024  Ernesto Vega de la Iglesia Soria
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

package com.etologic.mahjongscoring2.business.model.dtos

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

data class ExportedDb(
    val csvGames: File,
    val csvRounds: File,
) {
    fun toUriArrayList(context: Context, packageName: String): ArrayList<Uri> =
        arrayListOf(
            getUri(context, packageName, csvGames),
            getUri(context, packageName, csvRounds),
        )

    private fun getUri(context: Context, packageName: String, file: File): Uri =
        FileProvider.getUriForFile(context, "${packageName}.provider", file)
}