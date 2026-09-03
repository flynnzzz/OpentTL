package net.flynn.opentierlist.persistence.impl;

import net.flynn.opentierlist.persistence.ImagePath;
import net.flynn.opentierlist.persistence.ResourceHolder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public record DesktopImagePath(URI uri) implements ImagePath {

  public static DesktopImagePath of(File file) throws IllegalArgumentException {
    if (file != null && file.exists()) {
      return new DesktopImagePath(file.toURI());
    }
    return defaultResource();
  }

  public static DesktopImagePath of(String uri) {

    try {
      return DesktopImagePath.of(new URI(uri));
    } catch (URISyntaxException _) {
      System.err.println("[INFO] --- Invalid URL: " + uri + ", loading default resource ---");
      return defaultResource();
    }
  }

  public static DesktopImagePath of(URI uri) {

    try {
      return DesktopImagePath.of(new File(uri));
    } catch (IllegalArgumentException _) {
      System.err.println("[INFO] --- Invalid URL: " + uri + ", loading default resource ---");
      return DesktopImagePath.defaultResource();
    }
  }

  public static DesktopImagePath defaultResource() {
    final URL url = DesktopImagePath.class.getResource(ResourceHolder.DEFAULT_ITEM_IMAGE);
    if (url == null)
      throw new IllegalStateException("Resource missing: " + ResourceHolder.DEFAULT_ITEM_IMAGE);
    try {
      return new DesktopImagePath(url.toURI());
    } catch (URISyntaxException _) {
      System.err.println("[ERROR] --- Default resource not found, aborting ---");
      System.exit(-1);
      return null;
    }
  }

  @Override
  public String getUriAsString() {
    return uri.toString();
  }

  @Override
  public boolean exists() {
    return Files.exists(Path.of(uri));
  }
}
