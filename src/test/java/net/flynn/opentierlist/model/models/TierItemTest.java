package net.flynn.opentierlist.model.models;

import java.net.URISyntaxException;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.flynn.opentierlist.model.enums.TieredStatus;
import net.flynn.opentierlist.persistence.ResourceHolder;

import static org.junit.Assert.*;

public class TierItemTest {

  TierItem defaultTierItem, el1, el2, el3;

  @Before
  public void setUp() throws Exception {

    final String resource = Objects.requireNonNull(getClass().getResource("/greyyakuza.jpg")).toURI().toString();

    defaultTierItem = new TierItem();
    el1 = new TierItem("itemName1");
    el2 = new TierItem("itemName2", resource);
    el3 = new TierItem(TieredStatus.TIERED, "itemName3", resource);

  }

  @After
  public void tearDown() {
    defaultTierItem = null;
    el1 = null;
    el2 = null;
    el3 = null;
  }

  @Test
  public void getStatus() {
    assertEquals(TieredStatus.UNTIERED, defaultTierItem.getStatus());
    assertEquals(TieredStatus.UNTIERED, el1.getStatus());
    assertEquals(TieredStatus.UNTIERED, el2.getStatus());
    assertEquals(TieredStatus.TIERED, el3.getStatus());
  }

  @Test
  public void isTiered() {
    assertFalse(defaultTierItem.isTiered());
    assertFalse(el1.isTiered());
    assertFalse(el2.isTiered());
    assertTrue(el3.isTiered());
  }

  @Test
  public void changeTo() {
    el1.changeTo(TieredStatus.TIERED);
    assertTrue(el1.isTiered());
    el1.changeTo(TieredStatus.UNTIERED);
    assertFalse(el1.isTiered());

    el2.changeTo(TieredStatus.TIERED);
    assertTrue(el2.isTiered());
    el2.changeTo(TieredStatus.UNTIERED);
    assertFalse(el2.isTiered());

    el3.changeTo(TieredStatus.TIERED);
    assertTrue(el3.isTiered());
    el3.changeTo(TieredStatus.UNTIERED);
    assertFalse(el3.isTiered());

    el3.changeTo(TieredStatus.UNTIERED);
    assertFalse(el3.isTiered());
    el3.changeTo(TieredStatus.TIERED);
    assertTrue(el3.isTiered());
  }

  @Test
  public void getItemName() {
    assertEquals("New Item", defaultTierItem.getItemName());
    assertEquals("itemName1", el1.getItemName());
    assertEquals("itemName2", el2.getItemName());
    assertEquals("itemName3", el3.getItemName());
  }

  @Test
  public void setItemName() {
    el1.setItemName("newitemName1");
    assertEquals("newitemName1", el1.getItemName());
    el1.setItemName("itemName1");
    assertEquals("itemName1", el1.getItemName());

    el2.setItemName("newitemName2");
    assertEquals("newitemName2", el2.getItemName());

    el2.setItemName("newNewitemName2");
    assertEquals("newNewitemName2", el2.getItemName());

    el2.setItemName("itemName2");
    assertEquals("itemName2", el2.getItemName());

    assertThrows(IllegalArgumentException.class, () -> el1.setItemName(""));
    assertThrows(IllegalArgumentException.class, () -> el1.setItemName(" "));
    assertThrows(IllegalArgumentException.class, () -> el1.setItemName(System.lineSeparator()));

    el1.setItemName("itemName1");
  }

  @Test
  public void getImageUri() throws URISyntaxException {

    String resource = Objects.requireNonNull(getClass().getResource("/greyyakuza.jpg")).toURI().toString();

    assertEquals(
        (Objects.requireNonNull(getClass().getResource(ResourceHolder.DEFAULT_ITEM_IMAGE))).toURI().toString(),
        defaultTierItem.getImageUri());

    assertEquals(resource, el2.getImageUri());
  }

  @Test
  public void testEquals() {

    assertNotEquals(el1, el2);
    assertNotEquals(el2, el3);
    assertNotEquals(el3, el1);

    assertNotEquals(el2, el1);
    assertNotEquals(el3, el2);
    assertNotEquals(el1, el3);

    assertNotEquals(el1, new TierItem(el1.getItemName(), el1.getImageUri()));

  }

  @Test
  public void equalsItem() {

    assertTrue(el1.equalsItem(new TierItem(el1.getItemName(), el1.getImageUri())));
    assertFalse(el1.equalsItem(el2));

  }
}
