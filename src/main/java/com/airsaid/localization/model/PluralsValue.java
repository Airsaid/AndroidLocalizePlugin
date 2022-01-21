package com.airsaid.localization.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class PluralsValue extends AbstractValue implements Cloneable {

  private final List<Item> items;

  public PluralsValue(String name, boolean translatable, List<Item> items) {
    super(name, translatable);
    this.items = items;
  }

  public List<Item> getItems() {
    return items;
  }

  @Override
  public PluralsValue clone() {
    PluralsValue clone = (PluralsValue) super.clone();
    List<Item> items = clone.getItems();
    List<Item> cloneItems = new ArrayList<>(items.size());
    for (Item item : items) { // deep clone
      cloneItems.add(item.clone());
    }
    return new PluralsValue(clone.getName(), clone.isTranslatable(), cloneItems);
  }

  @Override
  public String toString() {
    return "PluralsValue{" +
        "items=" + items +
        '}';
  }

  public static class Item implements Cloneable {
    private final String quantityName;
    private final List<Content> contents;

    public Item(String quantityName, List<Content> contents) {
      this.quantityName = quantityName;
      this.contents = contents;
    }

    public String getQuantityName() {
      return quantityName;
    }

    public List<Content> getContents() {
      return contents;
    }

    @Override
    public Item clone() {
      try {
        PluralsValue.Item clone = (PluralsValue.Item) super.clone();
        List<Content> contents = clone.getContents();
        List<Content> cloneContents = new ArrayList<>(contents.size());
        for (Content content : contents) { // deep clone
          cloneContents.add(content.clone());
        }
        return new Item(clone.quantityName, cloneContents);
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    public String toString() {
      return "Item{" +
          "quantityName='" + quantityName + '\'' +
          ", contents=" + contents +
          '}';
    }
  }
}
