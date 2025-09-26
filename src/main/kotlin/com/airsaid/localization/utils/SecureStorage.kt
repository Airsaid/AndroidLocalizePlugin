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

package com.airsaid.localization.utils

import com.airsaid.localization.constant.Constants
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

/**
 * @author airsaid
 */
class SecureStorage(private val key: String) {

  fun save(text: String) {
    val credentialAttributes = createCredentialAttributes()
    val credentials = Credentials(key, text)
    PasswordSafe.instance.set(credentialAttributes, credentials)
  }

  fun read(): String {
    val password = PasswordSafe.instance.getPassword(createCredentialAttributes())
    return password ?: ""
  }

  private fun createCredentialAttributes(): CredentialAttributes {
    return CredentialAttributes(generateServiceName(Constants.PLUGIN_NAME, key))
  }
}