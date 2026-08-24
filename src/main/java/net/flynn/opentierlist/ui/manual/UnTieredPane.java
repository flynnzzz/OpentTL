package net.flynn.opentierlist.ui.manual;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import net.flynn.opentierlist.ConfigHolder;
import net.flynn.opentierlist.controller.GraphicsController;
import net.flynn.opentierlist.controller.TierListController;

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

    setPrefWidth(ConfigHolder.DEFAULT_UNTIERED_BAR_WIDTH);
    setPrefHeight(ConfigHolder.DEFAULT_UNRANKED_PANE_HEIGHT);

    unTieredPane.setPrefWidth(ConfigHolder.DEFAULT_UNTIERED_BAR_WIDTH);
    unTieredPane.setMinHeight(ConfigHolder.DEFAULT_UNRANKED_PANE_HEIGHT - 2);
    unTieredPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
    unTieredPane.setAlignment(Pos.TOP_LEFT);

    setContent(unTieredPane);
  }

  public void update() {
    graphicsController.updateItemViews(unTieredPane);
  }
}
