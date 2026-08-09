package net.flynn.opentierlist.model.models;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.flynn.opentierlist.model.enums.TierStringFormat;
import net.flynn.opentierlist.model.enums.TieredStatus;

/**
 * Class representing a single {@link Tier} entry.
 * <p>
 *
 * @author flynnz
 * @version 2.75
 * @since v0.0.0
 */
public class TierItem {
  private TieredStatus status;
  private String itemName;
  private ImagePath imagePath;

  private static long NEXT_ID = 1;
  private final long id;

  public static final String DEFAULT_ITEM_NAME = "New Item";

  private TierItem(TieredStatus status, String itemName, ImagePath imagePath) throws IllegalArgumentException {
    Objects.requireNonNull(itemName);
    Objects.requireNonNull(imagePath);
    if (itemName.isBlank())
      throw new IllegalArgumentException();

    this.status = status;
    this.itemName = itemName;
    this.id = NEXT_ID++;
    this.imagePath = imagePath;
  }

  /**
   * Constructs a {@link TierList} entry given the following parameters.
   * 
   * @param status      enum representing state
   * @param itemName the entry's name
   * @param uri         path to the entry image
   * 
   * @throws IllegalArgumentException if either name or image path are blank
   */
  public TierItem(TieredStatus status, String itemName, String uri)
      throws IllegalArgumentException  {
    if (uri.isBlank())
      throw new IllegalArgumentException();
    this(status, itemName, ImagePath.of(uri));
  }

  /**
   * Constructs a {@link TierList} entry given the following parameters.
   * 
   * @param itemName the entry's name
   * @param uri         the entry's image path
   * 
   * @throws IllegalArgumentException if either name or path are blank
   */
  public TierItem(String itemName, String uri) throws IllegalArgumentException  {
    this(TieredStatus.UNTIERED, itemName, uri);
  }

  /**
   * Constructs a {@link TierList} entry given only the name.
   * 
   * @param itemName the entry's name
   * 
   * @throws IllegalArgumentException if name is blank
   */
  public TierItem(String itemName) throws IllegalArgumentException {
    this(TieredStatus.UNTIERED, itemName, ImagePath.defaultResource());
  }

  /**
   * Constructs a 'default' {@link TierList} entry.
   */
  public TierItem() {
    this(DEFAULT_ITEM_NAME);
  }

  @JsonCreator
  public TierItem(
      @JsonProperty("name") String itemName,
      @JsonProperty("status") TieredStatus status,
      @JsonProperty("id") long id,
      @JsonProperty("imageUri") String imageUri) {
    this.itemName = itemName;
    this.status = status;
    this.id = id;
    try {
      this.imagePath = ImagePath.of(new URI(imageUri));
    } catch (URISyntaxException e) {
      this.imagePath = ImagePath.defaultResource();
    }
  }

  public TieredStatus getStatus() {
    return status;
  }

  @JsonIgnore
  public boolean isTiered() {
    return status.value();
  }

  /**
   * Method to mutate this {@link TierItem} instance's status
   * 
   * @param status to change to
   */
  public void changeTo(TieredStatus status) {
    Objects.requireNonNull(status);
    this.status = status;
  }

  public void setItemName(String itemName) throws IllegalArgumentException {
    Objects.requireNonNull(itemName);
    if (itemName.isBlank())
      throw new IllegalArgumentException();
    this.itemName = itemName;
  }

  public String getItemName() {
    return itemName;
  }

  public String getImageUri() {
    return this.imagePath.getUri();
  }

  public void updateImagePath() {
    this.imagePath = imagePath.exists() ? imagePath : ImagePath.defaultResource();
  }

  @Override
  public int hashCode() {
    return Objects.hash(imagePath, itemName, status, id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TierItem other)) {
      return false;
    }
    return Objects.equals(imagePath, other.imagePath)
        && Objects.equals(itemName, other.itemName)
        && Objects.equals(id, other.id)
        && status == other.status;
  }

  /**
   * Equals but ignoring instance difference
   *
   * @param tierItem tier element to compare to
   * @return true if names, resource paths and statuses match
   */
  public boolean equalsItem(TierItem tierItem) {
    if (this == tierItem) {
      return true;
    }
    if (!(tierItem instanceof TierItem other)) {
      return false;
    }
    return Objects.equals(imagePath, other.imagePath)
        && Objects.equals(itemName, other.itemName)
        && status == other.status;
  }

  /**
   * Returns the {@link TierItem} as a {@link String}
   * 
   * Format:
   * "TierElementName".
   * 
   * @return {@link String}
   */
  @Override
  public String toString() {
    return toString(TierStringFormat.COMPACT);
  }

  /**
   * Returns the {@link TierItem} as a {@link String}
   * 
   * Format COMPACT:
   * "name".
   * <p>
   * Format EXTENDED:
   * "name\n
   * status\n
   * imagePath".
   * 
   * @return {@link String}
   */
  public String toString(TierStringFormat format) {
    String res = null;

    switch (format) {
      case TierStringFormat.EXTENDED -> res = getItemName() + System.lineSeparator()
          + status + System.lineSeparator()
          + imagePath.getUri();
      case TierStringFormat.COMPACT -> res = getItemName();
    }
    return res;
  }
}
