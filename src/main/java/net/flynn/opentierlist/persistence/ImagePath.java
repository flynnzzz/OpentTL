package net.flynn.opentierlist.persistence;

import net.flynn.opentierlist.MainApplication;
import net.flynn.opentierlist.persistence.impl.DesktopImagePath;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public interface ImagePath {
  static ImagePath of(File file) throws IllegalArgumentException {
    if (file != null && file.exists()) {
      return MainApplication.isOnAndroid()
          ? null
          : DesktopImagePath.of(file.toURI());
    }
    return ImagePath.defaultResource();
  }

  static ImagePath of(String uri) {

    try {
      return ImagePath.of(new URI(uri));
    } catch (URISyntaxException _) {
      System.err.println("[INFO] --- Invalid URL: " + uri + ", loading default resource ---");
      return ImagePath.defaultResource();
    }
  }

  static ImagePath of(URI uri) {

    try {
      return ImagePath.of(new File(uri));
    } catch (IllegalArgumentException _) {
      System.err.println("[INFO] --- Invalid URL: " + uri + ", loading default resource ---");
      return ImagePath.defaultResource();
    }
  }

  static ImagePath defaultResource() {
    final URL url = ImagePath.class.getResource(ResourceHolder.DEFAULT_ITEM_IMAGE);
    if (url == null)
      throw new IllegalStateException("Resource missing: " + ResourceHolder.DEFAULT_ITEM_IMAGE);
    try {
      return MainApplication.isOnAndroid()
          ? null
          : new DesktopImagePath(url.toURI());
    } catch (URISyntaxException _) {
      System.err.println("[ERROR] --- Default resource not found, aborting ---");
      System.exit(-1);
      return null;
    }
  }

  String getUriAsString();

  boolean exists();
}
