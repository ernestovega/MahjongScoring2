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

package com.etologic.mahjongscoring2.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import java.io.File
import java.lang.String.format
import java.util.Locale

fun Context.showShareGameDialog(
    shareGame: (ShareGameOptions) -> Unit,
) {
    AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        .setTitle(R.string.share_game)
        .setSingleChoiceItems(
            /* items = */ arrayOf(getString(R.string.plain_text), getString(R.string.table_csv)),
            /* checkedItem = */ -1,
        )   /* listener = */ { dialog, newSelectedItem ->
            shareGame(ShareGameOptions.fromIndex(newSelectedItem))
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel, null)
        .create()
        .show()
}

fun Activity.shareText(exportedGame: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, exportedGame)
    }
    val chooser = Intent.createChooser(shareIntent, getString(R.string.share_game_using))
    startActivity(chooser)
}

fun Activity.shareFiles(files: List<File>, mimeType: String = "text/csv") {
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = mimeType
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, files.toUriArrayList(applicationContext, packageName))
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    val chooser = Intent.createChooser(shareIntent, getString(R.string.share_files_using))
    startActivity(chooser)
}

private fun List<File>.toUriArrayList(context: Context, packageName: String): ArrayList<Uri> {
    fun getUri(context: Context, packageName: String, file: File): Uri =
        FileProvider.getUriForFile(context, "${packageName}.provider", file)

    val uris = this.map { getUri(context, packageName, it) }
    return arrayListOf(*uris.toTypedArray())
}

fun writeToFile(name: String, csvText: String, externalFilesDir: File?): File {
    val fileName = "$name.csv"
    val file = File(externalFilesDir, fileName)
    file.writeText(csvText)
    return file
}
