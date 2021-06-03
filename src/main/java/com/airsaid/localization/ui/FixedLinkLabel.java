package com.airsaid.localization.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.labels.LinkLabel;
import jdk.javadoc.internal.doclets.toolkit.util.DocLink;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Fixed the problem that sometimes click does not respond.
 *
 * @author airsaid
 */
public class FixedLinkLabel extends LinkLabel<Object> {
  private boolean isDoClick = false;

  public FixedLinkLabel() {
    super("", AllIcons.Ide.Link);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        if (isEnabled() && isInClickableArea(e.getPoint())) {
          doClick();
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        isDoClick = false;
      }
    });
  }

  @Override
  public void doClick() {
    if (!isDoClick) {
      isDoClick = true;
      super.doClick();
    }
  }
}
