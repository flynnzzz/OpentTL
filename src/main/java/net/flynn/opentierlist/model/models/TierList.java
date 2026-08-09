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

  private String tierListName;
  private final List<Tier> tiers;
  private final Tier unTiered;
  public static final String DEFAULT_TIER_LIST_NAME = "New Tier List";

  /**
   * Constructs a {@link TierList} instance
   * <p>
   * The tier list instance will be constructed with the given parameters
   * 
   * @param tierListName the tier list's name
   * @param unTiered     elements to rank
   * @param tiers        preset tiers
   * @throws IllegalArgumentException if name is blank
   */
  public TierList(String tierListName, List<TierItem> unTiered, List<Tier> tiers) throws IllegalArgumentException {
    this.tierListName = Objects.requireNonNull(tierListName);
    this.unTiered = Tier.UNTIERED;
    this.tiers = Objects.requireNonNull(tiers);
    if (tierListName.isBlank())
      throw new IllegalArgumentException("[ERROR] --- TierList name cannot be blank ---");
  }

  /**
   * Constructs {@link TierList} instance
   * <p>
   * The tier list instance will be constructed with the given parameters
   * 
   * @param tierListName the tier list's name
   * @param unTiered     elements to rank
   * @throws IllegalArgumentException if name is blank
   */
  public TierList(String tierListName, List<TierItem> unTiered) throws IllegalArgumentException {
    this(tierListName, unTiered, new ArrayList<>());
  }

  /**
   * Constructs a {@link TierList} instance
   * <p>
   * The tier list instance will be constructed with the given elements to rank
   * 
   * @param unTiered elements to rank
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
    this.tiers = tiers;
    this.unTiered = new Tier("__UNTIERED__", "#ffffff", unTiered);
  }

  /**
   * Ranks a {@link TierItem}
   *
   * @param unTiered element to rank
   * @param tier     tier to rank to
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   * @throws IllegalArgumentException     if the element is already tiered
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
   * @param unTiered element to rank
   * @param tier     tier to rank to
   * @param position destination
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   * @throws IllegalArgumentException     if the element is already tiered
   */
  public void tierInsert(TierItem unTiered, Tier tier, TierItem position)
      throws TierNotFoundException, TierItemNotFoundException {
    if (!tiers.contains(tier))
      throw new TierNotFoundException(
              "[ERROR] --- Tier not found: " + tier + " ---"
      );

    if (!tier.contains(position))
      throw new TierItemNotFoundException(
              "[ERROR] --- Position to move to: " + position + " doesn't exist ---"
      );

    if (unTiered.getStatus() != TieredStatus.UNTIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot tier: " + unTiered + " as it's already tiered ---");

    insertItem(unTiered, tier, position);
  }

  /**
   * Ranks a {@link TierItem}
   *
   * @param unTiered  element to rank
   * @param tierIndex tier to rank to
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   * @throws IllegalArgumentException     if the element is already tiered
   */
  @Deprecated
  public void tier(TierItem unTiered, int tierIndex) throws TierNotFoundException, TierItemNotFoundException {

    if (unTiered.getStatus() != TieredStatus.UNTIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot tier: " + unTiered + " as it's already tiered ---");

    moveItem(unTiered, tiers.get(tierIndex));
  }

  /**
   * Ranks a {@link TierItem} to a specified position
   *
   * @param unTiered element to rank
   * @param tier     tier to rank to
   * @param index    destination index
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   * @throws IllegalArgumentException     if the element is already tiered
   */
  public void tierInsert(TierItem unTiered, Tier tier, int index)
      throws TierNotFoundException, TierItemNotFoundException {

    if (!this.contains(tier))
      throw new TierNotFoundException(
              "[ERROR] --- Tier not found: " + tier + " ---"
      );

    try {
      tierInsert(unTiered, tier, tier.getTiered().get(index));
    }
    catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException("[ERROR] --- Index to move to: " + index + " ---");
    }
  }

  /**
   * Un-ranks a {@link TierItem}
   *
   * @param tiered element to un-rank
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   * @throws IllegalArgumentException     if the element is already untiered
   */
  public void unTier(TierItem tiered) throws TierNotFoundException, TierItemNotFoundException {

    if (tiered.getStatus() != TieredStatus.TIERED)
      throw new IllegalArgumentException("[ERROR] --- Cannot untier: " + tiered + " as it's already untiered ---");

    moveItem(tiered, Tier.UNTIERED);
  }

  /**
   * Un-ranks a {@link TierItem} to a specified position
   *
   * @param tiered   element to rank
   * @param position destination
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   * @throws IllegalArgumentException     if the element is already untiered
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
   * @param tiered element to rank
   * @param index  destination index
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   * @throws IllegalArgumentException     if the element is already untiered
   */
  public void unTierInsert(TierItem tiered, int index)
      throws TierNotFoundException, TierItemNotFoundException {

    if (index < 0 || index > unTiered.itemCount()) {
      throw new TierItemNotFoundException(
              "[ERROR] --- Destination index out of bounds: " + index + " ---"
      );
    }

    if (index == unTiered.itemCount())
      unTier(tiered);
    else
      unTierInsert(tiered, unTiered.get(index));
  }

  public void addTier(Tier tier) {
    tiers.add(tier);
  }

  public void addItem(TierItem element, Tier toTier) throws TierNotFoundException {

    if (toTier.equalsTier(Tier.UNTIERED)) {
      unTiered.add(element);
      return;
    }

    if (!tiers.contains(toTier))
      throw new TierNotFoundException(
          "[ERROR] --- Could not add " + element + " to tier: " + toTier + " as it doesn't exists ---"
      );

    toTier.add(element);

  }

  public void addItem(TierItem element, Tier toTier, TierItem position) throws TierNotFoundException, TierItemNotFoundException {

    final var destination = toTier.equalsTier(Tier.UNTIERED) ? unTiered : toTier;
    final int index = destination.indexOf(position);

    if (!destination.contains(position)) {
      throw new TierItemNotFoundException("[ERROR] --- Position to move to: " + position + " doesn't exist ---");
    }

    addItem(element, toTier);
    destination.move(element, index);

  }

  public void addItem(TierItem element, Tier toTier, int index) throws TierNotFoundException, TierItemNotFoundException {

    final var destination = toTier.equalsTier(Tier.UNTIERED) ? unTiered : toTier;

    if (index == destination.itemCount()) {
      destination.add(element);
      return;
    }

    try {
      addItem(element, toTier, destination.get(index));
    } catch (IndexOutOfBoundsException _) {
      throw new TierItemNotFoundException("[ERROR] --- Element index is out of bounds: " + index + " ---");
    }

  }

  public void addAllItems(List<TierItem> items, Tier toTier) throws TierNotFoundException {
    items.forEach(e -> addItem(e, toTier));
  }

  public void removeTier(int index) throws TierNotFoundException {
    try {
      tiers.remove(index);

    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException("[ERROR] --- Indexes out of bounds for removal, max = " + tiersQuantity() + " ---");

    } catch (UnsupportedOperationException _) {
      throw new TierNotFoundException("[ERROR] --- Unsupported removal operation ---");
    }
  }

  public void removeTier(Tier tier) throws TierNotFoundException {
    try {
      if (!tiers.remove(tier))
        throw new TierNotFoundException("[ERROR] --- Tier to remove not found: " + tier + " ---");

    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException("[ERROR] --- Indexes out of bounds for removal, max = " + tiersQuantity() + " ---");

    } catch (UnsupportedOperationException _) {
      throw new TierNotFoundException("[ERROR] --- Unsupported removal operation ---");
    }
  }

  public void removeItem(TierItem item) throws TierItemNotFoundException {

    if (unTiered.contains(item)) {
      unTiered.remove(item);
      return;
    }

    var potentialTier = tiers.stream()
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
      Collections.swap(tiers, src, dest);
    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException("[ERROR] --- Indexes out of bounds for swapping, max = " + tiersQuantity() + " ---");
    }
  }

  public void swapTiers(Tier src, Tier dest) throws TierNotFoundException {
    try {
      swapTiers(indexOf(src), indexOf(dest));
    } catch (IndexOutOfBoundsException _) {
      throw new TierNotFoundException("[ERROR] --- Indexes out of bounds for swapping, max = " + tiersQuantity() + " ---");
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
    final var i = tiers.indexOf(tier);
    if (i == -1)
      throw new TierNotFoundException("[ERROR] --- Could not find index of tier: " + tier.toString(TierStringFormat.COMPACT));
    return i;
  }

  /**
   * Returns the index of a tier
   * 
   * @param element to search the index for
   * @return the element's index within it's tier
   * @throws TierNotFoundException if tier doesn't exist
   */
  public int indexOf(TierItem element) throws TierNotFoundException {

    final Optional<Integer> i = unTiered.contains(element) ?
            Optional.of(unTiered.indexOf(element)) :
            tiers.stream()
                    .filter(t -> t.contains(element))
                    .map(t -> t.getTiered().indexOf(element))
                    .findFirst();

    if (i.isEmpty() || i.get() == -1)
      throw new TierNotFoundException(
          "[ERROR] --- Could not find index of tier: " + element.toString(TierStringFormat.COMPACT));
    return i.get();
  }

  private Tier tierByItem(TierItem item) throws TierItemNotFoundException {

    if (unTiered.contains(item))
      return Tier.UNTIERED;

    var matching = tiers.stream()
        .filter(tier -> tier.contains(item))
        .findFirst();

    if (matching.isEmpty())
      throw new TierItemNotFoundException("[ERROR] --- No tier contains item: " + item + " ---");

    return matching.get();
  }

  public int tiersQuantity() {
    return tiers.size();
  }

  public boolean contains(TierItem element) {

    return Stream
        .concat(
            getTiers()
                .stream()
                .flatMap(t -> t.getTiered().stream()),

            getUnTiered()
                .stream())
        .anyMatch(e -> e.equals(element));

  }

  public boolean contains(Tier tier) {
    return tier.equalsTier(Tier.UNTIERED) || tiers.contains(tier);
  }

  public void moveTier(Tier from, Tier to) throws TierNotFoundException, UnsupportedOperationException {

    if (from.equalsTier(Tier.UNTIERED) || to.equalsTier(Tier.UNTIERED))
      throw new UnsupportedOperationException("[ERROR] --- Cannot move UNTIERED tier ---");

    int fromIndex, toIndex;
    if ((fromIndex = tiers.indexOf(from)) == -1)
      throw new TierNotFoundException("[ERROR] --- Index out of bounds 'from': " + fromIndex + " ---");
    if ((toIndex = tiers.indexOf(to)) == -1)
      throw new TierNotFoundException("[ERROR] --- Index out of bounds 'to': " + toIndex + " ---");

    tiers.remove(fromIndex);
    tiers.add(toIndex, from);
  }

  public void moveTier(Tier from, int toIndex) throws TierNotFoundException {

    int fromIndex;
    if ((fromIndex = tiers.indexOf(from)) == -1)
      throw new TierNotFoundException("[ERROR] --- Index out of bounds 'from': " + fromIndex + " ---");

    tiers.remove(fromIndex);
    tiers.add(toIndex, from);
  }

  public void moveItem(TierItem element, Tier toTier) {

    final var destination = toTier.equalsTier(Tier.UNTIERED) ? unTiered : toTier;

    if (!this.contains(element))
      throw new TierItemNotFoundException("[ERROR] --- Element to move: " + element + " not found ---");

    if (!this.contains(destination))
      throw new TierNotFoundException("[ERROR] --- Tier to move to: " + toTier + " not found ---");

    removeItem(element);
    addItem(element, destination);

    final var updatedStatus = toTier.equalsTier(Tier.UNTIERED) ? UNTIERED : TIERED;
    element.changeTo(updatedStatus);
  }

  public void insertItem(TierItem item, Tier toTier, TierItem position) {
    if (!this.contains(position))
      throw new TierItemNotFoundException("[ERROR] --- Position to move to: " + position + " not found ---");

    final var destination = toTier.equalsTier(Tier.UNTIERED) ? unTiered : toTier;

    final int index = destination.indexOf(position);

    moveItem(item, toTier);

    destination.move(item, index);
  }

  public void insertItem(TierItem item, Tier toTier, int index) {

    final var destination = toTier.equalsTier(Tier.UNTIERED) ? unTiered : toTier;

    if (index < 0 || destination.itemCount() < index)
      throw new TierItemNotFoundException("[ERROR] --- Index to move to: " + index + " not found ---");

    if (index == destination.itemCount()) {
      moveItem(item, destination);
      return;
    }

    final TierItem position = destination.get(index);

    insertItem(item, destination, position);
  }

  public void setTierListName(String name) throws IllegalArgumentException {
    Objects.requireNonNull(name);
    if (name.isBlank())
      throw new IllegalArgumentException("[ERROR] --- The tier list's name cannot be blank ---");
    this.tierListName = name;
  }

  public void setTierName(int tierIndex, String name) {
    var oldColor = tiers.get(tierIndex).getColor();
    setTierHeader(tierIndex, new TierHeader(name, oldColor));
  }

  public void setTierColor(int tierIndex, String color) {
    var oldName = tiers.get(tierIndex).getName();
    setTierHeader(tierIndex, new TierHeader(oldName, color));
  }

  private void setTierHeader(int tierIndex, TierHeader tierHeader) throws TierNotFoundException {
    Objects.requireNonNull(tierHeader);
    var tier = tiers.get(tierIndex);
    tier.setName(tierHeader.name());
    tier.setColor(tierHeader.color());
  }

  public String getTierListName() {
    return tierListName;
  }

  public List<TierItem> getUnTiered() {
    return List.copyOf(unTiered.getTiered());
  }

  public List<Tier> getTiers() {
    return List.copyOf(tiers);
  }

  public Tier getTier(int index) {
    return tiers.get(index);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tiers, tierListName, unTiered);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TierList other)) {
      return false;
    }
    return Objects.equals(tiers, other.tiers) && Objects.equals(tierListName, other.tierListName)
        && Objects.equals(unTiered, other.unTiered);
  }

  @Override
  public String toString() {
    return this.toString(TierStringFormat.COMPACT);
  }

  public String toString(TierStringFormat format) {
    var sb = new StringBuilder();
    sb.append(this.tierListName).append(System.lineSeparator());
    sb.append(System.lineSeparator());

    tiers.stream()
        .map(tier -> tier.toString(format))
        .forEach(tierString -> {
          sb.append(tierString);
          sb.append(System.lineSeparator());
          sb.append(System.lineSeparator());
        });

    sb.append("Untiered: ").append(System.lineSeparator()).append(unTiered.toString());
    return sb.toString();
  }

  @Deprecated
  public void removeUnTiered(TierItem element) throws TierItemNotFoundException {
    verifyElementExistence(element, unTiered.getTiered());
    unTiered.remove(element);
    element.changeTo(TIERED);
  }

  @Deprecated
  public void removeFromTier(int tierIndex, TierItem element)
      throws TierNotFoundException, TierItemNotFoundException {
    verifyElementExistenceInTier(element, tierIndex);
    if (!tiers.get(tierIndex).remove(element))
      throw new TierItemNotFoundException();
  }

  @Deprecated
  public void removeFromTier(Tier tier, TierItem element)
      throws TierNotFoundException, TierItemNotFoundException {
    verifyElementExistenceInTier(element, tier);
    if (!tier.remove(element))
      throw new TierItemNotFoundException();
  }

  /**
   * Swaps two elements from within a tier
   * 
   * @param tierIndex destination index
   * @param a         first element
   * @param b         second element
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   */
  @Deprecated
  public void swapTiered(int tierIndex, TierItem a, TierItem b)
      throws TierNotFoundException, TierItemNotFoundException {

    verifyElementExistenceInTier(a, tierIndex);
    verifyElementExistenceInTier(b, tierIndex);

    var tier = tiers.get(tierIndex);
    tier.swap(a, b);
  }

  /**
   * Swaps two elements from within a tier
   * 
   * @param tier destination
   * @param a    first element
   * @param b    second element
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   */
  @Deprecated
  public void swapTiered(Tier tier, TierItem a, TierItem b)
      throws TierNotFoundException, TierItemNotFoundException {

    verifyElementExistenceInTier(a, tier);
    verifyElementExistenceInTier(b, tier);

    tier.swap(a, b);
  }

  /**
   * Swaps two elements from the unTiered elements list
   * 
   * @param a first element
   * @param b second element
   * @throws TierItemNotFoundException if tier element doesn't exist
   */
  @Deprecated
  public void swapUnTiered(TierItem a, TierItem b) throws TierItemNotFoundException {

    int index1 = verifyElementExistence(a, unTiered.getTiered()),
        index2 = verifyElementExistence(b, unTiered.getTiered());

    Collections.swap(unTiered.getTiered(), index1, index2);
  }

  @Deprecated
  public void addUnTiered(TierItem element) {
    unTiered.add(element);
    element.changeTo(UNTIERED);
  }

  @Deprecated
  public void addToTier(int tierIndex, TierItem element) throws TierNotFoundException {
    verifyTierExistence(tierIndex);
    element.changeTo(TIERED);
    tiers.get(tierIndex).add(element);
  }

  @Deprecated
  public void addToTier(Tier tier, TierItem element) throws TierNotFoundException {
    verifyTierExistence(tier);
    element.changeTo(TIERED);
    tier.add(element);
  }

  @Deprecated
  private int verifyTierExistence(int tierIndex) throws TierNotFoundException {
    var exception = new TierNotFoundException("Tier at index \"" + tierIndex + "\" not found");
    try {
      if (tierIndex == -1)
        throw exception;
      else
        return tierIndex;
    } catch (IndexOutOfBoundsException indexException) {
      throw exception;
    }
  }

  @Deprecated
  private int verifyTierExistence(Tier tier) throws TierNotFoundException {
    var exception = new TierNotFoundException("Tier \"" + tier + "\" not found");
    int tierIndex = tiers.indexOf(tier);
    try {
      if (tierIndex == -1)
        throw exception;
      else
        return tierIndex;
    } catch (IndexOutOfBoundsException indexException) {
      throw exception;
    }
  }

  @Deprecated
  private int verifyElementExistence(TierItem element, List<TierItem> inList)
      throws TierItemNotFoundException {
    var exception = new TierItemNotFoundException(
        "Element \"" + element + "\" not found in list \"" + inList + "\"");
    try {
      int elementIndex = inList.indexOf(element);
      if (elementIndex == -1)
        throw exception;
      return elementIndex;
    } catch (IndexOutOfBoundsException physException) {
      throw exception;
    }
  }

  @Deprecated
  private void verifyElementExistenceInTier(TierItem element, int tierIndex)
      throws TierItemNotFoundException, TierNotFoundException {
    verifyTierExistence(tierIndex);
    verifyElementExistence(element, tiers.get(tierIndex).getTiered());
  }

  @Deprecated
  private void verifyElementExistenceInTier(TierItem element, Tier tier)
      throws TierItemNotFoundException, TierNotFoundException {
    verifyTierExistence(tier);
    verifyElementExistence(element, tier.getTiered());
  }

  /**
   * Moves elements between tiers
   * 
   * @param element     element to move
   * @param toTierIndex tier destination index
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   */
  @Deprecated
  public void moveToTier(TierItem element, int toTierIndex)
      throws TierItemNotFoundException, TierNotFoundException {

    verifyTierExistence(toTierIndex);

    int fromTierIndex = tiers.indexOf(tierByItem(element));
    if (tiers.get(fromTierIndex).remove(element))
      tiers.get(toTierIndex).add(element);
  }

  /**
   * Moves elements between tiers
   *
   * @param element element to move
   * @param toTier  tier destination index
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   */
  @Deprecated
  public void moveToTier(TierItem element, Tier toTier) throws TierItemNotFoundException, TierNotFoundException {

    verifyTierExistence(toTier);

    if (tierByItem(element).remove(element)) {
      toTier.add(element);
    }
  }

  /**
   * Moves elements between tiers to a specified position
   * 
   * @param element        element to move
   * @param toTierIndex    tier destination index
   * @param toElementIndex destination position index
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   */
  @Deprecated
  public void moveToTier(TierItem element, int toTierIndex, int toElementIndex)
      throws TierItemNotFoundException, TierNotFoundException {

    verifyTierExistence(toTierIndex);

    int fromTierIndex = tiers.indexOf(tierByItem(element));
    if (tiers.get(fromTierIndex).remove(element)) {
      tiers.get(toTierIndex).add(element);
      tiers.get(toTierIndex).move(element, toElementIndex);
    }
  }

  /**
   * Moves elements between tiers to a specified position
   *
   * @param element        element to move
   * @param toTier         tier destination index
   * @param toElementIndex destination position index
   * @throws TierNotFoundException        if tier doesn't exist
   * @throws TierItemNotFoundException if tier element doesn't exist
   */
  @Deprecated
  public void moveToTier(TierItem element, Tier toTier, int toElementIndex)
      throws TierItemNotFoundException, TierNotFoundException {

    verifyTierExistence(toTier);

    int fromTierIndex = tiers.indexOf(tierByItem(element));
    if (tiers.get(fromTierIndex).remove(element)) {
      toTier.add(element);
      toTier.move(element, toElementIndex);
    }
  }
}
