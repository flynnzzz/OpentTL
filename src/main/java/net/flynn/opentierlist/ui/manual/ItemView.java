package net.flynn.opentierlist.ui.manual;

import javafx.event.EventTarget;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.persistence.DataHandler;
import net.flynn.opentierlist.controller.GraphicsController;

import java.util.Optional;

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
    this.setFitHeight(DataHandler.ConfigHolder.DEFAULT_CELL_SIZE);
    this.setFitWidth(DataHandler.ConfigHolder.DEFAULT_CELL_SIZE);
    setupEventHandlers();
  }

  private void setupEventHandlers() {

    setOnMouseClicked(mouseEvent -> {

      final var imageContextMenu = new ContextMenu();
      if (mouseEvent.getButton() == MouseButton.SECONDARY) {

        final var deleteImageMenu = new MenuItem("Delete");
        deleteImageMenu.setOnAction(_ -> {
          if (getUserData() instanceof TierItem item) {

            tierListController.removeItem(item);
            parent.getChildren().remove(this);
            graphicsController.updateItemViews(parent);

          }
        });

        if (getUserData() instanceof TierItem item && item.isTiered()) {

          final var unTierImageMenu = new MenuItem("UnTier");
          imageContextMenu.getItems().add(unTierImageMenu);
          unTierImageMenu.setOnAction(_ -> {

            tierListController.unTier(item);
            graphicsController.updateAll();
          });
        }

        imageContextMenu.getItems().add(deleteImageMenu);
        imageContextMenu.show(this, Side.RIGHT, 0, 0);

      }
    });

    setOnDragDetected(this::handleDragDetectedImage);

    setOnDragOver(event -> {
      if (event.getDragboard().hasImage())
        event.acceptTransferModes(TransferMode.MOVE);
      event.consume();
    });
    setOnDragEntered(event -> {
      if (event.getDragboard().hasImage())
        setFitHeight(DataHandler.ConfigHolder.DEFAULT_EXPANDED_IMAGE_SIZE);
      event.consume();
    });

    setOnDragExited(event -> {
      if (event.getTarget() instanceof ImageView && event.getSource() instanceof ImageView)
        setFitHeight(DataHandler.ConfigHolder.DEFAULT_CELL_SIZE);
      event.consume();
    });

    setOnDragDropped(this::handleDragDroppedImage);

    setOnDragDone(event -> {
      if (event.getTransferMode() == TransferMode.MOVE)
        graphicsController.updateItemViews(parent);
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
        && targetImage.getUserData() instanceof TierItem targetitem
        && !sourceItem.equals(targetitem)) {

      final Optional<Tier> potentialTargetTier = tierListController.getTierByItem(targetitem);

      potentialTargetTier.ifPresent(
          targetTier -> tierListController.insertItem(sourceItem, targetTier, targetitem));

      if (potentialTargetTier.isPresent())
        success = true;
    }
    if (success)
      graphicsController.updateItemViews(parent);

    event.setDropCompleted(success);
    event.consume();

  }
}
