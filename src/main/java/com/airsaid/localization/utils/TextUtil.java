package com.airsaid.localization.utils;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author airsaid
 */
public class TextUtil {

  public static boolean isEmptyOrSpacesLineBreak(@Nullable CharSequence s) {
    if (StringUtil.isEmpty(s)) {
      return true;
    }
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) != ' ' && s.charAt(i) != '\r' && s.charAt(i) != '\n') {
        return false;
      }
    }
    return true;
  }

}
