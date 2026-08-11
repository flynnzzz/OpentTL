package net.flynn.opentierlist.model.models;

import net.flynn.opentierlist.model.enums.TierStringFormat;
import net.flynn.opentierlist.model.exceptions.TierItemNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.scene.paint.Color;

/**
 * Class representing the concept of a 'Tier'
 * 
 * @author flynnz
 * @version 2.25
 * @since v0.0.0
 */
public class Tier {

  public static final String DEFAULT_TIER_NAME = "New Tier";
  public static final String DEFAULT_TIER_COLOR = Color.GRAY.toString();
  public static final Tier UNTIERED = new Tier("__UNTIERED__", "#ffffff");

  private TierHeader header;
  protected final List<TierItem> tiered;

  private static long NEXT_ID = 1;
  private final long id;

  private void setHeader(TierHeader header) {
    this.header = Objects.requireNonNull(header);
  }

  private TierHeader getHeader() {
    return new TierHeader(header.name(), header.color());
  }

  private Tier(TierHeader header, List<TierItem> tiered) {

    Objects.requireNonNull(header);
    if (header.name().isBlank())
      throw new IllegalArgumentException("[ERROR] --- Tier name cannot be null ---");

    this.header = header;
    this.tiered = Objects.requireNonNull(tiered);
    this.id = NEXT_ID++;
  }

  /**
   * Constructs a new {@link Tier} object with the given parameters.
   * 
   * @param name   tier name
   * @param color  tier {@link Color}
   * @param tiered list to associate to this tier
   * @throws IllegalArgumentException if the header's name is blank
   */
  public Tier(String name, String color, List<TierItem> tiered) {
    this(new TierHeader(name, color), tiered);
  }

  /**
   * Constructs a new empty {@link Tier} object with the given parameters.
   * 
   * @param name  tier name
   * @param color tier {@link Color}
   * @throws IllegalArgumentException if name is blank
   */
  public Tier(String name, String color) {
    this(new TierHeader(name, color), new ArrayList<>());
  }

  /**
   * Constructs a new empty {@link Tier} object with given name.
   * 
   * @param name tier name
   * @throws IllegalArgumentException if name is blank
   */
  public Tier(String name) {
    this(new TierHeader(name, DEFAULT_TIER_COLOR), new ArrayList<>());
  }

  /**
   * Constructs a new empty {@link Tier} object
   */
  public Tier() {
    this(DEFAULT_TIER_NAME);
  }

  @JsonCreator
  public Tier(
      @JsonProperty("name") String name,
      @JsonProperty("color") String color,
      @JsonProperty("id") long id,
      @JsonProperty("tiered") List<TierItem> tiered) {
    setHeader(new TierHeader(name, color));
    this.id = id;
    this.tiered = tiered;
  }


  public void clear() {
    tiered.clear();
  }

  /**
   * Adds an item to the tier instance
   *
   * @param item item to add
   * @return true if successful
   */
  public boolean add(TierItem item) {
    return tiered.add(item);
  }

  public boolean remove(TierItem item) throws TierItemNotFoundException {
    if (!tiered.remove(item))
      throw new TierItemNotFoundException(
          "[ERROR] --- Removal of item: " + item + " was not successful ---");
    else
      return true;
  }

  public TierItem remove(int i) throws TierItemNotFoundException {
    try {
      return tiered.remove(i);
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException(
          "[ERROR] --- Removal of item at index: " + i + " was not successful ---");
    }
  }

  public void swap(TierItem src, TierItem dest) throws TierItemNotFoundException {
    try {
      swap(tiered.indexOf(src), tiered.indexOf(dest));
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException("[ERROR] --- Cannot swap items: " + src + ", " + dest + " ---");
    }

  }

  public void swap(int src, int dest) throws TierItemNotFoundException {
    try {
      Collections.swap(tiered, src, dest);
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException(
          "[ERROR] ---  Cannot swap items: " + src + ", " + dest + " ---");
    }
  }

  public boolean contains(TierItem item) {
    return tiered.contains(item);
  }

  public int itemCount() {
    return tiered.size();
  }

  /**
   * Moves an item to a certain position, automatically shifting all the others
   * 
   * @param src  item to move
   * @param dest destination
   * @throws TierItemNotFoundException if item is not found
   */
  public void move(TierItem src, TierItem dest) throws TierItemNotFoundException {
    if (!tiered.contains(src))
      throw new TierItemNotFoundException("[ERROR] --- Item to move not found: " + src + " ---");
    if (!tiered.contains(dest))
      throw new TierItemNotFoundException("[ERROR] --- Item to move not found: " + src + " ---");
    final var destIndex = indexOf(dest);
    tiered.remove(src);
    tiered.add(destIndex, src);
  }

  /**
   * Moves an item to a certain index, automatically shifting all the others
   * 
   * @param item    item to move
   * @param toIndex destination index
   * @throws TierItemNotFoundException if item is not found
   */
  public void move(TierItem item, int toIndex) throws TierItemNotFoundException {
    if (!this.contains(item))
      throw new TierItemNotFoundException("[ERROR] --- Item to move not found: " + item + " ---");

    if (toIndex > tiered.size())
      throw new TierItemNotFoundException("[ERROR] --- Index to move to is out of bounds: " + toIndex + " ---");

    tiered.remove(item);
    tiered.add(toIndex, item);
  }

  public Tier copy() {
    return new Tier(header.name(), header.color(), tiered);
  }

  public Tier emptyCopy() {
    return new Tier(header.name(), header.color());
  }

  public int indexOf(TierItem item) {
    return tiered.indexOf(item);
  }

  public void setName(String name) throws IllegalArgumentException {
    Objects.requireNonNull(name);
    if (name.isBlank())
      throw new IllegalArgumentException("[ERROR] --- Tier name cannot be set to null ---");
    setHeader(new TierHeader(name, this.header.color()));
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

  /**
   * *Read only*
   * 
   * @return this tier instance's items
   */
  public List<TierItem> getTiered() {
    return List.copyOf(tiered);
  }

  public TierItem get(int i) {
    return tiered.get(i);
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

  /**
   * Equals but ignoring instance difference
   *
   * @param tier tier to compare to
   * @return true if names and colors match
   */
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

  /**
   * Returns the {@link Tier} as {@link String} with the specified
   * {@link TierStringFormat}
   * 
   * Format {@link TierStringFormat#EXTENDED}:
   * "header name:
   * \[
   * item1,
   * item2,
   * ...
   * \]
   * "
   */
  public String toString(TierStringFormat format) {
    return switch (format) {
      case EXTENDED -> toStringExtended();
      case COMPACT -> toStringCompact();
    };
  }

  private String toStringCompact() {
    return getHeader().name() + ": " + toStringItems(getTiered());
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
    for (TierItem e : tiered) {
      sb.append("\t");
      sb.append(e);
      if (!tiered.getLast().equals(e))
        sb.append(",");
      else
        sb.append(".");
      sb.append(System.lineSeparator());
    }
    sb.append("]");
    return sb.toString();
  }
}
