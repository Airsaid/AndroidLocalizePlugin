package com.airsaid.localization.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author airsaid
 */
class StringArrayValueTest {

  @Test
  void testClone() {
    List<Content> contents = new ArrayList<>();
    contents.add(new Content("one"));

    List<StringArrayValue.Item> items = new ArrayList<>();
    items.add(new StringArrayValue.Item(contents));

    StringArrayValue stringArrayValue = new StringArrayValue("name", false, items);
    StringArrayValue stringArrayValueClone = stringArrayValue.clone();

    assertNotSame(stringArrayValue, stringArrayValueClone);
    assertEquals(items.size(), stringArrayValueClone.getItems().size());

    List<Content> contents2 = new ArrayList<>();
    contents2.add(new Content("two"));
    items.add(new StringArrayValue.Item(contents2));
    assertNotEquals(items.size(), stringArrayValueClone.getItems().size());
  }

  @Test
  void testEquals() {
    StringArrayValue stringArrayValue1 = new StringArrayValue("name", false, Collections.emptyList());
    StringArrayValue stringArrayValue2 = new StringArrayValue("name", true, Collections.emptyList());
    assertEquals(stringArrayValue1, stringArrayValue2);
  }

  @Test
  void testNotEquals() {
    StringArrayValue stringArrayValue1 = new StringArrayValue("name", true, Collections.emptyList());
    StringArrayValue stringArrayValue2 = new StringArrayValue("name2", true, Collections.emptyList());
    assertNotEquals(stringArrayValue1, stringArrayValue2);
  }
}
