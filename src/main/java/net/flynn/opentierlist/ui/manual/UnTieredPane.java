package net.flynn.opentierlist.ui.manual;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.ui.ConfigHolder;
import net.flynn.opentierlist.controller.GraphicsController;

/**
 * 
 * @version 2.20
 * @since v1.2.5
 */
public class UnTieredPane extends ScrollPane {
  private final ItemsPane unTieredPane;
  private final GraphicsController graphicsController;

  public UnTieredPane(TierListController controller, GraphicsController graphicsController) {

    this.graphicsController = graphicsController;
    this.unTieredPane = new ItemsPane(controller, graphicsController);
    setupPane();
  }

  private void setupPane() {
    this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
    this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

    this.setFitToWidth(true);
    this.setFitToHeight(true);
    this.setPrefWidth(ConfigHolder.DEFAULT_BAR_WIDTH);

    this.setPrefHeight(ConfigHolder.DEFAULT_UNRANKED_PANE_MIN_HEIGHT);

    graphicsController.setBorder(this);

    unTieredPane.setPrefWidth(ConfigHolder.DEFAULT_BAR_WIDTH);

    unTieredPane.setMaxHeight(ConfigHolder.DEFAULT_UNRANKED_PANE_MAX_HEIGHT);
    unTieredPane.setMinHeight(ConfigHolder.DEFAULT_UNRANKED_PANE_MIN_HEIGHT);
    unTieredPane.setAlignment(Pos.TOP_LEFT);

    this.setContent(unTieredPane);
  }

  public void update() {
    graphicsController.updateImages(unTieredPane);
  }

}
