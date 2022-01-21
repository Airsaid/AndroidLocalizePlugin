package com.airsaid.localization.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author airsaid
 */
class PluralsValueTest {

  @Test
  void testClone() {
    List<Content> contents = new ArrayList<>();
    contents.add(new Content("test"));

    List<PluralsValue.Item> items = new ArrayList<>();
    items.add(new PluralsValue.Item("numberOfSongsAvailable", contents));

    PluralsValue pluralsValue = new PluralsValue("name", false, items);
    PluralsValue pluralsValueClone = pluralsValue.clone();

    assertNotSame(pluralsValue, pluralsValueClone);
    assertEquals(items.size(), pluralsValueClone.getItems().size());

    List<Content> contents2 = new ArrayList<>();
    contents2.add(new Content("test2"));
    items.add(new PluralsValue.Item("numberOfSongsAvailable2", contents2));
    assertNotEquals(items.size(), pluralsValueClone.getItems().size());
  }

  @Test
  void testEquals() {
    PluralsValue pluralsValue1 = new PluralsValue("name", false, Collections.emptyList());
    PluralsValue pluralsValue2 = new PluralsValue("name", true, Collections.emptyList());
    assertEquals(pluralsValue1, pluralsValue2);
  }

  @Test
  void testNotEquals() {
    PluralsValue pluralsValue1 = new PluralsValue("name", true, Collections.emptyList());
    PluralsValue pluralsValue2 = new PluralsValue("name2", true, Collections.emptyList());
    assertNotEquals(pluralsValue1, pluralsValue2);
  }
}
