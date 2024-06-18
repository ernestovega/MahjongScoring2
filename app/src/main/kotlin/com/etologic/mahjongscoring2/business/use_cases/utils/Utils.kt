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

package com.etologic.mahjongscoring2.business.use_cases.utils

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun normalizeName(name: String?): String =
    name?.trim()
        ?.replace("\"", "")
        ?: ""

fun Date.toFileNameFormat(): String =
    SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())
        .format(this)

fun writeToCsvFile(fileName: String, fileText: String, directory: File?): File =
    File(directory, fileName)
        .apply {
            writeText(fileText)
        }