package net.flynn.opentierlist.model.models;

import net.flynn.opentierlist.persistence.ImagePath;
import net.flynn.opentierlist.persistence.ResourceHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.Assert.*;

public class ImagePathTest {
  private ImagePath imagePath;
  private URI resource;
  private String pathResource;

  @Before
  public void setUp() throws NullPointerException, URISyntaxException {
    pathResource = "/greyyakuza.jpg";

    resource = Objects.requireNonNull(getClass().getResource(pathResource)).toURI();
    imagePath = new ImagePath(resource);
  }

  @After
  public void tearDown() {
    resource = null;
    imagePath = null;
    pathResource = null;
  }

  @Test
  public void ofURI() throws URISyntaxException {
    assertEquals(imagePath, ImagePath.of(resource));
    assertEquals(ImagePath.defaultResource(), ImagePath.of(new URI("_")));
  }

  @Test
  public void ofString() {
    assertEquals(ImagePath.defaultResource(), ImagePath.of("_"));
  }

  @Test
  public void ofFile() {
    assertEquals(imagePath, ImagePath.of(new File(resource)));
    assertEquals(ImagePath.defaultResource(), ImagePath.of(new File("_")));
  }

  @Test
  public void defaultResource() throws URISyntaxException {
    assertEquals(ImagePath.defaultResource(), new ImagePath(
        Objects.requireNonNull(
            getClass().getResource(ResourceHolder.DEFAULT_ITEM_IMAGE)).toURI()));
  }

  @Test
  public void getUri() {
    assertEquals(resource.toString(), imagePath.getUriAsString());
  }

  @Test
  public void exists() {
    assertTrue(imagePath.exists());
  }
}
