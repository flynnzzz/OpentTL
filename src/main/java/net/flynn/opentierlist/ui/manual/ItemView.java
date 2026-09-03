package net.flynn.opentierlist.ui.manual;

import java.util.Optional;

import javafx.event.EventTarget;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import net.flynn.opentierlist.ConfigHolder;
import net.flynn.opentierlist.controller.GraphicsController;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.model.models.TierItem;

public class ItemView extends ImageView {
  private final TierListController tierListController;
  private final GraphicsController graphicsController;
  private final ItemsPane parent;

  public ItemView(
      Image image, TierListController tierListController, GraphicsController graphicsController, ItemsPane parent) {
    super(image);
    this.tierListController = tierListController;
    this.graphicsController = graphicsController;
    this.parent = parent;
    this.setFitHeight(ConfigHolder.DEFAULT_CELL_SIZE);
    this.setFitWidth(ConfigHolder.DEFAULT_CELL_SIZE);
    setupEventHandlers();
  }

  private void setupEventHandlers() {

    setOnMouseClicked(mouseEvent -> graphicsController.rightClickImageHandle(mouseEvent, this));

    setOnDragDetected(this::handleDragDetectedImage);

    setOnDragOver(event -> {
      if (event.getDragboard().hasImage())
        event.acceptTransferModes(TransferMode.MOVE);
      event.consume();
    });

    setOnDragEntered(event -> {
      if (event.getDragboard().hasImage())
        setFitHeight(ConfigHolder.DEFAULT_EXPANDED_IMAGE_SIZE);
      event.consume();
    });

    setOnDragExited(event -> {
      if (event.getTarget() instanceof ImageView && event.getSource() instanceof ImageView)
        setFitHeight(ConfigHolder.DEFAULT_CELL_SIZE);
      event.consume();
    });

    setOnDragDropped(this::handleDragDroppedImage);

    setOnDragDone(event -> {
      if (event.getTransferMode() == TransferMode.MOVE)
        graphicsController.constructorInstance().updateItemViews(parent);
      event.consume();
    });
  }

  private void handleDragDetectedImage(MouseEvent event) {

    if (!(event.getSource() instanceof ImageView sourceImage))
      return;

    final Dragboard dragBoard = sourceImage.startDragAndDrop(TransferMode.MOVE);
    final var content = new ClipboardContent();

    if (sourceImage.getUserData() instanceof TierItem sourceItem) {

      content.putImage(sourceImage.getImage());
      content.putString(String.valueOf(sourceItem.hashCode()));

      dragBoard.setContent(content);
    }
    event.consume();
  }

  private void handleDragDroppedImage(DragEvent event) {

    boolean success = false;

    Dragboard dragBoard = event.getDragboard();
    String itemHash = dragBoard.getString();

    final var sourceItem = tierListController.getItemByHash(Integer.parseInt(itemHash));

    final EventTarget eventTarget = event.getTarget();
    if (dragBoard.hasImage() && dragBoard.hasString()
        && eventTarget instanceof ImageView targetImage
        && targetImage.getUserData() instanceof TierItem targetItem
        && !sourceItem.equals(targetItem)) {

      final Optional<Tier> potentialTargetTier = tierListController.getTierByItem(targetItem);

      potentialTargetTier.ifPresent(
          targetTier -> tierListController.insertItem(sourceItem, targetTier, targetItem));

      if (potentialTargetTier.isPresent())
        success = true;
    }
    if (success)
      graphicsController.constructorInstance().updateItemViews(parent);

    event.setDropCompleted(success);
    event.consume();

  }

  public ItemsPane parentInstance() {
    return parent;
  }
}
