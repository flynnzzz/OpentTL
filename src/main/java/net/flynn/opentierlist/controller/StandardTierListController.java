package net.flynn.opentierlist.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import net.flynn.opentierlist.model.enums.TierStringFormat;
import net.flynn.opentierlist.model.exceptions.TierItemNotFoundException;
import net.flynn.opentierlist.model.exceptions.TierNotFoundException;
import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.persistence.DataHandler;
import net.flynn.opentierlist.ui.manual.TieredPane;

/**
 * Main implementation of {@link TierListController}.
 * 
 * @author flynnz
 * @version 2.0.0
 * @since v0.0.0
 */
public class StandardTierListController implements TierListController {

  private TierList tierList;
  private final DataHandler dataHandler;

  /**
   * Constructor that creates a controller for {@link TierList}.
   * <p>
   * Instantiates an {@link TierList} with the given parameter
   *
   * @param tierList parameter to pass
   */
  public StandardTierListController(TierList tierList) {
    this.tierList = tierList;
    this.dataHandler = new DataHandler();
  }

  @Override
  public void tier(TierItem unTiered, Tier toTier) {
    try {
      tierList.tier(unTiered, toTier);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'tier' method ---");
    }
  }

  @Override
  public void tier(TierItem unTiered, Tier toTier, TierItem position) {
    try {
      tierList.tierInsert(unTiered, toTier, position);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'tierInsert' method ---");
    }
  }

  @Override
  public void tier(TierItem unTiered, Tier toTier, int toIndex) {
    try {
      tierList.tierInsert(unTiered, toTier, toIndex);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'tierInsert' method ---");
    }
  }

  @Override
  public void unTier(TierItem tiered) {
    try {
      tierList.unTier(tiered);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'unTier' method ---");
    }
  }

  @Override
  public void unTier(TierItem tiered, TierItem position) {
    try {
      tierList.unTierInsert(tiered, position);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'unTierInsert' method ---");
    }
  }

  @Override
  public void unTier(TierItem tiered, int toIndex) {
    try {
      tierList.unTierInsert(tiered, toIndex);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'unTierInsert' method ---");
    }
  }

  @Override
  public void setTierList(TierList tierList) {
    try {
      this.tierList = tierList;
    } catch (NullPointerException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'setTierList' method ---");
    }
  }

  @Override
  public void setTierListName(String name) {
    try {
      tierList.setTierListName(name);
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'setTierListName' method ---");
    }
  }

  @Override
  public void setTierName(Tier tier, String name) {
    try {
      tierList.setTierName(tierList.indexOf(tier), name);
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'setTierName' method ---");
    }
  }

  @Override
  public void addTier(Tier tier) {
    try {
      tierList.addTier(tier);
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'addTier' method ---");
    }
  }

  @Override
  public void addDefaultTier() {
    try {
      tierList.addTier(new Tier());
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'addDefaultTier' method ---");
    }
  }

  @Override
  public void addUnTiered(TierItem item) {
    try {
      tierList.addItem(item, Tier.UNTIERED);
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'addUnTiered' method ---");
    }
  }

  @Override
  public void removeTier(Tier tier) {
    try {
      tierList.removeTier(tier);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'deleteTier' method ---");
    }
  }

  @Override
  public void removeItem(TierItem item) {
    try {
      tierList.removeItem(item);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'deleteTierElement' method ---");
    }
  }

  @Override
  public void moveItem(TierItem item, Tier toTier) {
    try {
      tierList.moveItem(item, toTier);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'moveElement' method ---");
    }
  }

  @Override
  public void insertItem(TierItem item, Tier toTier, TierItem position) {
    try {
      tierList.insertItem(item, toTier, position);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'moveElement' method ---");
    }
  }

  @Override
  public void insertItem(TierItem item, Tier toTier, int index) {
    try {
      tierList.insertItem(item, toTier, index);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'moveElement' method ---");
    }
  }

  @Override
  public void moveTier(Tier from, Tier to) {
    try {
      tierList.moveTier(from, to);
    } catch (
        NullPointerException
        | IllegalArgumentException
        | IndexOutOfBoundsException
        | UnsupportedOperationException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'moveTier' method ---");
    }
  }

  @Override
  public void moveTier(Tier from, int toIndex) {
    try {
      tierList.moveTier(from, toIndex);
    } catch (
        NullPointerException
        | IllegalArgumentException
        | IndexOutOfBoundsException
        | UnsupportedOperationException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'moveTier' method ---");
    }
  }

  @Override
  public boolean saveTierList() {
    try {
      final Path defaultPath = Path.of(System.getProperty("user.home"), "Documents", "OpenTierList");

      if (!Files.exists(defaultPath)) {
        if (!defaultPath.toFile().mkdir()) {
          System.err.println(
              "[ERROR] --- Could not create folder 'OpenTierList' in " + System.getProperty("user.home")
                  + "/Documents ---");
          return false;
        }
      }

      saveTierList(defaultPath.resolve(getTierListName() + ".tson"));
      return true;

    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'saveTierList' method ---");
      return false;
    }
  }

  @Override
  public boolean saveTierList(Path path) {
    try {

      if (!path.toString().endsWith(".tson"))
        path = Path.of(path + ".tson");

      dataHandler.save(path.toFile(), tierList);
      return true;
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'saveTierList' method ---");
      return false;
    }
  }

