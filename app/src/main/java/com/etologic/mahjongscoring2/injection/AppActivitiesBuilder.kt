/*
    Copyright (C) 2020  Ernesto Vega de la Iglesia Soria
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.etologic.mahjongscoring2.injection

import com.etologic.mahjongscoring2.app.game.activity.GameActivity
import com.etologic.mahjongscoring2.app.game.activity.GameActivityFragmentsBuilder
import com.etologic.mahjongscoring2.app.game.activity.GameActivityModule
import com.etologic.mahjongscoring2.app.main.activity.MainActivity
import com.etologic.mahjongscoring2.app.main.activity.MainActivityFragmentsBuilder
import com.etologic.mahjongscoring2.app.main.activity.MainActivityModule
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivity
import com.etologic.mahjongscoring2.app.main.combinations.CombinationsActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AppActivitiesBuilder {
    
    @ContributesAndroidInjector(modules = [MainActivityModule::class, MainActivityFragmentsBuilder::class])
    internal abstract fun bindMainActivity(): MainActivity
    
    @ContributesAndroidInjector(modules = [CombinationsActivityModule::class])
    internal abstract fun bindCombinationsActivity(): CombinationsActivity
    
    @ContributesAndroidInjector(modules = [GameActivityModule::class, GameActivityFragmentsBuilder::class])
    internal abstract fun bindGameActivity(): GameActivity
}
