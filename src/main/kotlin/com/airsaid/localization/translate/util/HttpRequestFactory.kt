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

package com.airsaid.localization.translate.util

import com.intellij.util.io.HttpRequests
import com.intellij.util.io.RequestBuilder

/**
 * Factory methods for creating [RequestBuilder] instances that honour the IDE's
 * proxy and timeout settings.
 *
 * @author airsaid
 */
object HttpRequestFactory {

  private const val DEFAULT_TIMEOUT_MS = 60 * 1000

  fun post(url: String, contentType: String, timeoutMs: Int = DEFAULT_TIMEOUT_MS): RequestBuilder {
    return HttpRequests.post(url, contentType)
      .productNameAsUserAgent()
      .connectTimeout(timeoutMs)
      .readTimeout(timeoutMs)
  }

  fun get(url: String, timeoutMs: Int = DEFAULT_TIMEOUT_MS): RequestBuilder {
    return HttpRequests.request(url)
      .productNameAsUserAgent()
      .connectTimeout(timeoutMs)
      .readTimeout(timeoutMs)
  }
}
