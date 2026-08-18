package net.flynn.opentierlist.controller;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.flynn.opentierlist.model.enums.TierStringFormat;
import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.ui.manual.TieredPane;

public interface TierListController {

  static TierListController of(TierList tl) {
    Objects.requireNonNull(tl);
    return new StandardTierListController(tl);
  }

  static TierListController ofDefaultTiers() {
    return new StandardTierListController(TierList.ofDefaultTiers());
  }

  void tier(TierItem unTiered, Tier toTier);

  void tier(TierItem unTiered, Tier toTier, TierItem position);

  void tier(TierItem unTiered, Tier toTier, int toIndex);

  void unTier(TierItem tiered);

  void unTier(TierItem tiered, TierItem position);

  void unTier(TierItem tiered, int toIndex);

  void addTier(Tier tier);

  void addDefaultTier();

  void addUnTiered(TierItem unTiered);

  void removeTier(Tier tier);

  void removeItem(TierItem item);

  void moveItem(TierItem tiered, Tier toTier);

  void insertItem(TierItem item, Tier toTier, TierItem position);

  void insertItem(TierItem item, Tier toTier, int index);

  void moveTier(Tier from, Tier to);

  void moveTier(Tier from, int toIndex);

  void setTierList(TierList tierList);

  void setTierListName(String name);

  void setTierName(Tier tier, String name);

  Optional<Tier> getTierByItem(TierItem item);

  TierItem getItemByHash(Integer hashCode);

  Tier getTierByHash(Integer hashCode);

  List<TierItem> getUnTiered();

  List<Tier> getTiers();

  String getTierListName();

  boolean saveTierList();

  boolean saveTierList(Path path);

  boolean exportTierList(TieredPane node);

  boolean exportTierList(TieredPane node, Path path);

  boolean itemExists(Integer hash);

  Optional<TierList> parseTierList(File file);

  String toString();

  String toString(TierStringFormat format);

}
