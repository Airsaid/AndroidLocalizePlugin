/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.airsaid.localization.ui

import com.intellij.icons.AllIcons
import com.intellij.ui.components.labels.LinkLabel
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

/**
 * Fixed the problem that sometimes click does not respond.
 *
 * @author airsaid
 */
class FixedLinkLabel : LinkLabel<Any>("", AllIcons.Ide.Link) {

    private var isDoClick = false

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (isEnabled && isInClickableArea(e.point)) {
                    doClick()
                }
            }

            override fun mouseExited(e: MouseEvent) {
                isDoClick = false
            }
        })
    }

    override fun doClick() {
        if (!isDoClick) {
            isDoClick = true
            super.doClick()
        }
    }
}