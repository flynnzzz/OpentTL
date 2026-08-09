package net.flynn.opentierlist.model.models;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import net.flynn.opentierlist.persistence.ResourceHolder;

/**
 * Custom class to handle image resources
 *
 * @author flynnz
 * @version 0.10
 * @since v1.7.0
 */
public record ImagePath(URI uri) {
  private static final String DEFAULT_IMAGE_RESOURCE = ResourceHolder.DEFAULT_ITEM_IMAGE;

  public static ImagePath of(File file) throws IllegalArgumentException {
    if (file != null && file.exists()) {
      return new ImagePath(file.toURI());
    }
    return defaultResource();
  }

  public static ImagePath of(String uri) {

    try {
      return ImagePath.of(new URI(uri));
    } catch (URISyntaxException _) {
      System.err.println("[ERROR] --- Invalid url: " + uri + " ---");
      return defaultResource();
    }
  }

  public static ImagePath of(URI uri) {

    try {
      return ImagePath.of(new File(uri));
    } catch (IllegalArgumentException _) {
      System.err.println("[ERROR] --- Invalid url: " + uri + " ---");
      return ImagePath.defaultResource();
    }

  }

  public static ImagePath defaultResource() {
    URL url = ImagePath.class.getResource(DEFAULT_IMAGE_RESOURCE);
    if (url == null)
      throw new IllegalStateException("Resource missing: " + DEFAULT_IMAGE_RESOURCE);
    try {
      return new ImagePath(url.toURI());
    } catch (URISyntaxException e) {
      System.err.println("[ERROR] --- Default resource not found, aborting ---");
      System.exit(-1);
      return null;
    }
  }

  public String getUri() {
    return this.uri.toString();
  }

  public boolean exists() {
    return Files.exists(Path.of(uri));
  }
}
