package net.flynn.opentierlist.ui.manual;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import net.flynn.opentierlist.ConfigHolder;
import net.flynn.opentierlist.controller.GraphicsController;

public class TieredPane extends ScrollPane {
  private final VBox tiersVBox;
  private final GraphicsController graphicsController;
  private ObservableList<TierBox> tierBoxList;

  public TieredPane(GraphicsController graphicsController) {
    this.graphicsController = graphicsController;
    this.tierBoxList = FXCollections.observableArrayList();
    this.tiersVBox = new VBox();
    tierBoxList = graphicsController.constructorInstance().constructTierBoxes();
    tiersVBox.getChildren().addAll(tierBoxList);

    setContent(tiersVBox);
    setFitToWidth(true);
    setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    setHbarPolicy(ScrollBarPolicy.AS_NEEDED);

    final var color = ConfigHolder.getCurrentTheme() == ConfigHolder.Theme.LIGHT
            ? ConfigHolder.DEFAULT_ACCENT_COLOR_LIGHT
            : ConfigHolder.DEFAULT_ACCENT_COLOR_DARK;

    final var border = new Border(
            new BorderStroke(
                    Paint.valueOf(color),
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT));
    setBorder(border);

    tiersVBox.setAlignment(Pos.CENTER);
    tiersVBox.setPadding(new Insets(ConfigHolder.DEFAULT_TIERS_VBOX_PADDING));
  }

  public void update() {
    tiersVBox.getChildren().clear();
    tierBoxList.clear();

    tierBoxList = graphicsController.constructorInstance().constructTierBoxes();
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
