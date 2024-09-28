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

package com.rubensousa.carioca.sample.logger

import com.rubensousa.carioca.android.sample.logger.Logger
import com.rubensousa.carioca.android.sample.logger.LoggingModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LoggingModule::class]
)
@Module
class TestLoggingModule {

    @Singleton
    @Provides
    fun provideTestLogger() : TestLogger {
        return TestLogger()
    }

    @Singleton
    @Provides
    fun provideLogger(instance: TestLogger) : Logger {
        return instance
    }

}
