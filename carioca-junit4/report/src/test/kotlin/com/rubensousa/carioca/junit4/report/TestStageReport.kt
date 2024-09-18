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

package com.rubensousa.carioca.junit4.report

import com.rubensousa.carioca.report.runtime.StageReport

class TestStageReport : StageReport {

    val id: Int?

    constructor() : super() {
        id = null
    }

    constructor(id: Int) : super(id.toString()) {
        this.id = id
    }

    override fun toString(): String {
        return "TestReport: $id"
    }

}
