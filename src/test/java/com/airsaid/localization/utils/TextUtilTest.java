package com.airsaid.localization.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author airsaid
 */
class TextUtilTest {

  @Test
  void isEmptyOrSpacesLineBreak() {
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak(null));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak(""));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak(" "));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak("   "));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak("\r"));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak("\n"));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak("\r\n"));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak(" \r\n "));
    assertTrue(TextUtil.isEmptyOrSpacesLineBreak(" \r \n "));

    assertFalse(TextUtil.isEmptyOrSpacesLineBreak("text"));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak("text "));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak(" text"));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak(" text "));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak("\ntext"));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak("text\n"));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak("\rtext"));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak("text\r"));
    assertFalse(TextUtil.isEmptyOrSpacesLineBreak("\r\ntext\r\n"));
  }
}