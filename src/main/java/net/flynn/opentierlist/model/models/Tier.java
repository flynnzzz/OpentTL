package net.flynn.opentierlist.model.models;

import net.flynn.opentierlist.model.enums.TierStringFormat;
import net.flynn.opentierlist.model.exceptions.TierItemNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.flynn.opentierlist.ConfigHolder;

public class Tier {
  private TierHeader header;
  protected final List<TierItem> items;

  private static long NEXT_ID = 1;
  private final long id;

  public static final String DEFAULT_TIER_NAME = "New Tier";
  public static final String DEFAULT_TIER_COLOR = ConfigHolder.DEFAULT_NEW_TIER_COLOR;

  private void setHeader(TierHeader header) {
    this.header = Objects.requireNonNull(header);
  }

  private TierHeader getHeader() {
    return new TierHeader(header.name(), header.color());
  }

  private Tier(TierHeader header, List<TierItem> items) {

    Objects.requireNonNull(header);
    if (header.name().isBlank())
      throw new IllegalArgumentException("[ERROR] --- Tier name cannot be null ---");

    this.header = header;
    this.items = Objects.requireNonNull(items);
    this.id = NEXT_ID++;
  }

  public Tier(String name, String color, List<TierItem> items) {
    this(new TierHeader(name, color), items);
  }

  public Tier(String name, String color) {
    this(new TierHeader(name, color), new ArrayList<>());
  }

  public Tier(String name) {
    this(new TierHeader(name, DEFAULT_TIER_COLOR), new ArrayList<>());
  }

  public Tier() {
    this(DEFAULT_TIER_NAME);
  }

  @JsonCreator
  public Tier(
      @JsonProperty("name") String name,
      @JsonProperty("color") String color,
      @JsonProperty("id") long id,
      @JsonProperty("tiered") List<TierItem> items) {
    setHeader(new TierHeader(name, color));
    this.id = id;
    this.items = items;
  }

  public void clear() {
    items.clear();
  }

  public boolean add(TierItem item) {
    return items.add(Objects.requireNonNull(item));
  }

  public boolean remove(TierItem item) throws TierItemNotFoundException {
    if (!items.remove(Objects.requireNonNull(item)))
      throw new TierItemNotFoundException(
          "[ERROR] --- Removal of item: " + item + " was not successful ---");
    return true;
  }

  public TierItem remove(int i) throws TierItemNotFoundException {
    try {
      return items.remove(i);
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException(
          "[ERROR] --- Removal of item at index: " + i + " was not successful ---");
    }
  }

  public void swap(TierItem src, TierItem dest) throws TierItemNotFoundException {
    try {
      swap(items.indexOf(src), items.indexOf(dest));
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException("[ERROR] --- Cannot swap items: " + src + ", " + dest + " ---");
    }

  }

  public void swap(int src, int dest) throws TierItemNotFoundException {
    try {
      Collections.swap(items, src, dest);
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException(
          "[ERROR] ---  Cannot swap items: " + src + ", " + dest + " ---");
    }
  }

  public boolean contains(TierItem item) {
    return items.contains(Objects.requireNonNull(item));
  }

  public int itemCount() {
    return items.size();
  }

  public void move(TierItem src, TierItem dest) throws TierItemNotFoundException {
    if (!items.contains(src))
      throw new TierItemNotFoundException("[ERROR] --- Item to move not found: " + src + " ---");
    if (!items.contains(dest))
      throw new TierItemNotFoundException("[ERROR] --- Item to move not found: " + src + " ---");
    final var destIndex = indexOf(dest);
    items.remove(src);
    items.add(destIndex, src);
  }

  public void move(TierItem item, int tierIndex) throws TierItemNotFoundException {
    if (!contains(item))
      throw new TierItemNotFoundException("[ERROR] --- Item to move not found: " + item + " ---");

    if (tierIndex > items.size())
      throw new TierItemNotFoundException("[ERROR] --- Index to move to is out of bounds: " + tierIndex + " ---");

    items.remove(item);
    items.add(tierIndex, item);
  }

  public Tier copy() {
    return new Tier(header.name(), header.color(), items);
  }

  public Tier emptyCopy() {
    return new Tier(header.name(), header.color());
  }

  public int indexOf(TierItem item) {
    return items.indexOf(Objects.requireNonNull(item));
  }

  public void setName(String name) throws IllegalArgumentException {
    Objects.requireNonNull(name);
    if (name.isBlank())
      throw new IllegalArgumentException("[ERROR] --- Tier name cannot be set to null ---");
    setHeader(new TierHeader(name, header.color()));
  }

  public void setColor(String color) throws IllegalArgumentException {
    Objects.requireNonNull(color);
    if (color.isBlank())
      throw new IllegalArgumentException("[ERROR] --- Color string must not me blank ---");
    setHeader(new TierHeader(getName(), color));
  }

  public String getName() {
    return getHeader().name();
  }

  public String getColor() {
    return getHeader().color();
  }

  public List<TierItem> getItems() {
    return List.copyOf(items);
  }

  public TierItem get(int i) {
    return items.get(i);
  }

  @Override
  public int hashCode() {
    return Objects.hash(header, id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Tier other)) {
      return false;
    }
    return Objects.equals(header, other.header)
        && Objects.equals(id, other.id);
  }

  public boolean equalsTier(Tier tier) {
    if (this == tier) {
      return true;
    }
    if (!(tier instanceof Tier other)) {
      return false;
    }
    return Objects.equals(header, other.header);
  }

  @Override
  public String toString() {
    return toStringCompact();
  }

  public String toString(TierStringFormat format) {
    return switch (format) {
      case EXTENDED -> toStringExtended();
      case COMPACT -> toStringCompact();
    };
  }

  private String toStringCompact() {
    return getHeader().name() + ": " + toStringItems(getItems());
  }

  private String toStringItems(List<TierItem> items) {
    var sb = new StringBuilder();
    sb.append("[ ");
    for (TierItem e : items) {
      sb.append(e);
      if (!items.getLast().equals(e))
        sb.append(", ");
      else
        sb.append(".");
    }
    sb.append(" ]");
    return sb.toString();
  }

  private String toStringExtended() {
    var sb = new StringBuilder();
    sb.append(getHeader().name()).append(":").append(System.lineSeparator());
    sb.append("[");
    sb.append(System.lineSeparator());
    for (TierItem e : items) {
      sb.append("\t");
      sb.append(e);
      if (!items.getLast().equals(e))
        sb.append(",");
      else
        sb.append(".");
      sb.append(System.lineSeparator());
    }
    sb.append("]");
    return sb.toString();
  }
}
