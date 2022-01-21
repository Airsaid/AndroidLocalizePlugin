package com.airsaid.localization.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airsaid
 */
public class StringValue extends AbstractValue implements Cloneable {

  private final List<Content> contents;

  public StringValue(String name, boolean translatable, List<Content> contents) {
    super(name, translatable);
    this.contents = contents;
  }

  public List<Content> getContents() {
    return contents;
  }

  @Override
  public StringValue clone() {
    StringValue clone = (StringValue) super.clone();
    List<Content> contents = clone.getContents();
    List<Content> cloneContents = new ArrayList<>(contents.size());
    for (Content content : contents) { // deep clone
      cloneContents.add(content.clone());
    }
    return new StringValue(clone.getName(), clone.isTranslatable(), cloneContents);
  }

  @Override
  public String toString() {
    return "StringValue{" +
        "contents=" + contents +
        '}';
  }
}
