package net.flynn.opentierlist.model.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.flynn.opentierlist.model.enums.TierStringFormat;
import net.flynn.opentierlist.model.enums.TieredStatus;
import net.flynn.opentierlist.persistence.ImagePath;
import net.flynn.opentierlist.persistence.ResourceHolder;

public class TierItem {
  private TieredStatus status;
  private String itemName;
  private final ImagePath imagePath;

  private static long NEXT_ID = 1;
  private final long id;

  public static final String DEFAULT_ITEM_NAME = "New Item";

  private TierItem(TieredStatus status, String itemName, ImagePath imagePath) throws IllegalArgumentException {
    Objects.requireNonNull(itemName, "[ERROR] --- Null pointer exception: item name ---");
    Objects.requireNonNull(imagePath, "[ERROR] --- Null pointer exception: item image path ---");
    if (itemName.isBlank())
      throw new IllegalArgumentException("[ERROR] --- Item name cannot be blank ---");

    this.status = status;
    this.itemName = itemName;
    this.imagePath = imagePath;
    this.id = NEXT_ID++;
  }

  public TierItem(TieredStatus status, String itemName, String uri)
      throws IllegalArgumentException {
    if (uri.isBlank())
      throw new IllegalArgumentException("[DEBUG] --- Item URI cannot be blank ---");
    this(status, itemName, ImagePath.of(uri));
  }

  public TierItem(String itemName, String uri) throws IllegalArgumentException {
    this(TieredStatus.UNTIERED, itemName, uri);
  }

  public TierItem(String itemName) throws IllegalArgumentException {
    this(TieredStatus.UNTIERED, itemName, ImagePath.defaultResource());
  }

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
    this.imagePath = ImagePath.of(imageUri);
  }

  @JsonIgnore
  public boolean isTiered() {
    return status.value();
  }

  public void changeTo(TieredStatus status) {
    this.status = Objects.requireNonNull(status);
  }

  public void setItemName(String itemName) throws IllegalArgumentException {
    Objects.requireNonNull(itemName);
    if (itemName.isBlank())
      throw new IllegalArgumentException("[ERROR] -- Cannot set blank item name ---");
    this.itemName = itemName;
  }

  public String getItemName() {
    return itemName;
  }

  public String getImageUri() {
    return imagePath.getUriAsString();
  }

  @JsonIgnore
  public String getImageUriOrDefault() {
    return imagePath.exists() ? imagePath.getUriAsString()
        : Objects.requireNonNull(Objects.requireNonNull(getClass().getResource(ResourceHolder.DEFAULT_ITEM_IMAGE))).toString();
  }

  public TieredStatus getStatus() {
    return status;
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

  @Override
  public String toString() {
    return toString(TierStringFormat.COMPACT);
  }

  public String toString(TierStringFormat format) {
    String res = null;

    switch (format) {
      case TierStringFormat.EXTENDED -> res = getItemName() + System.lineSeparator()
          + status + System.lineSeparator()
          + imagePath.getUriAsString();
      case TierStringFormat.COMPACT -> res = getItemName();
    }
    return res;
  }
}
