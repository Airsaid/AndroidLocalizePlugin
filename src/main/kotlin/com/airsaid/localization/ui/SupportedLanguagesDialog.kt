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
 */

package com.airsaid.localization.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text

/**
 * A dialog to show the supported languages of the [translator].
 *
 * @author airsaid
 */
class SupportedLanguagesDialog(private val translator: AbstractTranslator) : ComposeDialog() {

  override val defaultPreferredSize
    get() = 460 to 420

  private val supportedLanguages = translator.supportedLanguages.sortedBy { it.code }

  init {
    title = "${translator.name} Translator Supported Languages"
  }

  @Composable
  override fun Content() {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JewelTheme.globalColors.panelBackground),
    ) {
      SupportLanguagesContent(languages = supportedLanguages)
    }
  }

  override fun getDimensionServiceKey(): String {
    return "#com.airsaid.localization.ui.SupportLanguagesDialog#${translator.key}"
  }
}

@Composable
private fun SupportLanguagesContent(languages: List<Lang>) {
  val listState = rememberLazyListState()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 24.dp)
  ) {
    Text(
      text = "Supported languages (${languages.size})",
      fontWeight = FontWeight.Medium,
      color = JewelTheme.globalColors.text.normal
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp)
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(languages, key = { it.code }) { language ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val flag = language.flag
            if (flag.isNotEmpty()) {
              Text(
                text = flag,
                fontSize = 22.sp
              )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = language.name,
                color = JewelTheme.globalColors.text.normal
              )
              Text(
                text = "${language.englishName} (${language.code})",
                color = JewelTheme.globalColors.text.info
              )
            }
          }
        }
      }
      VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd),
        adapter = rememberScrollbarAdapter(listState)
      )
    }
  }
}