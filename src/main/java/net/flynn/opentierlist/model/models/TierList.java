package net.flynn.opentierlist.model.models;

import static net.flynn.opentierlist.model.enums.TieredStatus.TIERED;
import static net.flynn.opentierlist.model.enums.TieredStatus.UNTIERED;

import java.util.*;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.flynn.opentierlist.model.enums.DefaultTier;
import net.flynn.opentierlist.model.enums.TierStringFormat;
import net.flynn.opentierlist.model.enums.TieredStatus;
import net.flynn.opentierlist.model.exceptions.TierItemNotFoundException;
import net.flynn.opentierlist.model.exceptions.TierNotFoundException;

/**
 * A class representing the concept of a tier list
 * 
 * @author flynnz
 * @version 2.50
 * @since v0.0.0
 */
public class TierList {

  private String name;
  private final List<Tier> tiered;
  private final Tier unTiered;
  public static final String DEFAULT_TIER_LIST_NAME = "New Tier List";

  private Tier destinationOf(Tier tier) {
    return isUntiered(tier) ? unTiered : tier;
  }

  private boolean isUntiered(Tier tier) {
    return tier.equalsTier(Tier.UNTIERED);
  }

  private void setTierHeader(int tierIndex, TierHeader tierHeader) throws TierNotFoundException {
    Objects.requireNonNull(tierHeader);
    var tier = tiered.get(tierIndex);
    tier.setName(tierHeader.name());
    tier.setColor(tierHeader.color());
  }

  /**
   * Constructs a {@link TierList} instance
   * <p>
   * The tier list instance will be constructed with the given parameters
   * 
   * @param tierListName the tier list's name
   * @param unTiered     items to rank
   * @param tiers        preset tiers
   * @throws IllegalArgumentException if name is blank
   */
  public TierList(String tierListName, List<TierItem> unTiered, List<Tier> tiers) throws IllegalArgumentException {
    this.name = Objects.requireNonNull(tierListName);
    this.unTiered = new Tier(Tier.UNTIERED.getName(), Tier.UNTIERED.getColor(), unTiered);
    this.tiered = Objects.requireNonNull(tiers);
    if (tierListName.isBlank())
      throw new IllegalArgumentException("[ERROR] --- TierList name cannot be blank ---");
  }

  /**
   * Constructs {@link TierList} instance
   * <p>
   * The tier list instance will be constructed with the given parameters
   * 
   * @param tierListName the tier list's name
   * @param unTiered     items to rank
   * @throws IllegalArgumentException if name is blank
   */
  public TierList(String tierListName, List<TierItem> unTiered) throws IllegalArgumentException {
    this(tierListName, unTiered, new ArrayList<>());
  }

  /**
   * Constructs a {@link TierList} instance
   * <p>
   * The tier list instance will be constructed with the given items to rank
   * 
   * @param unTiered items to rank
   */
  public TierList(List<TierItem> unTiered) {
    this(DEFAULT_TIER_LIST_NAME, unTiered);
  }

  /**
   * Constructs a {@link TierList} instance
   * <p>
   * The tier list instance will be constructed with the given name
   * 
   * @param tierListName name of the tier list
   * @throws IllegalArgumentException if name is blank
   */
  public TierList(String tierListName) throws IllegalArgumentException {
    this(tierListName, new ArrayList<>());
  }

  /**
   * Constructs an empty {@link TierList} instance
   */
  public TierList() {
    this(new ArrayList<>());
  }

  public static TierList ofDefaultTiers() {
    var tierList = new TierList();
    for (var tier : DefaultTier.values())
      tierList.addTier(tier.value());
    return tierList;
  }

  @JsonCreator
  public TierList(
      @JsonProperty("tiers") List<Tier> tiers,
      @JsonProperty("unTiered") List<TierItem> unTiered) {
    this.tiered = tiers;
    this.unTiered = new Tier("__UNTIERED__", "#ffffff", unTiered);
  }

