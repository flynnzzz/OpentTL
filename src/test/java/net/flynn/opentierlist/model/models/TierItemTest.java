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
    el1 = new TierItem("elementName1");
    el2 = new TierItem("elementName2", resource);
    el3 = new TierItem(TieredStatus.TIERED, "elementName3", resource);

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
    assertEquals("elementName1", el1.getItemName());
    assertEquals("elementName2", el2.getItemName());
    assertEquals("elementName3", el3.getItemName());
  }

  @Test
  public void setItemName() {
    el1.setItemName("newElementName1");
    assertEquals("newElementName1", el1.getItemName());
    el1.setItemName("elementName1");
    assertEquals("elementName1", el1.getItemName());

    el2.setItemName("newElementName2");
    assertEquals("newElementName2", el2.getItemName());

    el2.setItemName("newNewElementName2");
    assertEquals("newNewElementName2", el2.getItemName());

    el2.setItemName("elementName2");
    assertEquals("elementName2", el2.getItemName());

    assertThrows(IllegalArgumentException.class, () -> el1.setItemName(""));
    assertThrows(IllegalArgumentException.class, () -> el1.setItemName(" "));
    assertThrows(IllegalArgumentException.class, () -> el1.setItemName(System.lineSeparator()));

    el1.setItemName("elementName1");
  }

  @Test
  public void getImageUri() throws URISyntaxException {

    String resource = Objects.requireNonNull(getClass().getResource("/greyyakuza.jpg")).toURI().toString();

    assertEquals(
        (Objects.requireNonNull(getClass().getResource(ResourceHolder.DEFAULT_ITEM_IMAGE))).toURI().toString(),
        defaultTierItem.getImageUri()
    );

    assertEquals(resource, el2.getImageUri() );
  }

  @Test
  public void updateImagePath() throws URISyntaxException {

    var el4 = new TierItem("elementName4", "nonExistentUrl");

    assertEquals(
            (Objects.requireNonNull(getClass().getResource(ResourceHolder.DEFAULT_ITEM_IMAGE))).toURI().toString(),
            el4.getImageUri()
    );

    el4.updateImagePath();

    assertEquals(
            (Objects.requireNonNull(getClass().getResource(ResourceHolder.DEFAULT_ITEM_IMAGE))).toURI().toString(),
            el4.getImageUri()
    );

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
