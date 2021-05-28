package com.airsaid.localization.action;

import com.airsaid.localization.translate.services.TranslatorService;
import com.airsaid.localization.translate.lang.Lang;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * @author airsaid
 */
public class TestAction extends AnAction {

  private static final Logger LOG = Logger.getInstance(TestAction.class);

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    TranslatorService translatorService = TranslatorService.getInstance();

    String text = Messages.showInputDialog("Please input the text to be translated: ", e.getPresentation().getText(), null);
    if (text != null && !text.isEmpty()) {
      translatorService.doTranslate(Lang.ENGLISH, Lang.CHINESE_SIMPLIFIED, text, result -> LOG.warn("result: " + result));
    }
  }
}
