package com.airsaid.localization.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.labels.LinkLabel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author airsaid
 */
public class FixedLinkLabel extends LinkLabel<Object> {
  public FixedLinkLabel() {
    super("", AllIcons.Ide.Link);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        if (isEnabled() && isInClickableArea(e.getPoint())) {
          doClick();
        }
      }
    });
  }
}
