package net.flynn.opentierlist.ui.manual;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import net.flynn.opentierlist.ui.ConfigHolder;
import net.flynn.opentierlist.controller.GraphicsController;

/**
 * 
 * @version 2.20
 * @since v1.2.5
 */
public class TieredPane extends ScrollPane {
  private final VBox tiersVBox;
  private final GraphicsController graphicsController;
  private ObservableList<TierBox> tierBoxList;

  public TieredPane(GraphicsController graphicsController) {
    this.graphicsController = graphicsController;
    this.tierBoxList = FXCollections.observableArrayList();
    this.tiersVBox = new VBox();
    setupPane();
  }

  private void setupPane() {

    tierBoxList = graphicsController.loadTiers();
    tiersVBox.getChildren().addAll(tierBoxList);

    this.setContent(tiersVBox);
    this.setFitToWidth(true);
    this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);

    graphicsController.setBorder(this);

    tiersVBox.setAlignment(Pos.CENTER);
    tiersVBox.setPadding(new Insets(ConfigHolder.DEFAULT_TIERS_VBOX_PADDING));

  }

  public void update() {
    tiersVBox.getChildren().clear();
    tierBoxList.clear();

    tierBoxList = graphicsController.loadTiers();
    tiersVBox.getChildren().addAll(tierBoxList);
  }

  public void hideEditButtons() {
    tiersVBox.getChildren().stream()
        .filter(b -> b instanceof TierBox)
        .map(h -> (TierBox) h)
        .forEach(TierBox::hideEditButton);
  }

  public void showEditButtons() {
    tiersVBox.getChildren().stream()
        .filter(b -> b instanceof TierBox)
        .map(h -> (TierBox) h)
        .forEach(TierBox::showEditButton);
  }

  public void setButtonThemes(ConfigHolder.Theme theme) {
    for (var box : tierBoxList)
      box.setButtonTheme(theme);
  }

}
