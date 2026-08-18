package net.flynn.opentierlist.controller;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.persistence.DataHandler;
import net.flynn.opentierlist.ui.manual.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicsController {

  private final TierListController tierListController;

  private Stage mainStage;
  private MainPane mainPane;
  private TieredPane tieredPane;
  private UnTieredPane unTieredPane;
  private final FileChooser tierListFileChooser;
  private final Map<Integer, Image> imageCache;

  private void setTieredBorder(String color) {

    if (tieredPane == null || unTieredPane == null) {
      System.err.println(
          "[ERROR] --- Cannot set borders: Controller is missing the necessary instance ---");
      return;
    }

    setBorder(tieredPane, color);
  }

  private FileChooser createSaveChooser(
      String title, FileChooser.ExtensionFilter filter, File initialDir) {

    final var saveChooser = new FileChooser();

    saveChooser.setTitle(title);
    saveChooser.getExtensionFilters().addAll(filter);
    saveChooser.setInitialDirectory(initialDir);

    saveChooser.setInitialFileName(
        tierListController.getTierListName() +
            filter.getExtensions().getFirst().replace("*", ""));

    return saveChooser;
  }

  public GraphicsController(TierListController tierListController) {
    this.tierListController = tierListController;
    this.tierListFileChooser = new FileChooser();

    tierListFileChooser.setTitle("Load tier list");
    tierListFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tier List json files", "*.tson"));

    this.imageCache = new HashMap<>();
    System.err.println("[INFO] --- Instantiating new image cache ---");

  }

  public static void alert(Alert.AlertType type, String title, String message) {
    final var alert = new Alert(type);

    alert.setTitle(title);
    alert.setHeaderText(message);
    alert.setContentText("");

    alert.show();
  }

  public ObservableList<TierBox> constructTierBoxes() {
    return FXCollections.observableArrayList(
        tierListController
            .getTiers().stream()
            .map(t -> new TierBox(t.hashCode(), tierListController, this))
            .toList());
  }

  public ObservableList<ItemView> constructItemViews(ItemsPane flowPane, List<TierItem> items) {

    final ObservableList<ItemView> images = FXCollections.observableArrayList();

    items.forEach(item -> {

      final String url = item.getImageUriOrDefault();

      Image img = imageCache.get(item.hashCode());

      if (img == null) {
        img = new Image(url,
            DataHandler.ConfigHolder.DEFAULT_CELL_SIZE,
            DataHandler.ConfigHolder.DEFAULT_CELL_SIZE,
            false,
            false);
        imageCache.put(item.hashCode(), img);
      }

      final var imageViewer = new ItemView(
          img,
          tierListController,
          this,
          flowPane);
      imageViewer.setUserData(item);
      images.add(imageViewer);

    });

    return images;
  }

  public void updateItemViews(ItemsPane flowPane) {

    final List<TierItem> items = tierListController
        .getTierByHash(flowPane.getTierHash())
        .getItems();

    imageCache.keySet()
        .removeIf(hash -> !tierListController.itemExists(hash));

    final List<ItemView> images = constructItemViews(flowPane, items);

    flowPane.getChildren().clear();
    flowPane.getChildren().addAll(images);

  }

  public void updateTiered() {

    if (tieredPane == null) {
      System.err.println(
          "[ERROR] --- Cannot update Tiers: Controller is missing the necessary instance ---");
      return;
    }
    tieredPane.update();
  }

  public void updateUnTiered() {
    if (unTieredPane == null) {
      System.err.println(
          "[ERROR] --- Cannot update untiered: Controller is missing the necessary instance ---");
      return;
    }
    unTieredPane.update();
  }

  public void updateAll() {

    if (mainPane == null) {
      System.err.println(
          "[ERROR] --- Cannot update Tier List: Controller is missing a MainPane instance ---");
      return;
    }

    if (mainStage == null) {
      System.err.println(
          "[ERROR] --- Cannot update Tier List: Controller is missing a Stage instance ---");
      return;
    }

    final String newTitle = tierListController.getTierListName();
    mainPane.updateTitleLabel(newTitle);
    mainStage.setTitle("OpenTL - " + newTitle);

    updateTiered();
    updateUnTiered();
  }

  public void addItemHandle(ActionEvent ignoredEvent) {

    if (mainStage == null) {
      System.err.println(
          "[ERROR] --- Cannot add item: Controller is missing a Stage instance ---");
      return;
    }

    final var imageFileChooser = new FileChooser();

    imageFileChooser.setTitle("Select file");
    imageFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
        "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
    imageFileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Pictures"));
    imageFileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));

    final List<File> files = imageFileChooser.showOpenMultipleDialog(mainStage);
    if (files == null || files.isEmpty()) {
      System.err.println("[INFO] --- No file selected ---");
      return;
    }

    files.forEach(selectedFile -> {
      if (selectedFile != null && selectedFile.exists()) {
        try {
          final var el = new TierItem(selectedFile.getName(), selectedFile.toURI().toString());
          tierListController.addUnTiered(el);
        } catch (IllegalArgumentException _) {
          System.err.println("[ERROR] --- Resource not found, aborting ---");
          System.exit(-1);
        }
      }
    });

    updateAll();

  }

  public void addTierHandle(ActionEvent ignoredEvent) {
    tierListController.addDefaultTier();
    updateTiered();
  }

  public void parseAndLoadTierList(ActionEvent ignoredEvent) {

    if (mainStage == null) {
      System.err.println(
          "[ERROR] --- Cannot parse and load Tier List: Controller is missing a Stage instance ---");
      return;
    }

    final File toParse = tierListFileChooser.showOpenDialog(mainStage);

    if (toParse == null) {
      System.err.println("[INFO] --- No file selected ---");
      return;
    }

    final var parsedTier = tierListController.parseTierList(toParse);

    if (parsedTier.isPresent()) {
      tierListController.setTierList(parsedTier.get());
      mainStage.requestFocus();
      reloadImageCache();
      updateAll();
    }

  }

  public void saveHandle(ActionEvent ignoredEvent) {

    if (tierListController.saveTierList()) {
      GraphicsController.alert(
          Alert.AlertType.INFORMATION, "Save successful",
          tierListController.getTierListName() + " was saved successfully");
    } else {
      GraphicsController.alert(
          Alert.AlertType.ERROR, "Save unsuccessful",
          tierListController.getTierListName() + " could not be saved successfully");
    }
  }

  public void saveAsPathHandle(ActionEvent ignoredEvent) {

    if (mainStage == null) {
      System.err.println(
          "[ERROR] --- Cannot save Tier List: Controller is missing a Stage instance ---");
      return;
    }

    final var saveChooser = createSaveChooser(
        "Save Tier List as...",
        new FileChooser.ExtensionFilter("Tier List json files", "*.tson"),
        new File(System.getProperty("user.home") + "/Documents"));

    final var file = saveChooser.showSaveDialog(mainStage);
    if (file == null) {
      System.err.println("[INFO] --- No file selected ---");
      return;
    }

    if (tierListController.saveTierList(file.toPath())) {
      GraphicsController.alert(
          Alert.AlertType.INFORMATION, "Save successful",
          tierListController.getTierListName() + " was saved successfully");
    } else {
      GraphicsController.alert(
          Alert.AlertType.ERROR, "Save unsuccessful",
          tierListController.getTierListName() + " could not be saved successfully");
    }

  }

  public void exportHandle(ActionEvent ignoredEvent) {

    if (tieredPane == null) {
      System.err.println(
          "[ERROR] --- Cannot export Tier List: Controller is missing the necessary instance ---");
      return;
    }

    if (tierListController.exportTierList(tieredPane)) {
      GraphicsController.alert(
          Alert.AlertType.INFORMATION, "Export successful",
          tierListController.getTierListName() + " was exported successfully");
    } else {
      GraphicsController.alert(
          Alert.AlertType.ERROR, "Export unsuccessful",
          tierListController.getTierListName() + " could not be exported successfully");
    }
  }

  public void exportAsPathHandle(ActionEvent ignoredEvent) {

    if (mainStage == null) {
      System.err.println(
          "[ERROR] --- Cannot export Tier List: Controller is missing a Stage instance ---");
      return;
    }

    if (tieredPane == null) {
      System.err.println(
          "[ERROR] --- Cannot export Tier List: Controller is missing the necessary instance ---");
      return;
    }

    final var saveChooser = createSaveChooser(
        "Export Tier List as...",
        new FileChooser.ExtensionFilter("PNG images", "*.png"),
        new File(System.getProperty("user.home") + "/Pictures"));

    final var file = saveChooser.showSaveDialog(mainStage);
    if (file == null) {
      System.err.println("[INFO] --- No file selected ---");
      return;
    }
    if (tierListController.exportTierList(tieredPane, file.toPath())) {
      GraphicsController.alert(
          Alert.AlertType.INFORMATION, "Export successful",
          tierListController.getTierListName() + " was exported successfully");
    } else {
      GraphicsController.alert(
          Alert.AlertType.ERROR, "Export unsuccessful",
          tierListController.getTierListName() + " could not be exported successfully");
    }

  }

  public void reloadImageCache() {
    System.err.println("[INFO] --- Reloading image cache ---");
    imageCache.clear();
  }

  public void setStageTitle(String string) {
    if (mainStage == null) {
      System.err.println(
          "[ERROR] --- Cannot set stage title: Controller is missing a Stage instance ---");
      return;
    }
    mainStage.setTitle(string);
  }

  public void setBorder(ScrollPane pane, String color) {
    pane.setBorder(new Border(
        new BorderStroke(
            Paint.valueOf(color),
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            BorderWidths.DEFAULT)));
  }

  public void updateBorders(ScrollPane pane) {

    final var color = DataHandler.ConfigHolder.getCurrentTheme() == DataHandler.ConfigHolder.Theme.LIGHT
        ? DataHandler.ConfigHolder.DEFAULT_ACCENT_COLOR_LIGHT
        : DataHandler.ConfigHolder.DEFAULT_ACCENT_COLOR_DARK;

    final var border = new Border(
        new BorderStroke(
            Paint.valueOf(color),
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            BorderWidths.DEFAULT));
    pane.setBorder(border);

  }

  public void setGraphic(Button button, String resource) {
    try {
      var imageURI = getClass().getResource(resource);
      if (imageURI == null)
        throw new URISyntaxException("imageURI", "[ERROR] --- add tier button resource not found, exiting ---");
      button.setGraphic(new ImageView(new Image(imageURI.toURI().toString())));

    } catch (URISyntaxException e) {
      System.err.println(e.getReason());
      System.exit(-1);
    }
  }

  public void setTheme(DataHandler.ConfigHolder.Theme theme) {
    if (mainPane == null) {
      System.err.println(
          "[ERROR] --- Cannot set theme: Controller is missing a MainPane instance ---");
      return;
    }

    if (tieredPane == null) {
      System.err.println(
          "[ERROR] --- Cannot set theme: Controller is missing the necessary instance ---");
      return;
    }

    DataHandler.ConfigHolder.setCurrentTheme(theme);

    final var lightTheme = new NordLight().getUserAgentStylesheet();
    final var darkTheme = new NordDark().getUserAgentStylesheet();

    switch (theme) {
      case LIGHT -> {
        Application.setUserAgentStylesheet(lightTheme);
        mainPane.setButtonGraphics(DataHandler.ConfigHolder.Theme.LIGHT);
        tieredPane.setButtonThemes(DataHandler.ConfigHolder.Theme.LIGHT);
        setTieredBorder(DataHandler.ConfigHolder.DEFAULT_ACCENT_COLOR_LIGHT);
      }
      case DARK -> {
        Application.setUserAgentStylesheet(darkTheme);
        mainPane.setButtonGraphics(DataHandler.ConfigHolder.Theme.DARK);
        tieredPane.setButtonThemes(DataHandler.ConfigHolder.Theme.DARK);
        setTieredBorder(DataHandler.ConfigHolder.DEFAULT_ACCENT_COLOR_DARK);
      }
    }
  }

  public void setFlowPaneBorder(FlowPane flowPane, String color) {
    flowPane.setBorder(new Border(
        new BorderStroke(
            Paint.valueOf(color),
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            BorderWidths.DEFAULT)));
  }

  public void setMainStage(Stage mainStage) {
    this.mainStage = mainStage;
  }

  public void setMainPane(MainPane mainPane) {
    this.mainPane = mainPane;
  }

  public void setTieredPane(TieredPane tieredPane) {
    this.tieredPane = tieredPane;
  }

  public void setUnTieredPane(UnTieredPane unTieredPane) {
    this.unTieredPane = unTieredPane;
  }

  public Stage getMainStage() {
    return mainStage;
  }

}
