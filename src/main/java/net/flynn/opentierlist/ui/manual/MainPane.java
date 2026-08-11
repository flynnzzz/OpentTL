package net.flynn.opentierlist.ui.manual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.persistence.ResourceHolder;
import net.flynn.opentierlist.ui.ConfigHolder;
import net.flynn.opentierlist.controller.GraphicsController;

/**
 * 
 * @version 2.95
 * @since v1.2.5
 */
public class MainPane extends BorderPane {

  private final TextField titleLabel;
  private final TieredPane tieredPane;
  private final UnTieredPane unTieredPane;
  private final Button addTierButton, addItemButton;
  private final MenuBar menuBar;
  private final Menu fileMenu, viewMenu;
  private final TierListController tierListController;
  private final GraphicsController graphicsController;

  private String oldTitle;

  public MainPane(TierListController tierListController, Stage mainStage) {
    this.tierListController = tierListController;
    this.titleLabel = new TextField(tierListController.getTierListName());

    this.graphicsController = new GraphicsController(tierListController);
    this.tieredPane = new TieredPane(graphicsController);
    this.unTieredPane = new UnTieredPane(tierListController, graphicsController);
    this.addTierButton = new Button();
    this.addItemButton = new Button();
    this.menuBar = new MenuBar();
    this.fileMenu = new Menu("File");
    this.viewMenu = new Menu("View");
    this.oldTitle = "";

    graphicsController.setMainStage(mainStage);
    graphicsController.setMainPane(this);
    graphicsController.setTieredPane(tieredPane);
    graphicsController.setUnTieredPane(unTieredPane);

    initPane();
  }

  public void initPane() {

    {
      final MenuItem menuNewTierList = new MenuItem("New...\t\t");
      menuNewTierList.setOnAction(_ -> {

        tierListController.setTierList(TierList.ofDefaultTiers());
        graphicsController.reloadImageCache();
        graphicsController.updateTierList();

      });

      final MenuItem menuSaveItem = new MenuItem("Save file...\t\t");
      menuSaveItem.setOnAction(graphicsController::saveTierList);

      final MenuItem menuSaveItemAs = new MenuItem("Save file to...\t\t");
      menuSaveItemAs.setOnAction(graphicsController::saveTierListAs);

      final MenuItem menuLoadItem = new MenuItem("Load\t\t");
      menuLoadItem.setOnAction(graphicsController::parseAndLoadTierList);

      final MenuItem menuExport = new MenuItem("Export as PNG...\t\t");
      menuExport.setOnAction(graphicsController::exportTierList);

      final MenuItem menuExportAs = new MenuItem("Export as PNG to...\t\t");
      menuExportAs.setOnAction(graphicsController::exportTierListAs);

      graphicsController.setTheme(ConfigHolder.Theme.LIGHT);

      final MenuItem menuLightTheme = new MenuItem("Light Theme\t\t"), menuDarkTheme = new MenuItem("Dark Theme\t\t");

      menuLightTheme.setOnAction(_ -> graphicsController.setTheme(ConfigHolder.Theme.LIGHT));
      menuDarkTheme.setOnAction(_ -> graphicsController.setTheme(ConfigHolder.Theme.DARK));

      fileMenu.getItems().addAll(menuNewTierList, menuSaveItem, menuSaveItemAs, menuLoadItem, menuExport, menuExportAs);
      viewMenu.getItems().addAll(menuLightTheme, menuDarkTheme);
      menuBar.getMenus().addAll(fileMenu, viewMenu);
      setTop(menuBar);
    }

    {
      addTierButton.setTooltip(new Tooltip("Add new Tier"));
      addItemButton.setTooltip(new Tooltip("Add new Item"));

      addTierButton.setFocusTraversable(false);
      addItemButton.setFocusTraversable(false);

      graphicsController.setGraphic(addItemButton, ResourceHolder.ADD_ITEM_BUTTON_ICON_LIGHT);
      graphicsController.setGraphic(addTierButton, ResourceHolder.ADD_TIER_BUTTON_ICON_LIGHT);

    }

    final HBox buttonsHBox = new HBox();
    {
      buttonsHBox.getChildren().addAll(addTierButton, addItemButton);

      buttonsHBox.setPadding(new Insets(
          ConfigHolder.DEFAULT_BUTTON_PADDING,
          ConfigHolder.DEFAULT_BUTTON_PADDING,
          ConfigHolder.DEFAULT_BUTTON_PADDING,
          ConfigHolder.DEFAULT_BUTTON_PADDING));

      buttonsHBox.setSpacing(ConfigHolder.DEFAULT_BUTTON_SPACING);
      buttonsHBox.setAlignment(Pos.BOTTOM_CENTER);
    }
    {
      titleLabel.setFocusTraversable(false);
      HBox titleBox = new HBox(titleLabel);
      titleBox.setPadding(new Insets(
          ConfigHolder.DEFAULT_TITLE_PADDING_TOP,
          ConfigHolder.DEFAULT_TITLE_PADDING_RIGHT,
          ConfigHolder.DEFAULT_TITLE_PADDING_BOTTOM,
          ConfigHolder.DEFAULT_TITLE_PADDING_LEFT));

      titleBox.setAlignment(Pos.BASELINE_CENTER);
      var centerBox = new VBox(titleBox, tieredPane, buttonsHBox);
      centerBox.setAlignment(Pos.CENTER);
      setCenter(centerBox);

      HBox unrankedBox = new HBox(unTieredPane);
      unrankedBox.setAlignment(Pos.CENTER);
      unrankedBox.setPadding(
          new Insets(
              ConfigHolder.DEFAULT_UNRANKED_PADDING_TOP,
              ConfigHolder.DEFAULT_UNRANKED_PADDING_RIGHT,
              ConfigHolder.DEFAULT_UNRANKED_PADDING_BOTTOM,
              ConfigHolder.DEFAULT_UNRANKED_PADDING_LEFT));

      setBottom(unrankedBox);
    }

    setupEventHandlers();
  }

  private void setupEventHandlers() {

    titleLabel.focusedProperty().addListener((_, _, changed) -> {
      if (changed)
        this.oldTitle = titleLabel.getText();
      else
        titleLabel.setText(oldTitle);
    });

    titleLabel.setOnAction(_ -> {

      if (!titleLabel.getText().isBlank()) {

        tierListController.setTierListName(titleLabel.getText());
        graphicsController.setStageTitle("OpenTL - " + titleLabel.getText());

        this.oldTitle = titleLabel.getText();

      }

      titleLabel.getScene().getRoot().requestFocus();

    });

    addTierButton.setOnAction(graphicsController::addTier);
    addItemButton.setOnAction(graphicsController::addItem);
  }

  public void setButtonGraphics(ConfigHolder.Theme theme) {
    switch (theme) {
      case LIGHT -> {
        graphicsController.setGraphic(addTierButton, ResourceHolder.ADD_TIER_BUTTON_ICON_LIGHT);
        graphicsController.setGraphic(addItemButton, ResourceHolder.ADD_ITEM_BUTTON_ICON_LIGHT);
      }
      case DARK -> {
        graphicsController.setGraphic(addTierButton, ResourceHolder.ADD_TIER_BUTTON_ICON_DARK);
        graphicsController.setGraphic(addItemButton, ResourceHolder.ADD_ITEM_BUTTON_ICON_DARK);
      }
    }
  }

  public void updateTitleLabel(String text) {
    titleLabel.setText(text);
    this.oldTitle = text;
  }

}
