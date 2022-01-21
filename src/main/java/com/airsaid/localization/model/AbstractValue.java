package com.airsaid.localization.model;

import java.util.Objects;

/**
 * @author airsaid
 */
public abstract class AbstractValue implements Cloneable {

  private final String name;
  private final boolean translatable;

  public AbstractValue(String name) {
    this(name, true);
  }

  public AbstractValue(String name, boolean translatable) {
    this.name = name;
    this.translatable = translatable;
  }

  public String getName() {
    return name;
  }

  public boolean isTranslatable() {
    return translatable;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof AbstractValue)) {
      return false;
    }
    AbstractValue value = (AbstractValue) obj;
    return name.equals(value.name); // name is unique
  }

  @Override
  public AbstractValue clone() {
    try {
      return (AbstractValue) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String toString() {
    return "AbstractValue{" +
        "name='" + name + '\'' +
        ", translatable=" + translatable +
        '}';
  }
}
