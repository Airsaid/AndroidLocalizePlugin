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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import java.awt.Dimension
import javax.swing.Action

/**
 * A dialog to show the supported languages of the [translator].
 *
 * @author airsaid
 */
class SupportedLanguagesDialog(private val translator: AbstractTranslator) : ComposeDialog() {

  private val supportedLanguages = translator.supportedLanguages.sortedBy { it.code }

  init {
    title = "${translator.name} Translator Supported Languages"
  }

  override fun preferredSize() = Dimension(460, 420)

  @Composable
  override fun Content() {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.background,
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
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 20.dp, vertical = 24.dp)
  ) {
    Text(
      text = "Supported languages (${languages.size})",
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onBackground
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
                style = MaterialTheme.typography.headlineLarge
              )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = language.name,
                style = MaterialTheme.typography.bodyMedium
              )
              Text(
                text = "${language.englishName} (${language.code})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
