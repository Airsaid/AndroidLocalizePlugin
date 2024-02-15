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

package com.airsaid.localization.config;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.services.TranslatorService;
import com.airsaid.localization.ui.FixedLinkLabel;
import com.airsaid.localization.ui.SupportLanguagesDialog;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * @author airsaid
 */
public class SettingsComponent {
  private static final Logger LOG = Logger.getInstance(SettingsComponent.class);

  private JPanel contentJPanel;
  private ComboBox<AbstractTranslator> translatorsComboBox;
  private JBLabel appIdLabel;
  private JBTextField appIdField;
  private JBLabel appKeyLabel;
  private JBPasswordField appKeyField;
  private FixedLinkLabel applyLink;
  private JButton supportLanguagesButton;
  private JLabel maxCacheSizeLabel;
  private JBCheckBox enableCacheCheckBox;
  private ComboBox<String> maxCacheSizeComboBox;
  private ComboBox<String> translationIntervalComboBox;
  private JCheckBox skipNonTranslatableCheckBox;

  public SettingsComponent() {
    initTranslatorComponents();
    initCacheComponents();
  }

  private void initTranslatorComponents() {
    translatorsComboBox.setRenderer(new SimpleListCellRenderer<>() {
      @Override
      public void customize(@NotNull JList<? extends AbstractTranslator> list, AbstractTranslator value, int index, boolean selected, boolean hasFocus) {
        setText(value.getName());
        setIcon(value.getIcon());
      }
    });
    translatorsComboBox.addItemListener(itemEvent -> {
      if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
        setSelectedTranslator(getSelectedTranslator());
      }
    });
    applyLink.setListener((aSource, aLinkData) -> {
      AbstractTranslator selectedTranslator = getSelectedTranslator();
      String applyAppIdUrl = selectedTranslator.getApplyAppIdUrl();
      if (!StringUtil.isEmpty(applyAppIdUrl)) {
        BrowserUtil.browse(applyAppIdUrl);
        applyLink.setFocusable(false);
      }
    }, null);
    supportLanguagesButton.addActionListener(actionEvent -> {
      showSupportLanguagesDialog(getSelectedTranslator());
    });
  }

  private void initCacheComponents() {
    enableCacheCheckBox.addItemListener(event -> {
      if (event.getStateChange() == ItemEvent.SELECTED) {
        setEnableCache(true);
      } else if (event.getStateChange() == ItemEvent.DESELECTED) {
        setEnableCache(false);
      }
    });
  }

  @NotNull
  public AbstractTranslator getSelectedTranslator() {
    return (AbstractTranslator) Objects.requireNonNull(translatorsComboBox.getSelectedItem());
  }

  private void showSupportLanguagesDialog(AbstractTranslator selectedTranslator) {
    new SupportLanguagesDialog(selectedTranslator).show();
  }

  public JPanel getContent() {
    return contentJPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return translatorsComboBox;
  }

  public void setTranslators(@NotNull Map<String, AbstractTranslator> translators) {
    LOG.info("setTranslators: " + translators.keySet());
    translatorsComboBox.setModel(new CollectionComboBoxModel<>(new ArrayList<>(translators.values())));
  }

  public void setSelectedTranslator(@NotNull AbstractTranslator selected) {
    LOG.info("setSelectedTranslator: " + selected);
    translatorsComboBox.setSelectedItem(selected);

    boolean isNeedAppId = selected.isNeedAppId();
    appIdLabel.setVisible(isNeedAppId);
    appIdField.setVisible(isNeedAppId);
    if (isNeedAppId) {
      appIdLabel.setText(selected.getAppIdDisplay() + ":");
      appIdField.setText(selected.getAppId());
    }

    boolean isNeedAppKey = selected.isNeedAppKey();
    appKeyLabel.setVisible(isNeedAppKey);
    appKeyField.setVisible(isNeedAppKey);
    if (isNeedAppKey) {
      appKeyLabel.setText(selected.getAppKeyDisplay() + ":");
      appKeyField.setText(selected.getAppKey());
    }

    String applyAppIdUrl = selected.getApplyAppIdUrl();
    if (!StringUtil.isEmpty(applyAppIdUrl)) {
      applyLink.setVisible(true);
      new HelpTooltip()
          .setDescription("Apply for " + selected.getName() + " translation API service")
          .installOn(applyLink);
    } else {
      applyLink.setVisible(false);
    }
  }

  public boolean isSelectedDefaultTranslator() {
    return isSelectedDefaultTranslator(getSelectedTranslator());
  }

  private boolean isSelectedDefaultTranslator(@NotNull AbstractTranslator selected) {
    return selected == TranslatorService.getInstance().getDefaultTranslator();
  }

  @NotNull
  public String getAppId() {
    String appId = appIdField.getText();
    return appId != null ? appId : "";
  }

  public void setAppId(@NotNull String appId) {
    appIdField.setText(appId);
  }

  @NotNull
  public String getAppKey() {
    char[] password = appKeyField.getPassword();
    return password != null ? String.valueOf(password) : "";
  }

  public void setAppKey(@NotNull String appKey) {
    appKeyField.setText(appKey);
  }

  public void setEnableCache(boolean isEnable) {
    enableCacheCheckBox.setSelected(isEnable);
    maxCacheSizeComboBox.setVisible(isEnable);
    maxCacheSizeLabel.setVisible(isEnable);
  }

  public boolean isEnableCache() {
    return enableCacheCheckBox.isSelected();
  }

  public int getMaxCacheSize() {
    return Integer.parseInt((String) Objects.requireNonNull(maxCacheSizeComboBox.getSelectedItem()));
  }

  public void setMaxCacheSize(int maxCacheSize) {
    maxCacheSizeComboBox.setSelectedItem(String.valueOf(maxCacheSize));
  }

  public int getTranslationInterval() {
    return Integer.parseInt((String) Objects.requireNonNull(translationIntervalComboBox.getSelectedItem()));
  }

  public void setTranslationInterval(int intervalTime) {
    translationIntervalComboBox.setSelectedItem(String.valueOf(intervalTime));
  }

  public boolean isSkipNonTranslatable() {
    return skipNonTranslatableCheckBox.isSelected();
  }

  public void setSkipNonTranslatable(boolean isSkipNonTranslatable) {
    skipNonTranslatableCheckBox.setSelected(isSkipNonTranslatable);
  }
}
