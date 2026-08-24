package net.flynn.opentierlist.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import net.flynn.opentierlist.model.enums.DefaultTier;
import net.flynn.opentierlist.model.exceptions.TierItemNotFoundException;
import net.flynn.opentierlist.model.exceptions.TierNotFoundException;
import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.persistence.TierListDataHandler;
import net.flynn.opentierlist.ui.manual.TieredPane;

public class StdTierListController implements TierListController {

  private TierList tierList;
  private final TierListDataHandler dataHandler;

  public StdTierListController(TierList tierList, TierListDataHandler dataHandler) {
    this.tierList = tierList;
    this.dataHandler = dataHandler;
  }

  @Override
  public void tier(TierItem unTiered, Tier toTier) {
    try {
      tierList.tier(unTiered, toTier);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void tier(TierItem unTiered, Tier toTier, TierItem position) {
    try {
      tierList.tierInsert(unTiered, toTier, position);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void tier(TierItem unTiered, Tier toTier, int toIndex) {
    try {
      tierList.tierInsert(unTiered, toTier, toIndex);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void unTier(TierItem tiered) {
    try {
      tierList.unTier(tiered);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void unTier(TierItem tiered, TierItem position) {
    try {
      tierList.unTierInsert(tiered, position);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void unTier(TierItem tiered, int toIndex) {
    try {
      tierList.unTierInsert(tiered, toIndex);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void setTierList(TierList tierList) {
    try {
      this.tierList = tierList;
    } catch (NullPointerException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void setTierListName(String name) {
    try {
      tierList.setName(name);
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void setTierName(Tier tier, String name) {
    try {
      tierList.setTierName(tierList.indexOf(tier), name);
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void addTier(Tier tier) {
    try {
      tierList.addTier(tier);
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void addDefaultTier() {
    try {
      tierList.addTier(new Tier());
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void addUnTiered(TierItem item) {
    try {
      tierList.addItem(item, DefaultTier.UNTIERED.value());
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void removeTier(Tier tier) {
    try {
      tierList.removeTier(tier);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void removeItem(TierItem item) {
    try {
      tierList.removeItem(item);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void moveItem(TierItem item, Tier toTier) {
    try {
      tierList.moveItem(item, toTier);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void insertItem(TierItem item, Tier toTier, TierItem position) {
    try {
      tierList.insertItem(item, toTier, position);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
    }
  }

  @Override
  public void insertItem(TierItem item, Tier toTier, int index) {
    try {
      tierList.insertItem(item, toTier, index);
    } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException ex) {
      System.err.println(ex.getMessage());
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
      System.err.println(ex.getMessage());
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
      System.err.println(ex.getMessage());
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
      System.err.println(ex.getMessage());
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
      System.err.println(ex.getMessage());
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
      System.err.println(ex.getMessage());
      return false;
    }
  }

  @Override
  public boolean itemExists(Integer hash) {

    return Stream
        .concat(
            tierList.getTiered()
                .stream()
                .map(Tier::getItems)
                .flatMap(List::stream),
            tierList.getUnTiered()
                .stream())
        .anyMatch(e -> Objects.equals(e.hashCode(), hash));

  }

  @Override
  public Optional<TierList> parseTierList(File file) {
    return dataHandler.load(file);
  }

  @Override
  public String toString() {
    return tierList.toString();
  }

  @Override
  public String getTierListName() {
    return tierList.getName();
  }

  @Override
  public List<TierItem> getUnTiered() {
    return tierList.getUnTiered();
  }

  @Override
  public List<Tier> getTiers() {
    return tierList.getTiered();
  }

  @Override
  public Optional<Tier> getTierByItem(TierItem item) {
    Optional<Tier> potentialItem = Optional.empty();

    try {
      potentialItem = tierList.getTiered().stream()
          .filter(t -> t.contains(item))
          .findFirst();

      if (potentialItem.isEmpty() && getUnTiered().contains(item))
        potentialItem = Optional.of(DefaultTier.UNTIERED.value());

    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
    }

    return potentialItem;
  }

  @Override
  public TierItem getItemByHash(Integer hashCode) {
    Optional<TierItem> item;

    try {

      item = Stream
          .concat(
              tierList.getTiered().stream()
                  .map(Tier::getItems)
                  .flatMap(List::stream),
              tierList.getUnTiered().stream())
          .filter(e -> e.hashCode() == hashCode)
          .findFirst();

      if (item.isEmpty())
        throw new TierItemNotFoundException();
      return item.get();
    } catch (NullPointerException | IllegalArgumentException ex) {
      System.err.println(ex.getMessage());
      return null;
    }
  }

  @Override
  public Tier getTierByHash(Integer hashCode) {
    if (hashCode == DefaultTier.UNTIERED.value().hashCode())
      return DefaultTier.UNTIERED.value();

    Optional<Tier> tier;
    try {

      tier = tierList.getTiered().stream()
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

}
