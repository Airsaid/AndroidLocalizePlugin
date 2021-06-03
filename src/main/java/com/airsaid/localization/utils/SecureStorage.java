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
