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

package com.etologic.mahjongscoring2.app.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.etologic.mahjongscoring2.BuildConfig
import com.etologic.mahjongscoring2.R
import com.etologic.mahjongscoring2.app.screens.MainActivity

abstract class BaseMainFragment : Fragment() {

    protected open val onBackOrUpClick: () -> Unit = { findNavController().navigateUp() }

    protected open val toolbarMenuProvider: MenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                android.R.id.home -> onBackOrUpClick.invoke()
                else -> return false
            }
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        setToolbarMenuProvider()
    }

    private fun setToolbarMenuProvider() {
        (activity as? MainActivity)?.apply {
            addMenuProvider(toolbarMenuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnBackBehaviour()
    }

    private fun setOnBackBehaviour() {
        requireActivity().onBackPressedDispatcher.addCallback(
            owner = viewLifecycleOwner,
            onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackOrUpClick.invoke()
                }
            }
        )
    }

    protected fun showErrorDialog(throwable: Throwable?) {
        val message = if (BuildConfig.DEBUG && throwable != null) throwable.toString() else getString(R.string.ups_something_wrong)
        AlertDialog.Builder(requireContext(), R.style.AlertDialogStyleMM)
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
