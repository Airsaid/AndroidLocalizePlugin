package com.airsaid.localization.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class StringArrayValue extends AbstractValue implements Cloneable {

  private final List<Item> items;

  public StringArrayValue(String name, boolean translatable, List<Item> items) {
    super(name, translatable);
    this.items = items;
  }

  public List<Item> getItems() {
    return items;
  }

  @Override
  public StringArrayValue clone() {
    StringArrayValue clone = (StringArrayValue) super.clone();
    List<StringArrayValue.Item> items = clone.getItems();
    List<StringArrayValue.Item> cloneItems = new ArrayList<>(items.size());
    for (StringArrayValue.Item item : items) { // deep clone
      cloneItems.add(item.clone());
    }
    return new StringArrayValue(clone.getName(), clone.isTranslatable(), cloneItems);
  }

  @Override
  public String toString() {
    return "StringArrayValue{" +
        "items=" + items +
        '}';
  }

  public static class Item implements Cloneable {
    private final List<Content> contents;

    public Item(List<Content> contents) {
      this.contents = contents;
    }

    public List<Content> getContents() {
      return contents;
    }

    @Override
    public StringArrayValue.Item clone() {
      try {
        StringArrayValue.Item clone = (StringArrayValue.Item) super.clone();
        List<Content> contents = clone.getContents();
        List<Content> cloneContents = new ArrayList<>(contents.size());
        for (Content content : contents) { // deep clone
          cloneContents.add(content.clone());
        }
        return new Item(cloneContents);
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    public String toString() {
      return "Item{" +
          "contents=" + contents +
          '}';
    }
  }
}
