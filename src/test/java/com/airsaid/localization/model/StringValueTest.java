package com.airsaid.localization.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author airsaid
 */
class StringValueTest {

  @Test
  void testClone() {
    List<Content> contents = new ArrayList<>();
    contents.add(new Content("test"));
    StringValue stringValue = new StringValue("name", false, contents);
    StringValue stringValueClone = stringValue.clone();

    assertNotSame(stringValue, stringValueClone);
    assertEquals(contents.size(), stringValueClone.getContents().size());

    contents.add(new Content("test2"));
    assertNotEquals(contents.size(), stringValueClone.getContents().size());
  }

  @Test
  void testEquals() {
    StringValue stringValue1 = new StringValue("name", false, Collections.emptyList());
    StringValue stringValue2 = new StringValue("name", true, Collections.emptyList());
    assertEquals(stringValue1, stringValue2);
  }

  @Test
  void testNotEquals() {
    StringValue stringValue1 = new StringValue("name", true, Collections.emptyList());
    StringValue stringValue2 = new StringValue("name2", true, Collections.emptyList());
    assertNotEquals(stringValue1, stringValue2);
  }
}
