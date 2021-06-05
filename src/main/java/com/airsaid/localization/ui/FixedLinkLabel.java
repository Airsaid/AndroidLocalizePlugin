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

package com.airsaid.localization.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.labels.LinkLabel;

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