  /**
   * Ranks a {@link TierItem}
   *
   * @param unTiered item to rank
   * @param tier     tier to rank to
   * @throws TierNotFoundException     if tier doesn't exist
   * @throws TierItemNotFoundException if tier item doesn't exist
   * @throws IllegalArgumentException  if the item is already tiered
   */
  public void tier(TierItem unTiered, Tier tier) throws TierNotFoundException, TierItemNotFoundException {

    if (unTiered.getStatus() != TieredStatus.UNTIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot tier: " + unTiered + " as it's already tiered ---");
    if (tier.equalsTier(Tier.UNTIERED))
      return;

    moveItem(unTiered, tier);

  }

  /**
   * Ranks a {@link TierItem} to a specified position
   *
   * @param unTiered item to rank
   * @param tier     tier to rank to
   * @param position destination
   * @throws TierNotFoundException     if tier doesn't exist
   * @throws TierItemNotFoundException if tier item doesn't exist
   * @throws IllegalArgumentException  if the item is already tiered
   */
  public void tierInsert(TierItem unTiered, Tier tier, TierItem position)
      throws TierNotFoundException, TierItemNotFoundException {
    if (!tiered.contains(tier))
      throw new TierNotFoundException(
          "[ERROR] --- Tier not found: " + tier + " ---");

    if (!tier.contains(position))
      throw new TierItemNotFoundException(
          "[ERROR] --- Position to move to: " + position + " doesn't exist ---");

    if (unTiered.getStatus() != TieredStatus.UNTIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot tier: " + unTiered + " as it's already tiered ---");

    insertItem(unTiered, tier, position);
  }

  /**
   * Ranks a {@link TierItem} to a specified position
   *
   * @param unTiered item to rank
   * @param tier     tier to rank to
   * @param index    destination index
   * @throws TierNotFoundException     if tier doesn't exist
   * @throws TierItemNotFoundException if tier item doesn't exist
   * @throws IllegalArgumentException  if the item is already tiered
   */
  public void tierInsert(TierItem unTiered, Tier tier, int index)
      throws TierNotFoundException, TierItemNotFoundException {

    if (!this.contains(tier))
      throw new TierNotFoundException(
          "[ERROR] --- Tier not found: " + tier + " ---");

    try {
      tierInsert(unTiered, tier, tier.getTiered().get(index));
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException("[ERROR] --- Index to move to: " + index + " ---");
    }
  }

  /**
   * Un-ranks a {@link TierItem}
   *
   * @param tiered item to un-rank
   * @throws TierNotFoundException     if tier doesn't exist
   * @throws TierItemNotFoundException if tier item doesn't exist
   * @throws IllegalArgumentException  if the item is already untiered
   */
  public void unTier(TierItem tiered) throws TierNotFoundException, TierItemNotFoundException {

    if (tiered.getStatus() != TieredStatus.TIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot untier: " + tiered + " as it's already untiered ---");

    moveItem(tiered, Tier.UNTIERED);
  }

  /**
   * Un-ranks a {@link TierItem} to a specified position
   *
   * @param tiered   item to rank
   * @param position destination
   * @throws TierNotFoundException     if tier doesn't exist
   * @throws TierItemNotFoundException if tier item doesn't exist
   * @throws IllegalArgumentException  if the item is already untiered
   */
  public void unTierInsert(TierItem tiered, TierItem position)
      throws TierNotFoundException, TierItemNotFoundException {

    if (tiered.getStatus() != TieredStatus.TIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot untier: " + tiered + " as it's already untiered ---");

    insertItem(tiered, Tier.UNTIERED, position);
  }

  /**
   * Un-ranks a {@link TierItem} to a specified position
   *
   * @param tiered item to rank
   * @param index  destination index
   * @throws TierNotFoundException     if tier doesn't exist
   * @throws TierItemNotFoundException if tier item doesn't exist
   * @throws IllegalArgumentException  if the item is already untiered
   */
  public void unTierInsert(TierItem tiered, int index)
      throws TierNotFoundException, TierItemNotFoundException {

    if (index < 0 || index > unTiered.itemCount()) {
      throw new TierItemNotFoundException(
          "[ERROR] --- Destination index out of bounds: " + index + " ---");
    }

    if (index == unTiered.itemCount())
      unTier(tiered);
    else
      unTierInsert(tiered, unTiered.get(index));
  }

  public void addTier(Tier tier) {
    tiered.add(tier);
  }

  public void addItem(TierItem item, Tier toTier) throws TierNotFoundException {

    if (toTier.equalsTier(Tier.UNTIERED)) {
      unTiered.add(item);
      return;
    }

    if (!tiered.contains(toTier))
      throw new TierNotFoundException(
          "[ERROR] --- Could not add " + item + " to tier: " + toTier + " as it doesn't exists ---");

    toTier.add(item);

  }

  public void addItem(TierItem item, Tier toTier, TierItem position)
      throws TierNotFoundException, TierItemNotFoundException {

    if (!destinationOf(toTier).contains(position)) {
      throw new TierItemNotFoundException("[ERROR] --- Position to move to: " + position + " doesn't exist ---");
    }
    addItem(item, toTier);
    final int index = destinationOf(toTier).indexOf(position);
    destinationOf(toTier).move(item, index);

  }

  public void addItem(TierItem item, Tier toTier, int index)
      throws TierNotFoundException, TierItemNotFoundException {

    if (index == destinationOf(toTier).itemCount()) {
      destinationOf(toTier).add(item);
      return;
    }

    try {
      addItem(item, toTier, destinationOf(toTier).get(index));
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException("[ERROR] --- item index is out of bounds: " + index + " ---");
    }

  }

  public void addAllItems(List<TierItem> items, Tier toTier) throws TierNotFoundException {
    items.forEach(e -> addItem(e, toTier));
  }

  public void removeTier(int index) throws TierNotFoundException {
    try {
      tiered.remove(index);

    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException(
          "[ERROR] --- Indexes out of bounds for removal, max = " + tiersQuantity() + " ---");

    } catch (UnsupportedOperationException _) {
      throw new TierNotFoundException("[ERROR] --- Unsupported removal operation ---");
    }
  }

  public void removeTier(Tier tier) throws TierNotFoundException {
    try {
      if (!tiered.remove(tier))
        throw new TierNotFoundException("[ERROR] --- Tier to remove not found: " + tier + " ---");

    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException(
          "[ERROR] --- Indexes out of bounds for removal, max = " + tiersQuantity() + " ---");

    } catch (UnsupportedOperationException _) {
      throw new TierNotFoundException("[ERROR] --- Unsupported removal operation ---");
    }
  }

  public void removeItem(TierItem item) throws TierItemNotFoundException {

    if (unTiered.contains(item)) {
      unTiered.remove(item);
      return;
    }

    var potentialTier = tiered.stream()
        .filter(t -> t.contains(item))
        .findFirst();

    potentialTier.ifPresentOrElse(
        tier -> tier.remove(item),
        () -> {
          throw new TierItemNotFoundException("[ERROR] --- No item: " + item + " to remove ---");
        });
  }

  public void removeAllItems(Set<TierItem> items) {
    items.forEach(this::removeItem);
  }

  public void swapTiers(int src, int dest) throws TierNotFoundException {
    try {
      Collections.swap(tiered, src, dest);
    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException(
          "[ERROR] --- Indexes out of bounds for swapping, max = " + tiersQuantity() + " ---");
    }
  }

  public void swapTiers(Tier src, Tier dest) throws TierNotFoundException {
    try {
      swapTiers(indexOf(src), indexOf(dest));
    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException(
          "[ERROR] --- Indexes out of bounds for swapping, max = " + tiersQuantity() + " ---");
    }
  }

  /**
   * Returns the index of a tier
   * 
   * @param tier to search the index for
   * @return the tier's index
   * @throws TierNotFoundException if tier doesn't exist
   */
  public int indexOf(Tier tier) throws TierNotFoundException {
    final var i = tiered.indexOf(tier);
    if (i == -1)
      throw new TierNotFoundException(
          "[ERROR] --- Could not find index of tier: " + tier.toString(TierStringFormat.COMPACT));
    return i;
  }

  /**
   * Returns the index of a tier
   * 
   * @param item to search the index for
   * @return the item's index within it's tier
   * @throws TierNotFoundException if tier doesn't exist
   */
  public int indexOf(TierItem item) throws TierNotFoundException {

    final Optional<Integer> i = unTiered.contains(item) ? Optional.of(unTiered.indexOf(item))
        : tiered.stream()
            .filter(t -> t.contains(item))
            .map(t -> t.getTiered().indexOf(item))
            .findFirst();

    if (i.isEmpty() || i.get() == -1)
      throw new TierNotFoundException(
          "[ERROR] --- Could not find index of tier: " + item.toString(TierStringFormat.COMPACT));
    return i.get();
  }

  public int tiersQuantity() {
    return tiered.size();
  }

  public boolean contains(TierItem item) {

    return Stream
        .concat(
            getTiered()
                .stream()
                .flatMap(t -> t.getTiered().stream()),

            getUnTiered()
                .stream())
        .anyMatch(e -> e.equals(item));

  }

  public boolean contains(Tier tier) {
    return tier.equalsTier(Tier.UNTIERED) || tiered.contains(tier);
  }

  public void moveTier(Tier from, Tier to) throws TierNotFoundException, UnsupportedOperationException {

    if (from.equalsTier(Tier.UNTIERED) || to.equalsTier(Tier.UNTIERED))
      throw new UnsupportedOperationException("[ERROR] --- Cannot move UNTIERED tier ---");

    int fromIndex, toIndex;
    if ((fromIndex = tiered.indexOf(from)) == -1)
      throw new TierNotFoundException("[ERROR] --- Index out of bounds 'from': " + fromIndex + " ---");
    if ((toIndex = tiered.indexOf(to)) == -1)
      throw new TierNotFoundException("[ERROR] --- Index out of bounds 'to': " + toIndex + " ---");

    tiered.remove(fromIndex);
    tiered.add(toIndex, from);
  }

  public void moveTier(Tier from, int toIndex) throws TierNotFoundException {

    int fromIndex;
    if ((fromIndex = tiered.indexOf(from)) == -1)
      throw new TierNotFoundException("[ERROR] --- Index out of bounds 'from': " + fromIndex + " ---");

    tiered.remove(fromIndex);
    tiered.add(toIndex, from);
  }

  public void moveItem(TierItem item, Tier toTier) {

    final var destination = destinationOf(toTier);

    if (!this.contains(item))
      throw new TierItemNotFoundException("[ERROR] --- Item to move: " + item + " not found ---");

    if (!this.contains(destination))
      throw new TierNotFoundException("[ERROR] --- Tier to move to: " + toTier + " not found ---");

    removeItem(item);
    addItem(item, destination);

    final var updatedStatus = toTier.equalsTier(Tier.UNTIERED) ? UNTIERED : TIERED;
    item.changeTo(updatedStatus);
  }

  public void insertItem(TierItem item, Tier toTier, TierItem position) {
    if (!this.contains(position))
      throw new TierItemNotFoundException("[ERROR] --- Position to move to: " + position + " not found ---");

    final var index = destinationOf(toTier).indexOf(position);
    moveItem(item, toTier);
    destinationOf(toTier).move(item, index);
  }

  public void insertItem(TierItem item, Tier toTier, int index) {

    final var destination = destinationOf(toTier);

    if (index < 0 || destination.itemCount() < index)
      throw new TierItemNotFoundException("[ERROR] --- Index to move to: " + index + " not found ---");

    if (index == destination.itemCount()) {
      moveItem(item, destination);
      return;
    }

    final TierItem position = destination.get(index);

    insertItem(item, destination, position);
  }

  public void setName(String name) throws IllegalArgumentException {
    Objects.requireNonNull(name);
    if (name.isBlank())
      throw new IllegalArgumentException("[ERROR] --- The tier list's name cannot be blank ---");
    this.name = name;
  }

  public void setTierName(int tierIndex, String name) {
    var oldColor = tiered.get(tierIndex).getColor();
    setTierHeader(tierIndex, new TierHeader(name, oldColor));
  }

  public void setTierColor(int tierIndex, String color) {
    var oldName = tiered.get(tierIndex).getName();
    setTierHeader(tierIndex, new TierHeader(oldName, color));
  }

  public String getName() {
    return name;
  }

  public List<TierItem> getUnTiered() {
    return List.copyOf(unTiered.getTiered());
  }

  public List<Tier> getTiered() {
    return List.copyOf(tiered);
  }

  public Tier getTier(int index) {
    return tiered.get(index);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tiered, name, unTiered);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TierList other)) {
      return false;
    }
    return Objects.equals(tiered, other.tiered) && Objects.equals(name, other.name)
        && Objects.equals(unTiered, other.unTiered);
  }

  @Override
  public String toString() {
    return this.toString(TierStringFormat.COMPACT);
  }

  public String toString(TierStringFormat format) {
    var sb = new StringBuilder();
    sb.append(this.name).append(System.lineSeparator());
    sb.append(System.lineSeparator());

    tiered.stream()
        .map(tier -> tier.toString(format))
        .forEach(tierString -> {
          sb.append(tierString);
          sb.append(System.lineSeparator());
          sb.append(System.lineSeparator());
        });

    sb.append("Untiered: ").append(System.lineSeparator()).append(unTiered.toString());
    return sb.toString();
  }

}
