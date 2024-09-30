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

package com.rubensousa.carioca.report.android.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.semantics.AccessibilityAction
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.toSize
import androidx.core.view.children
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher
import org.hamcrest.Matchers

object ComposeHierarchyInspector {

    /**
     * @param useUnmergedTree Find within merged composables like Buttons
     */
    fun dump(useUnmergedTree: Boolean): String {
        val semanticsOwner = getRootForTest()?.semanticsOwner ?: return ""
        val node = if (useUnmergedTree) {
            semanticsOwner.unmergedRootSemanticsNode
        } else {
            semanticsOwner.rootSemanticsNode
        }
        return node.printToString()
    }

    /**
     * Taken from SemanticsNodeInteraction
     */
    private fun SemanticsNode.printToString(maxDepth: Int = Int.MAX_VALUE): String {
        val sb = StringBuilder()
        printToStringInner(
            sb = sb,
            maxDepth = maxDepth,
            nestingLevel = 0,
            nestingIndent = "",
            isFollowedBySibling = false
        )
        return sb.toString()
    }

    private fun SemanticsNode.printToStringInner(
        sb: StringBuilder,
        maxDepth: Int,
        nestingLevel: Int,
        nestingIndent: String,
        isFollowedBySibling: Boolean,
    ) {
        val newIndent = if (nestingLevel == 0) {
            ""
        } else if (isFollowedBySibling) {
            "$nestingIndent | "
        } else {
            "$nestingIndent   "
        }

        if (nestingLevel > 0) {
            sb.append("$nestingIndent |-")
        }
        sb.append("Node #$id at ")
        sb.append(rectToShortString(unclippedGlobalBounds))

        if (config.contains(SemanticsProperties.TestTag)) {
            sb.append(", Tag: '")
            sb.append(config[SemanticsProperties.TestTag])
            sb.append("'")
        }

        val maxLevelReached = nestingLevel == maxDepth

        sb.appendConfigInfo(config, newIndent)

        if (maxLevelReached) {
            val childrenCount = children.size
            val siblingsCount = (parent?.children?.size ?: 1) - 1
            if (childrenCount > 0 || (siblingsCount > 0 && nestingLevel == 0)) {
                sb.appendLine()
                sb.append(newIndent)
                sb.append("Has ")
                if (childrenCount > 1) {
                    sb.append("$childrenCount children")
                } else if (childrenCount == 1) {
                    sb.append("$childrenCount child")
                }
                if (siblingsCount > 0 && nestingLevel == 0) {
                    if (childrenCount > 0) {
                        sb.append(", ")
                    }
                    if (siblingsCount > 1) {
                        sb.append("$siblingsCount siblings")
                    } else {
                        sb.append("$siblingsCount sibling")
                    }
                }
            }
            return
        }

        val childrenLevel = nestingLevel + 1
        val children = this.children.toList()
        children.forEachIndexed { index, child ->
            val hasSibling = index < children.size - 1
            sb.appendLine()
            child.printToStringInner(sb, maxDepth, childrenLevel, newIndent, hasSibling)
        }
    }

    private val SemanticsNode.unclippedGlobalBounds: Rect
        get() {
            return Rect(positionInWindow, size.toSize())
        }

    private fun rectToShortString(rect: Rect): String {
        return "(left=${rect.left}, top=${rect.top}, right=${rect.right}, bottom=${rect.bottom})px"
    }

    private fun StringBuilder.appendConfigInfo(config: SemanticsConfiguration, indent: String = "") {
        val actions = mutableListOf<String>()
        val units = mutableListOf<String>()
        for ((key, value) in config) {
            if (key == SemanticsProperties.TestTag) {
                continue
            }

            if (value is AccessibilityAction<*> || value is Function<*>) {
                // Avoids printing stuff like "action = 'AccessibilityAction\(label=null, action=.*\)'"
                actions.add(key.name)
                continue
            }

            if (value is Unit) {
                // Avoids printing stuff like "Disabled = 'kotlin.Unit'"
                units.add(key.name)
                continue
            }

            appendLine()
            append(indent)
            append(key.name)
            append(" = '")

            if (value is AnnotatedString) {
                if (value.paragraphStyles.isEmpty() && value.spanStyles.isEmpty() && value
                        .getStringAnnotations(0, value.text.length).isEmpty()
                ) {
                    append(value.text)
                } else {
                    // Save space if we there is text only in the object
                    append(value)
                }
            } else {
                append(value)
            }

            append("'")
        }

        if (units.isNotEmpty()) {
            appendLine()
            append(indent)
            append("[")
            append(units.joinToString(separator = ", "))
            append("]")
        }

        if (actions.isNotEmpty()) {
            appendLine()
            append(indent)
            append("Actions = [")
            append(actions.joinToString(separator = ", "))
            append("]")
        }

        if (config.isMergingSemanticsOfDescendants) {
            appendLine()
            append(indent)
            append("MergeDescendants = 'true'")
        }

        if (config.isClearingSemantics) {
            appendLine()
            append(indent)
            append("ClearAndSetSemantics = 'true'")
        }
    }

    private fun getRootForTest(): RootForTest? {
        var rootForTest: RootForTest? = null
        Espresso.onView(withId(android.R.id.content)).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.any(View::class.java)
            }

            override fun getDescription(): String {
                return "Finding a RootForTest in the view hierarchy"
            }

            override fun perform(uiController: UiController?, view: View) {
                if (view is ViewGroup) {
                    rootForTest = findRootForTest(view)
                }
            }
        })
        return rootForTest
    }

    private fun findRootForTest(viewGroup: ViewGroup): RootForTest? {
        if (viewGroup is RootForTest) {
            return viewGroup
        }
        viewGroup.children.forEach { child ->
            if (child is ViewGroup) {
                findRootForTest(child)?.let {
                    return it
                }
            }
        }
        return null
    }

}