package net.flynn.opentierlist.controller;

import net.flynn.opentierlist.model.enums.DefaultTier;
import net.flynn.opentierlist.model.enums.TieredStatus;
import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.model.models.TierList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DesktopTierListControllerTest {

  private Tier t1, t2, t3, t4;
  private TierList tl;
  private TierItem el1, el2, el3, el4;
  private TierListController controller;
  private Tier untiered = DefaultTier.UNTIERED.value();

  @Before
  public void setUp() {
    el1 = new TierItem("1");
    el2 = new TierItem("2");
    el3 = new TierItem("3");
    el4 = new TierItem("4");

    tl = new TierList(new ArrayList<>(List.of(el1, el2, el3, el4)));
    t1 = new Tier("t1");
    t2 = new Tier("t2");
    t3 = new Tier("t3");
    t4 = new Tier("t4");

    tl.addTier(t1);
    tl.addTier(t2);
    tl.addTier(t3);
    tl.addTier(t4);

    controller = TierListController.of(tl);
  }

  @After
  public void tearDown() {
    el1 = null;
    el2 = null;
    el3 = null;
    el4 = null;
    t1 = null;
    t2 = null;
    t3 = null;
    t4 = null;
    tl = null;
    untiered.clear();
    untiered = null;
    controller = null;
  }

  @Test
  public void tier() {

    assertFalse(el1.isTiered());
    controller.tier(el1, t1);
    assertTrue(el1.isTiered());
    assertEquals(List.of(el1), t1.getItems());
    assertEquals(List.of(el2, el3, el4), controller.getUnTiered());

    assertFalse(el2.isTiered());
    controller.tier(el2, t1);
    assertTrue(el2.isTiered());
    assertEquals(List.of(el1, el2), t1.getItems());
    assertEquals(List.of(el3, el4), controller.getUnTiered());

    assertFalse(el3.isTiered());
    controller.tier(el3, t3);
    assertTrue(el3.isTiered());
    assertEquals(List.of(el3), t3.getItems());
    assertEquals(List.of(el4), controller.getUnTiered());

    controller.tier(el4, untiered);
    assertEquals(TieredStatus.UNTIERED, el4.getStatus());

    // exceptions are caught

    controller.tier(el1, null);
    controller.tier(null, t1);
    controller.tier(null, null);

    controller.tier(el1, t1);
    controller.tier(el2, t1);
    controller.tier(new TierItem(), t1);
    controller.tier(el4, new Tier());
  }

  @Test
  public void tierInsertPointer() {

    controller.tier(el1, t1, el1);

    controller.removeItem(el1);
    controller.removeItem(el2);

    tl.addItem(el1, t1);
    tl.addItem(el2, t1);
    el1.changeTo(TieredStatus.TIERED);
    el2.changeTo(TieredStatus.TIERED);

    // t1: el1, el2
    assertEquals(List.of(el1, el2), t1.getItems());
    assertEquals(List.of(el3, el4), controller.getUnTiered());

    controller.tier(el3, t1, el1);
    // t1: el3, el1, el2
    assertEquals(List.of(el3, el1, el2), t1.getItems());
    assertEquals(List.of(el4), controller.getUnTiered());

    controller.tier(el4, new Tier(), el1);

    controller.tier(el4, t1, el1);
    // t1: el3, el4, el1, el2
    assertEquals(List.of(el3, el4, el1, el2), t1.getItems());
    assertEquals(List.of(), controller.getUnTiered());

    final var el5 = new TierItem("el5");

    tl.addItem(el5, untiered);
    controller.tier(el5, t1, el2);
    // t1: el3, el4, el1, el5, el2
    assertEquals(List.of(el3, el4, el1, el5, el2), t1.getItems());
    assertEquals(List.of(), controller.getUnTiered());

    controller.tier(el1, t1, 0);
    controller.tier(el2, t1, 0);
    controller.tier(el3, t1, 0);
    controller.tier(el4, t1, 0);

  }

  @Test
  public void tierInsertIndex() {

    controller.tier(el1, t1, 0);
    controller.tier(el1, t1, 1);
    controller.tier(el1, t1, -1);

    controller.removeItem(el1);
    controller.removeItem(el2);

    tl.addItem(el1, t1);
    tl.addItem(el2, t1);

    el1.changeTo(TieredStatus.TIERED);
    el2.changeTo(TieredStatus.TIERED);

    // t1: el1, el2
    assertEquals(List.of(el1, el2), t1.getItems());
    assertEquals(List.of(el3, el4), controller.getUnTiered());

    controller.tier(el3, t1, 1);
    // t1: el1, el3, el2
    assertEquals(List.of(el1, el3, el2), t1.getItems());
    assertEquals(List.of(el4), controller.getUnTiered());

    controller.tier(el4, t1, 0);
    controller.tier(el4, new Tier(), -1);

    // t1: el4, el1, el3, el2
    assertEquals(List.of(el4, el1, el3, el2), t1.getItems());
    assertEquals(List.of(), controller.getUnTiered());

    assertTrue(
        controller.getTiers()
            .stream()
            .map(Tier::getItems)
            .flatMap(List::stream)
            .map(TierItem::getStatus)
            .allMatch(
                e -> e.equals(TieredStatus.TIERED)));

    controller.tier(el1, t1, 0);
    controller.tier(el2, t1, 0);

  }

  @Test
  public void unTier() {

    final var nonExistent = new TierItem();
    nonExistent.changeTo(TieredStatus.TIERED);

    controller.unTier(nonExistent);
    controller.unTier(el1);

    controller.tier(el1, t1);
    controller.tier(el2, t1);
    controller.tier(el3, t2);
    controller.tier(el4, t3);

    assertTrue(
        controller.getTiers()
            .stream()
            .map(Tier::getItems)
            .flatMap(List::stream)
            .map(TierItem::getStatus)
            .allMatch(
                e -> e.equals(TieredStatus.TIERED)));

    assertEquals(List.of(el1, el2), t1.getItems());
    assertEquals(List.of(el3), t2.getItems());
    assertEquals(List.of(el4), t3.getItems());
    assertEquals(List.of(), controller.getUnTiered());

    controller.unTier(el1);
    assertEquals(List.of(el2), t1.getItems());
    assertEquals(List.of(el1), controller.getUnTiered());

    controller.unTier(el2);
    assertEquals(List.of(), t1.getItems());
    assertEquals(List.of(el1, el2), controller.getUnTiered());

    controller.unTier(el3);
    assertEquals(List.of(), t2.getItems());
    assertEquals(List.of(el1, el2, el3), controller.getUnTiered());

    controller.unTier(el4);
    assertEquals(List.of(), t3.getItems());
    assertEquals(List.of(el1, el2, el3, el4), controller.getUnTiered());

    assertTrue(
        controller.getTiers()
            .stream()
            .map(Tier::getItems)
            .flatMap(List::stream)
            .map(TierItem::getStatus)
            .allMatch(
                e -> e.equals(TieredStatus.UNTIERED)));

  }

  @Test
  public void unTierInsertPointer() {

    final var nonExistent = new TierItem();
    nonExistent.changeTo(TieredStatus.TIERED);

    // exceptions are caught
    controller.unTier(nonExistent, el1);
    controller.unTier(el1, el2);

    controller.tier(el1, t1);
    controller.tier(el2, t1);
    controller.tier(el3, t2);
    controller.tier(el4, t3);

    assertTrue(
        controller.getTiers()
            .stream()
            .map(Tier::getItems)
            .flatMap(List::stream)
            .map(TierItem::getStatus)
            .allMatch(
                e -> e.equals(TieredStatus.TIERED)));

    assertEquals(List.of(el1, el2), t1.getItems());
    assertEquals(List.of(el3), t2.getItems());
    assertEquals(List.of(el4), t3.getItems());
    assertEquals(List.of(), controller.getUnTiered());

    controller.unTier(el1);
    assertEquals(List.of(el2), t1.getItems());
    assertEquals(List.of(el1), controller.getUnTiered());

    controller.unTier(el2, el1);
    assertEquals(List.of(), t1.getItems());
    assertEquals(List.of(el2, el1), controller.getUnTiered());

    controller.unTier(el3, el1);
    assertEquals(List.of(), t2.getItems());
    assertEquals(List.of(el2, el3, el1), controller.getUnTiered());

    controller.unTier(el4, el2);
    assertEquals(List.of(), t3.getItems());
    assertEquals(List.of(el4, el2, el3, el1), controller.getUnTiered());

    assertTrue(
        controller.getTiers()
            .stream()
            .map(Tier::getItems)
            .flatMap(List::stream)
            .map(TierItem::getStatus)
            .allMatch(
                e -> e.equals(TieredStatus.UNTIERED)));

  }

  @Test
  public void unTierInsertIndex() {

    final var nonExistent = new TierItem();
    nonExistent.changeTo(TieredStatus.TIERED);

    // exceptions are caught
    controller.unTier(nonExistent, 0);
    controller.unTier(el1, -1);
    controller.unTier(el2, 999);

    controller.tier(el1, t1);
    controller.tier(el2, t1);
    controller.tier(el3, t2);
    controller.tier(el4, t3);

    assertTrue(
        controller.getTiers()
            .stream()
            .map(Tier::getItems)
            .flatMap(List::stream)
            .map(TierItem::getStatus)
            .allMatch(
                e -> e.equals(TieredStatus.TIERED)));

    assertEquals(List.of(el1, el2), t1.getItems());
    assertEquals(List.of(el3), t2.getItems());
    assertEquals(List.of(el4), t3.getItems());
    assertEquals(List.of(), controller.getUnTiered());

    controller.unTier(el1);
    assertEquals(List.of(el2), t1.getItems());
    assertEquals(List.of(el1), controller.getUnTiered());

    controller.unTier(el2, 0);
    assertEquals(List.of(), t1.getItems());
    assertEquals(List.of(el2, el1), controller.getUnTiered());

    controller.unTier(el3, 2);
    assertEquals(List.of(), t2.getItems());
    assertEquals(List.of(el2, el1, el3), controller.getUnTiered());

    controller.unTier(el4, 1);
    assertEquals(List.of(), t3.getItems());
    assertEquals(List.of(el2, el4, el1, el3), controller.getUnTiered());

    assertTrue(
        controller.getTiers()
            .stream()
            .map(Tier::getItems)
            .flatMap(List::stream)
            .map(TierItem::getStatus)
            .allMatch(
                e -> e.equals(TieredStatus.UNTIERED)));

  }

  @Test
  public void setTierList() {
    final var newTl = new TierList();
    controller.setTierList(newTl);

    assertEquals(newTl.getName(), controller.getTierListName());
    assertEquals(newTl.getTiered(), controller.getTiers());
    assertEquals(newTl.getUnTiered(), controller.getUnTiered());
  }

  @Test
  public void setTierListName() {
    controller.setTierListName("new name");
    assertEquals("new name", controller.getTierListName());

    controller.setTierListName("   ");
    assertEquals("new name", controller.getTierListName());

    controller.setTierListName("another new name");
    assertEquals("another new name", controller.getTierListName());
  }

  @Test
  public void setTierName() {
    controller.setTierName(t1, "new name");
    assertEquals("new name", controller.getTiers().getFirst().getName());

    controller.setTierName(t1, "   ");
    assertEquals("new name", controller.getTiers().getFirst().getName());

    controller.setTierName(t1, "another new name");
    assertEquals("another new name", controller.getTiers().getFirst().getName());
  }

  @Test
  public void addTier() {

    final Tier t5 = new Tier("t5"), t6 = new Tier("t5");

    controller.addTier(t5);
    assertEquals(List.of(t1, t2, t3, t4, t5), controller.getTiers());

    controller.addTier(t6);
    assertEquals(List.of(t1, t2, t3, t4, t5, t6), controller.getTiers());

  }

  @Test
  public void addDefaultTier() {

    final int len = controller.getTiers().size();

    controller.addDefaultTier();
    assertEquals(len + 1, controller.getTiers().size());
    assertTrue(controller.getTiers().getLast().equalsTier(new Tier()));

    controller.addDefaultTier();
    assertEquals(len + 2, controller.getTiers().size());
    assertTrue(controller.getTiers().getLast().equalsTier(new Tier()));

  }

  @Test
  public void addUnTiered() {

    controller.removeItem(el1);
    controller.removeItem(el2);
    controller.removeItem(el3);
    controller.removeItem(el4);

    controller.addUnTiered(el1);
    assertEquals(List.of(el1), controller.getUnTiered());

    controller.addUnTiered(el2);
    assertEquals(List.of(el1, el2), controller.getUnTiered());

    controller.addUnTiered(el3);
    assertEquals(List.of(el1, el2, el3), controller.getUnTiered());

    controller.addUnTiered(el4);
    assertEquals(List.of(el1, el2, el3, el4), controller.getUnTiered());

  }

  @Test
  public void removeTier() {

    controller.removeTier(t1);
    assertEquals(List.of(t2, t3, t4), controller.getTiers());

    controller.removeTier(t2);
    assertEquals(List.of(t3, t4), controller.getTiers());

    controller.removeTier(t3);
    assertEquals(List.of(t4), controller.getTiers());

    controller.removeTier(t4);
    assertEquals(List.of(), controller.getTiers());

    controller.removeTier(new Tier());

  }

  @Test
  public void removeItems() {

    // setup
    controller.tier(el1, t1);
    controller.tier(el2, t2);
    controller.tier(el3, t2, 0);
    assertEquals(List.of(el1), t1.getItems());
    assertEquals(List.of(el3, el2), t2.getItems());
    assertEquals(List.of(el4), controller.getUnTiered());

    controller.removeItem(el4);
    assertEquals(List.of(), controller.getUnTiered());

    controller.removeItem(el1);
    assertEquals(List.of(), t1.getItems());

    controller.removeItem(el3);
    assertEquals(List.of(el2), t2.getItems());

    controller.removeItem(el2);
    assertEquals(List.of(), t2.getItems());

    controller.removeItem(el1);
    controller.removeItem(el2);
    controller.removeItem(el3);
    controller.removeItem(el4);

  }

  @Test
  public void moveItem() {

    assertEquals(List.of(el1, el2, el3, el4), controller.getUnTiered());

    controller.moveItem(el1, t1);
    assertEquals(TieredStatus.TIERED, el1.getStatus());
    assertEquals(List.of(el2, el3, el4), controller.getUnTiered());
    assertEquals(List.of(el1), t1.getItems());

    controller.moveItem(el2, t2);
    assertEquals(TieredStatus.TIERED, el2.getStatus());
    assertEquals(List.of(el3, el4), controller.getUnTiered());
    assertEquals(List.of(el1), t1.getItems());
    assertEquals(List.of(el2), t2.getItems());

    controller.moveItem(el3, t3);
    assertEquals(TieredStatus.TIERED, el3.getStatus());
    assertEquals(List.of(el4), controller.getUnTiered());
    assertEquals(List.of(el1), t1.getItems());
    assertEquals(List.of(el2), t2.getItems());
    assertEquals(List.of(el3), t3.getItems());

    controller.moveItem(el1, untiered);
    assertEquals(TieredStatus.UNTIERED, el1.getStatus());
    assertEquals(List.of(el4, el1), controller.getUnTiered());
    assertEquals(List.of(), t1.getItems());
    assertEquals(List.of(el2), t2.getItems());
    assertEquals(List.of(el3), t3.getItems());

  }

  @Test
  public void insertItemPointer() {

    // exception is caught
    controller.insertItem(el1, t1, new TierItem());

    assertEquals(List.of(el1, el2, el3, el4), controller.getUnTiered());

    controller.moveItem(el1, t1);
    assertEquals(TieredStatus.TIERED, el1.getStatus());
    assertEquals(List.of(el2, el3, el4), controller.getUnTiered());
    assertEquals(List.of(el1), t1.getItems());

    controller.insertItem(el2, t1, el1);
    assertEquals(TieredStatus.TIERED, el2.getStatus());
    assertEquals(List.of(el3, el4), controller.getUnTiered());
    assertEquals(List.of(el2, el1), t1.getItems());

    controller.insertItem(el3, t1, el2);
    assertEquals(TieredStatus.TIERED, el3.getStatus());
    assertEquals(List.of(el4), controller.getUnTiered());
    assertEquals(List.of(el3, el2, el1), t1.getItems());

    controller.insertItem(el1, untiered, el4);
    assertEquals(TieredStatus.UNTIERED, el1.getStatus());
    assertEquals(List.of(el1, el4), controller.getUnTiered());
    assertEquals(List.of(el3, el2), t1.getItems());

    controller.insertItem(el1, untiered, el4);
    assertEquals(List.of(el4, el1), controller.getUnTiered());

  }

  @Test
  public void insertItemIndex() {

    controller.insertItem(el1, t1, -1);

    assertEquals(List.of(el1, el2, el3, el4), controller.getUnTiered());

    controller.insertItem(el1, t1, 0);
    assertEquals(TieredStatus.TIERED, el1.getStatus());
    assertEquals(List.of(el2, el3, el4), controller.getUnTiered());
    assertEquals(List.of(el1), t1.getItems());

    controller.insertItem(el2, t1, 0);
    assertEquals(TieredStatus.TIERED, el2.getStatus());
    assertEquals(List.of(el3, el4), controller.getUnTiered());
    assertEquals(List.of(el2, el1), t1.getItems());

    controller.insertItem(el3, t1, 1);
    assertEquals(TieredStatus.TIERED, el3.getStatus());
    assertEquals(List.of(el4), controller.getUnTiered());
    assertEquals(List.of(el2, el3, el1), t1.getItems());

    controller.insertItem(el1, untiered, 1);
    assertEquals(TieredStatus.UNTIERED, el1.getStatus());
    assertEquals(List.of(el4, el1), controller.getUnTiered());
    assertEquals(List.of(el2, el3), t1.getItems());

  }

  @Test
  public void moveTierPointer() {

    controller.moveTier(untiered, t1);

    assertEquals(List.of(t1, t2, t3, t4), controller.getTiers());

    controller.moveTier(t1, t4);
    assertEquals(List.of(t2, t3, t4, t1), controller.getTiers());

    controller.moveTier(t4, t3);
    assertEquals(List.of(t2, t4, t3, t1), controller.getTiers());

    controller.moveTier(t1, t2);
    assertEquals(List.of(t1, t2, t4, t3), controller.getTiers());

  }

  @Test
  public void moveTierIndex() {

    assertEquals(List.of(t1, t2, t3, t4), controller.getTiers());

    controller.moveTier(t1, 3);
    assertEquals(List.of(t2, t3, t4, t1), controller.getTiers());

    controller.moveTier(t4, 1);
    assertEquals(List.of(t2, t4, t3, t1), controller.getTiers());

    controller.moveTier(t1, 0);
    assertEquals(List.of(t1, t2, t4, t3), controller.getTiers());

  }

  @Test
  public void itemExists() {

    assertTrue(controller.itemExists(el1.hashCode()));
    assertTrue(controller.itemExists(el2.hashCode()));
    assertTrue(controller.itemExists(el3.hashCode()));
    assertTrue(controller.itemExists(el4.hashCode()));
    assertFalse(controller.itemExists(new TierItem().hashCode()));

  }
}
