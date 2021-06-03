package com.airsaid.localization.translate;

import com.airsaid.localization.translate.lang.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author airsaid
 */
public interface TranslatorConfigurable {

  @NotNull
  String getKey();

  @NotNull
  String getName();

  @Nullable
  Icon getIcon();

  @NotNull
  List<Lang> getSupportedLanguages();

  @Nullable
  String getAppId();

  @Nullable
  String getAppKey();

}
