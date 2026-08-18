package net.flynn.opentierlist.model.models;

import javafx.scene.paint.Color;

public record TierHeader(String name, String color) implements Comparable<TierHeader> {

  public TierHeader {
    if (name.isBlank())
      throw new IllegalArgumentException("[ERROR] --- Tier name cannot be blank ---");
    if (color.isBlank())
      throw new IllegalArgumentException("[ERROR] --- Tier color string cannot be blank ---");
    try {
      Color.valueOf(color);
    } catch (IllegalArgumentException _) {
      throw new IllegalArgumentException("[ERROR] --- Invalid color: " + color + " ---");
    }
  }

  @Override
  public int compareTo(TierHeader o) {
    return name.compareTo(o.name());
  }
}