  @Override
  public boolean exportTierList(TieredPane node) {
    final Path defaultPath = Path.of(System.getProperty("user.home"), "Pictures", "OpenTierList");

    if (!Files.exists(defaultPath)) {
      if (!defaultPath.toFile().mkdir()) {
        System.err.println(
            "[ERROR] --- Could not create folder 'OpenTierList' in " + System.getProperty("user.home")
                + "/Pictures ---");
        return false;
      }
    }

    exportTierList(node, defaultPath.resolve(getTierListName() + ".png"));
    return true;
  }

  @Override
  public boolean exportTierList(TieredPane node, Path path) {
    try {

      if (!path.toString().endsWith(".png"))
        path = Path.of(path + ".png");

      dataHandler.export(path, node);
      return true;
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'saveTierList' method ---");
      return false;
    }
  }

  @Deprecated
  @Override
  public void saveTierListAs(String name) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("[ERROR] --- Deprecated method used ---");
  }

  @Override
  public Optional<TierList> loadTierList(File file) {
    return dataHandler.load(file);
  }

  @Override
  public String toString() {
    return tierList.toString();
  }

  @Override
  public String toString(TierStringFormat format) {
    return tierList.toString(format);
  }

  @Override
  public String getTierListName() {
    return tierList.getTierListName();
  }

  @Override
  public List<TierItem> getUnTiered() {
    return tierList.getUnTiered();
  }

  @Override
  public List<Tier> getTiers() {
    return tierList.getTiers();
  }

  @Override
  public Optional<Tier> getTierByItem(TierItem item) {
    Optional<Tier> element = Optional.empty();

    try {
      element = tierList.getTiers().stream()
          .filter(t -> t.contains(item))
          .findFirst();

      if (element.isEmpty() && getUnTiered().contains(item))
        element = Optional.of(Tier.UNTIERED);

    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'getTierByElement' method ---");
    }

    return element;
  }

  @Override
  public TierItem getItemByHash(Integer hashCode) {
    Optional<TierItem> element = Optional.empty();

    try {

      element = Stream
          .concat(
              tierList.getTiers().stream()
                  .flatMap(t -> t.getTiered().stream()),
              tierList.getUnTiered().stream())
          .filter(e -> e.hashCode() == hashCode)
          .findFirst();

      if (element.isEmpty())
        throw new TierItemNotFoundException();
      return element.get();
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'getElementByHash' method ---");
      return null;
    }
  }

  @Override
  public Tier getTierByHash(Integer hashCode) {
    if (hashCode == Tier.UNTIERED.hashCode())
      return Tier.UNTIERED;

    Optional<Tier> tier = Optional.empty();
    try {

      tier = tierList.getTiers().stream()
          .filter(t -> hashCode.equals(t.hashCode()))
          .findFirst();

      if (tier.isEmpty())
        throw new TierNotFoundException("[ERROR] --- No Tier with hashcode " + hashCode + " found ---");
      return tier.get();
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'getTierByHash' method ---");
      return null;
    }
  }

  @Override
  public boolean itemExists(Integer hash) {

    return Stream
        .concat(
            tierList.getTiers()
                .stream()
                .flatMap(t -> t.getTiered().stream()),
            tierList.getUnTiered()
                .stream())
        .anyMatch(e -> Objects.equals(e.hashCode(), hash));

  }

  @Override
  @Deprecated
  public void swapTiered(Tier tier, TierItem a, TierItem b) {
    try {
      tierList.swapTiered(tier, a, b);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'swapTiered' method ---");
    }
  }

  @Override
  @Deprecated
  public void swapUnTiered(TierItem a, TierItem b) {
    try {
      tierList.swapUnTiered(a, b);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'swapUnTiered' method ---");
    }
  }

  @Override
  @Deprecated
  public void appendTiered(TierItem tiered, Tier toTier) {
    try {
      tierList.moveToTier(tiered, toTier);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'appendTiered' method ---");
    }
  }

  @Override
  @Deprecated
  public void moveTiered(TierItem tiered, Tier toTier, TierItem toElement) {
    try {
      int toIndex = toTier.getTiered().indexOf(toElement);
      tierList.moveToTier(tiered, toTier, toIndex);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'moveTiered' method ---");
    }
  }

  @Override
  @Deprecated
  public void moveTiered(TierItem tiered, Tier toTier, int toIndex) {
    try {
      tierList.moveToTier(tiered, toTier, toIndex);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'moveTiered' method ---");
    }
  }

  @Override
  @Deprecated
  public void moveUnTiered(TierItem unTiered, TierItem toElement) {
  }

  @Override
  @Deprecated
  public void moveUnTiered(TierItem unTiered, int toIndex) {
  }

  @Override
  @Deprecated
  public void deleteUnTiered(TierItem unTiered) {
    try {
      tierList.removeUnTiered(unTiered);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'deleteUnTiered' method ---");
    }
  }

  @Override
  @Deprecated
  public void swapTiers(Tier a, Tier b) {
    try {
      tierList.swapTiers(a, b);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println("[ERROR] --- " + ex.getClass() + ": in 'swapTiers' method ---");
    }
  }

}
