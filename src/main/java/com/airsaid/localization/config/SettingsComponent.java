package com.airsaid.localization.config;

import com.airsaid.localization.translate.AbstractTranslator;
import com.airsaid.localization.translate.services.TranslatorService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.SimpleListCellRenderer;
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

  public SettingsComponent() {
    translatorsComboBox.setRenderer(new SimpleListCellRenderer<AbstractTranslator>() {
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
  }

  @NotNull
  public AbstractTranslator getSelectedTranslator() {
    return (AbstractTranslator) Objects.requireNonNull(translatorsComboBox.getSelectedItem());
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
    if (isSelectedDefaultTranslator(selected)) {
      appIdLabel.setVisible(false);
      appKeyLabel.setVisible(false);
      appIdField.setVisible(false);
      appKeyField.setVisible(false);
    } else {
      appIdLabel.setVisible(true);
      appKeyLabel.setVisible(true);
      appIdField.setVisible(true);
      appKeyField.setVisible(true);
      appIdField.setText(selected.getAppId());
      appKeyField.setText(selected.getAppKey());
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
}
