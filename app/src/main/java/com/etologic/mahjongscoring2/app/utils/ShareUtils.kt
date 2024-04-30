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
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.business.model.dtos.ExportedDb
import com.etologic.mahjongscoring2.business.model.enums.ShareGameOptions
import com.etologic.mahjongscoring2.data_source.model.GameId

fun Activity.shareExportedGame(exportedGame: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, exportedGame)
    }
    val chooser = Intent.createChooser(shareIntent, getString(R.string.share_game_using))
    startActivity(chooser)
}

fun Activity.shareExportedDb(exportedDb: ExportedDb) {
    val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "text/csv"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, exportedDb.toUriArrayList(applicationContext, packageName))
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    val chooser = Intent.createChooser(shareIntent, getString(R.string.share_files_using))
    startActivity(chooser)
}

fun Context.showShareGameDialog(
    gameId: GameId,
    getSelectedShareGameOption: () -> ShareGameOptions,
    setSelectedShareGameOption: (ShareGameOptions) -> Unit,
    shareGame: (GameId) -> Unit,
) {
    AlertDialog.Builder(this, R.style.AlertDialogStyleMM)
        .setTitle(R.string.share_game)
        .setSingleChoiceItems(
            /* items = */ arrayOf(getString(R.string.final_results), getString(R.string.complete_game)),
            /* checkedItem = */ getSelectedShareGameOption.invoke().index,
        )   /* listener = */ { _, newSelectedItem ->
            setSelectedShareGameOption.invoke(ShareGameOptions.fromIndex(newSelectedItem))
        }
        .setPositiveButton(R.string.share) { _, _ -> shareGame(gameId) }
        .setNegativeButton(R.string.cancel, null)
        .create()
        .show()
}