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

package com.airsaid.localization.utils;

import com.airsaid.localization.constant.Constants;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 */
public class SecureStorage {

  private final String key;

  public SecureStorage(@NotNull String key) {
    this.key = key;
  }

  public void save(@NotNull String text) {
    CredentialAttributes credentialAttributes = createCredentialAttributes();
    Credentials credentials = new Credentials(key, text);
    PasswordSafe.getInstance().set(credentialAttributes, credentials);
  }

  @NotNull
  public String read() {
    String password = PasswordSafe.getInstance().getPassword(createCredentialAttributes());
    return password != null ? password : "";
  }

  @NotNull
  private CredentialAttributes createCredentialAttributes() {
    return new CredentialAttributes(CredentialAttributesKt.generateServiceName(Constants.PLUGIN_NAME, key));
  }
}
