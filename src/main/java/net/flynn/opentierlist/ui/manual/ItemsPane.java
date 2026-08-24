package net.flynn.opentierlist.ui.manual;

import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.controller.GraphicsController;
import net.flynn.opentierlist.model.enums.DefaultTier;
import net.flynn.opentierlist.ConfigHolder;

public class ItemsPane extends FlowPane {
  private final TierListController tierListController;
  private final GraphicsController graphicsController;
  private final int tierHash;

  public ItemsPane(int tierHash, TierListController tierListController, GraphicsController graphicsController) {

    this.tierListController = tierListController;
    this.graphicsController = graphicsController;
    this.tierHash = tierHash;
    final var items = tierListController.getTierByHash(tierHash).getItems();
    final var images = graphicsController.constructItemViews(this, items);
    getChildren().addAll(images);
    graphicsController.setFlowPaneBorder(this, ConfigHolder.DEFAULT_BAR_BORDER_COLOR);
    setPrefWidth(ConfigHolder.DEFAULT_TIERED_BAR_WIDTH);
    setMaxHeight(Region.USE_COMPUTED_SIZE);
    setMinHeight(ConfigHolder.DEFAULT_BAR_MIN_HEIGHT);
    setupDragAndDrop();
  }

  public ItemsPane(TierListController tierListController, GraphicsController graphicsController) {
    this(DefaultTier.UNTIERED.value().hashCode(), tierListController, graphicsController);
  }

  private void setupDragAndDrop() {
    setOnDragOver(event -> {
      if (event.getGestureSource() != event.getSource() && event.getDragboard().hasImage())
        event.acceptTransferModes(TransferMode.MOVE);
      event.consume();
    });

    setOnDragEntered(event -> {
      var sourceData = event.getGestureSource();
      if (sourceData instanceof ImageView && event.getDragboard().hasImage()) {
        graphicsController.setFlowPaneBorder(this, ConfigHolder.DEFAULT_BAR_HIGHLIGHT_COLOR);
      }
      event.consume();
    });

    setOnDragExited(event -> {
      var sourceData = event.getGestureSource();
      if (sourceData instanceof ImageView && event.getDragboard().hasImage()) {
        graphicsController.setFlowPaneBorder(this, ConfigHolder.DEFAULT_BAR_BORDER_COLOR);
      }
      event.consume();
    });

    setOnDragDropped(this::handleDragDropped);

    setOnDragDone(event -> {
      if (event.getTransferMode() == TransferMode.MOVE)
        graphicsController.updateItemViews(this);
      event.consume();
    });
  }

  private void handleDragDropped(DragEvent event) {
    boolean success = false;

    final Dragboard dragBoard = event.getDragboard();
    if (dragBoard.hasImage() && dragBoard.hasString()
        && event.getTarget() instanceof ItemsPane) {

      final Integer itemHash = Integer.parseInt(dragBoard.getString());
      final var source = tierListController.getItemByHash(itemHash);
      tierListController.moveItem(source, tierListController.getTierByHash(tierHash));
      success = true;
    }

    if (success)
      graphicsController.updateItemViews(this);

    event.setDropCompleted(success);
    event.consume();
  }

  public int getTierHash() {
    return tierHash;
  }
}
