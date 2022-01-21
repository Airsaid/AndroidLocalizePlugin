package com.airsaid.localization.model;

/**
 * @author airsaid
 */
public class Content implements Cloneable {
  private String text;
  private String id;
  private String example;
  private boolean isIgnore;

  public Content(String text) {
    this.text = text;
  }

  public Content(String text, String id, String example, boolean isIgnore) {
    this.text = text;
    this.id = id;
    this.example = example;
    this.isIgnore = isIgnore;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getExample() {
    return example;
  }

  public void setExample(String example) {
    this.example = example;
  }

  public boolean isIgnore() {
    return isIgnore;
  }

  public void setIgnore(boolean ignore) {
    isIgnore = ignore;
  }

  @Override
  public Content clone() {
    try {
      return (Content) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String toString() {
    return "Content{" +
        "text='" + text + '\'' +
        ", id='" + id + '\'' +
        ", example='" + example + '\'' +
        ", isIgnore=" + isIgnore +
        '}';
  }
}