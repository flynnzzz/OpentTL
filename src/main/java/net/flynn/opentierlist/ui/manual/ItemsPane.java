package net.flynn.opentierlist.ui.manual;

import java.util.List;

import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.controller.GraphicsController;
import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.ui.ConfigHolder;

/**
 *
 * @version 3.80
 * @since v1.2.5
 */
public class ItemsPane extends FlowPane {

  private final TierListController tierListController;
  private final GraphicsController graphicsController;
  private final boolean isUnTiered;
  private final int tierHash;

  public ItemsPane(int tierHash, TierListController tierListController, GraphicsController graphicsController) {

    this.tierListController = tierListController;
    this.graphicsController = graphicsController;
    this.tierHash = tierHash;

    final var tier = tierListController.getTierByHash(tierHash);

    final var items = tier.getTiered();
    this.isUnTiered = tier.equalsTier(Tier.UNTIERED);
    setupPane(graphicsController.loadImages(this, items));
  }

  public ItemsPane(TierListController tierListController, GraphicsController graphicsController) {
    this(Tier.UNTIERED.hashCode(), tierListController, graphicsController);
  }

  private void setupPane(List<ItemView> images) {
    this.getChildren().addAll(images);

    graphicsController.setFlowPaneBorder(this, ConfigHolder.DEFAULT_BAR_BORDER_COLOR);
    this.setPrefWidth(ConfigHolder.DEFAULT_TIERED_BAR_WIDTH);
    this.setMaxHeight(ConfigHolder.DEFAULT_BAR_MAX_HEIGHT);
    this.setMinHeight(ConfigHolder.DEFAULT_BAR_MIN_HEIGHT);

    this.setupDragAndDrop();
  }

  private void setupDragAndDrop() {
    this.setOnDragOver(event -> {
      if (event.getGestureSource() != event.getSource() && event.getDragboard().hasImage())
        event.acceptTransferModes(TransferMode.MOVE);
      event.consume();
    });

    this.setOnDragEntered(event -> {
      var sourceData = event.getGestureSource();
      if (sourceData instanceof ImageView && event.getDragboard().hasImage()) {
        graphicsController.setFlowPaneBorder(this, ConfigHolder.DEFAULT_BAR_HIGHLIGHT_COLOR);
      }
      event.consume();
    });

    this.setOnDragExited(event -> {
      var sourceData = event.getGestureSource();
      if (sourceData instanceof ImageView && event.getDragboard().hasImage()) {
        graphicsController.setFlowPaneBorder(this, ConfigHolder.DEFAULT_BAR_BORDER_COLOR);
      }
      event.consume();
    });

    this.setOnDragDropped(this::handleDragDropped);

    this.setOnDragDone(event -> {
      if (event.getTransferMode() == TransferMode.MOVE)
        graphicsController.updateImages(this);
      event.consume();
    });
  }

  private void handleDragDropped(DragEvent event) {
    boolean success = false;

    final Dragboard dragBoard = event.getDragboard();
    if (dragBoard.hasImage() && dragBoard.hasString()
        && event.getTarget() instanceof ItemsPane) {

      Integer itemHash = Integer.parseInt(dragBoard.getString());

      final var source = tierListController.getItemByHash(itemHash);
      tierListController.moveItem(source, tierListController.getTierByHash(tierHash));
      success = true;
    }

    if (success)
      graphicsController.updateImages(this);

    event.setDropCompleted(success);
    event.consume();
  }

  public boolean isUnTiered() {
    return isUnTiered;
  }

  public int getTierHash() {
    return tierHash;
  }
}
