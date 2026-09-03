package net.flynn.opentierlist.model.models;

import net.flynn.opentierlist.persistence.impl.DesktopImagePath;
import net.flynn.opentierlist.persistence.ResourceHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.Assert.*;

public class DesktopImagePathTest {
  private DesktopImagePath desktopImagePath;
  private URI resource;
  private String pathResource;

  @Before
  public void setUp() throws NullPointerException, URISyntaxException {
    pathResource = "/greyyakuza.jpg";

    resource = Objects.requireNonNull(getClass().getResource(pathResource)).toURI();
    desktopImagePath = new DesktopImagePath(resource);
  }

  @After
  public void tearDown() {
    resource = null;
    desktopImagePath = null;
    pathResource = null;
  }

  @Test
  public void ofURI() throws URISyntaxException {
    assertEquals(desktopImagePath, DesktopImagePath.of(resource));
    assertEquals(DesktopImagePath.defaultResource(), DesktopImagePath.of(new URI("_")));
  }

  @Test
  public void ofString() {
    assertEquals(DesktopImagePath.defaultResource(), DesktopImagePath.of("_"));
  }

  @Test
  public void ofFile() {
    assertEquals(desktopImagePath, DesktopImagePath.of(new File(resource)));
    assertEquals(DesktopImagePath.defaultResource(), DesktopImagePath.of(new File("_")));
  }

  @Test
  public void defaultResource() throws URISyntaxException {
    assertEquals(DesktopImagePath.defaultResource(), new DesktopImagePath(
        Objects.requireNonNull(
            getClass().getResource(ResourceHolder.DEFAULT_ITEM_IMAGE)).toURI()));
  }

  @Test
  public void getUri() {
    assertEquals(resource.toString(), desktopImagePath.getUriAsString());
  }

  @Test
  public void exists() {
    assertTrue(desktopImagePath.exists());
  }
}
