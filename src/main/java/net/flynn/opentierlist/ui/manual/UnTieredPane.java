package net.flynn.opentierlist.ui.manual;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.persistence.DataHandler;
import net.flynn.opentierlist.controller.GraphicsController;

public class UnTieredPane extends ScrollPane {
  private final ItemsPane unTieredPane;
  private final GraphicsController graphicsController;

  public UnTieredPane(TierListController controller, GraphicsController graphicsController) {
    this.graphicsController = graphicsController;
    this.unTieredPane = new ItemsPane(controller, graphicsController);

    setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
    setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

    setFitToWidth(true);
    setFitToHeight(false);

    setPrefWidth(DataHandler.ConfigHolder.DEFAULT_UNTIERED_BAR_WIDTH);
    setPrefHeight(DataHandler.ConfigHolder.DEFAULT_UNRANKED_PANE_HEIGHT);

    unTieredPane.setPrefWidth(DataHandler.ConfigHolder.DEFAULT_UNTIERED_BAR_WIDTH);
    unTieredPane.setMinHeight(DataHandler.ConfigHolder.DEFAULT_UNRANKED_PANE_HEIGHT - 2);
    unTieredPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
    unTieredPane.setAlignment(Pos.TOP_LEFT);

    setContent(unTieredPane);
  }

  public void update() {
    graphicsController.updateItemViews(unTieredPane);
  }
}
