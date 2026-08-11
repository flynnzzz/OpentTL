package net.flynn.opentierlist.ui.manual;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
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
    this.setFitToHeight(false);

    this.setPrefWidth(ConfigHolder.DEFAULT_UNTIERED_BAR_WIDTH);
    this.setPrefHeight(ConfigHolder.DEFAULT_UNRANKED_PANE_HEIGHT);

    unTieredPane.setPrefWidth(ConfigHolder.DEFAULT_UNTIERED_BAR_WIDTH);

    unTieredPane.setMinHeight(ConfigHolder.DEFAULT_UNRANKED_PANE_HEIGHT - 2);
    unTieredPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
    unTieredPane.setAlignment(Pos.TOP_LEFT);

    this.setContent(unTieredPane);
  }

  public void update() {
    graphicsController.updateImages(unTieredPane);
  }

}
