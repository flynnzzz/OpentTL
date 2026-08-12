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

public class TierList {

  private String name;
  private final List<Tier> tiered;
  private final Tier unTiered;
  public static final String DEFAULT_TIER_LIST_NAME = "New Tier List";

  private Tier destinationOf(Tier tier) {
    return isUntiered(tier) ? unTiered : tier;
  }

  private boolean isUntiered(Tier tier) {
    return tier.equalsTier(unTiered);
  }

  private void setTierHeader(int tierIndex, TierHeader tierHeader) throws TierNotFoundException {
    Objects.requireNonNull(tierHeader);
    var tier = tiered.get(tierIndex);
    tier.setName(tierHeader.name());
    tier.setColor(tierHeader.color());
  }

  public TierList(String tierListName, List<TierItem> unTiered, List<Tier> tiers) throws IllegalArgumentException {
    this.name = Objects.requireNonNull(tierListName);

    this.unTiered = DefaultTier.__UNTIERED__.value();
    unTiered.forEach(this.unTiered::add);
    this.tiered = Objects.requireNonNull(tiers);
    if (tierListName.isBlank())
      throw new IllegalArgumentException("[ERROR] --- TierList name cannot be blank ---");
  }

  public TierList(String tierListName, List<TierItem> unTiered) throws IllegalArgumentException {
    this(tierListName, unTiered, new ArrayList<>());
  }

  public TierList(List<TierItem> unTiered) {
    this(DEFAULT_TIER_LIST_NAME, unTiered);
  }

  public TierList(String tierListName) throws IllegalArgumentException {
    this(tierListName, new ArrayList<>());
  }

  public TierList() {
    this(new ArrayList<>());
  }

  public static TierList ofDefaultTiers() {
    var tierList = new TierList();

    Arrays.stream(DefaultTier.values())
            .filter( d -> !d.equals(DefaultTier.__UNTIERED__))
            .map(DefaultTier::value)
            .peek(Tier::clear)
            .forEach(tierList::addTier);

    DefaultTier.__UNTIERED__.value().clear();
    return tierList;
  }

  @JsonCreator
  public TierList(
      @JsonProperty("tiers") List<Tier> tiers,
      @JsonProperty("unTiered") List<TierItem> unTiered) {
    this.tiered = tiers;
    this.unTiered = new Tier("__UNTIERED__", "#ffffff", unTiered);
  }

  public void tier(TierItem unTiered, Tier tier) throws TierNotFoundException, TierItemNotFoundException {

    if (unTiered.getStatus() != TieredStatus.UNTIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot tier: " + unTiered + " as it's already tiered ---");
    if (isUntiered(tier))
      return;

    moveItem(unTiered, tier);

  }

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

  public void tierInsert(TierItem unTiered, Tier tier, int index)
      throws TierNotFoundException, TierItemNotFoundException {

    if (!this.contains(tier))
      throw new TierNotFoundException(
          "[ERROR] --- Tier not found: " + tier + " ---");

    try {
      tierInsert(unTiered, tier, tier.getItems().get(index));
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException("[ERROR] --- Index to move to: " + index + " ---");
    }
  }

  public void unTier(TierItem tiered) throws TierNotFoundException, TierItemNotFoundException {

    if (tiered.getStatus() != TieredStatus.TIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot untier: " + tiered + " as it's already untiered ---");

    moveItem(tiered, unTiered);
  }

  public void unTierInsert(TierItem tiered, TierItem position)
      throws TierNotFoundException, TierItemNotFoundException {

    if (tiered.getStatus() != TieredStatus.TIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot untier: " + tiered + " as it's already untiered ---");

    insertItem(tiered, unTiered, position);
  }

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

    if (isUntiered(toTier)) {
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

  public int indexOf(Tier tier) throws TierNotFoundException {
    final var i = tiered.indexOf(tier);
    if (i == -1)
      throw new TierNotFoundException(
          "[ERROR] --- Could not find index of tier: " + tier.toString(TierStringFormat.COMPACT));
    return i;
  }

  public int indexOf(TierItem item) throws TierNotFoundException {

    final Optional<Integer> i = unTiered.contains(item) ? Optional.of(unTiered.indexOf(item))
        : tiered.stream()
            .filter(t -> t.contains(item))
            .map(t -> t.getItems().indexOf(item))
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
                .flatMap(t -> t.getItems().stream()),

            getUnTiered()
                .stream())
        .anyMatch(e -> e.equals(item));

  }

  public boolean contains(Tier tier) {
    return isUntiered(tier) || tiered.contains(tier);
  }

  public void moveTier(Tier from, Tier to) throws TierNotFoundException, UnsupportedOperationException {

    if (isUntiered(from) || isUntiered(to))
      throw new UnsupportedOperationException("[ERROR] --- Cannot move __UNTIERED__ tier ---");

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

    final var updatedStatus = isUntiered(toTier) ? UNTIERED : TIERED;

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
    return List.copyOf(unTiered.getItems());
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
