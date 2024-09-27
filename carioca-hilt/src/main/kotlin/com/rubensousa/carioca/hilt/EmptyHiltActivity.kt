/*
 * Copyright 2024 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rubensousa.carioca.hilt

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.rubensousa.carioca.android.report.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class EmptyHiltActivity : FragmentActivity() {

    internal var theme: Int = DEFAULT_THEME

    override fun onCreate(savedInstanceState: Bundle?) {
        theme = intent.getIntExtra(THEME_EXTRAS_BUNDLE_KEY, theme)
        setTheme(theme)
        super.onCreate(savedInstanceState)
    }

    companion object {
        const val THEME_EXTRAS_BUNDLE_KEY = "com.rubensousa.carioca.hilt.EmptyHiltActivity.theme"

        val DEFAULT_THEME = R.style.HiltEmptyActivityTheme
    }

}
