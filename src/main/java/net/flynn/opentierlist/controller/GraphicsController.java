package net.flynn.opentierlist.controller;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.flynn.opentierlist.ConfigHolder;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.ui.manual.MainPane;
import net.flynn.opentierlist.ui.manual.TieredPane;
import net.flynn.opentierlist.ui.manual.UnTieredPane;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class GraphicsController {

  private final TierListController tierListController;
  private final GraphicsConstructor graphicsConstructor;

  private Stage mainStage;
  private MainPane mainPane;
  private TieredPane tieredPane;
  private UnTieredPane unTieredPane;

  private final FileChooser tierListFileChooser;

  private FileChooser saveChooser(
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
    this.graphicsConstructor = new GraphicsConstructor(tierListController, this);

    tierListFileChooser.setTitle("Load tier list");
    tierListFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tier List json files", "*.tson"));
    tierListFileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Documents"));

    System.err.println("[INFO] --- Instantiating new image cache ---");

  }

  public static void alert(Alert.AlertType type, String title, String message) {
    final var alert = new Alert(type);

    alert.setTitle(title);
    alert.setHeaderText(message);
    alert.setContentText("");

    alert.show();
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

    graphicsConstructor.updateAll();

  }

  public void addTierHandle(ActionEvent ignoredEvent) {
    tierListController.addDefaultTier();
    graphicsConstructor.updateTiered();
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
      graphicsConstructor.reloadImageCache();
      graphicsConstructor.updateAll();
      alert(
          AlertType.CONFIRMATION, "Load Tier List", "File '" + toParse + "' was loaded correctly");
    } else {
      alert(AlertType.ERROR, "Load Tier List", "File '" + toParse + "' could not be loaded");
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

    final var saveChooser = saveChooser(
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

    final var saveChooser = saveChooser(
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

  public void changeTheme(ConfigHolder.Theme theme) {
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

    ConfigHolder.setCurrentTheme(theme);

    String color = null;
    switch (theme) {
      case LIGHT -> {
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
        mainPane.setButtonGraphics(ConfigHolder.Theme.LIGHT);
        tieredPane.setButtonThemes(ConfigHolder.Theme.LIGHT);
        color = ConfigHolder.DEFAULT_ACCENT_COLOR_LIGHT;
      }
      case DARK -> {
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        mainPane.setButtonGraphics(ConfigHolder.Theme.DARK);
        tieredPane.setButtonThemes(ConfigHolder.Theme.DARK);
        color = ConfigHolder.DEFAULT_ACCENT_COLOR_DARK;
      }
    }
    tieredPane.setBorder(new Border(
            new BorderStroke(
                    Paint.valueOf(color),
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT)));
  }

  public void changeFlowPaneBorder(FlowPane flowPane, String color) {
    flowPane.setBorder(new Border(
            new BorderStroke(
                    Paint.valueOf(color),
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT)));
  }


  public static void setButtonGraphic(Button button, String resource) {
    try {
      final var imageURI = GraphicsController.class.getResource(resource);
      if (imageURI == null)
        throw new URISyntaxException("imageURI", "[ERROR] --- add tier button resource not found, exiting ---");
      button.setGraphic(new ImageView(new Image(imageURI.toURI().toString())));

    } catch (URISyntaxException e) {
      System.err.println(e.getReason());
      System.exit(-1);
    }
  }

  public void attachMainStage(Stage mainStage) {
    this.mainStage = mainStage;
  }

  public void attachMainPane(MainPane mainPane) {
    this.mainPane = mainPane;
  }

  public void attachTieredPane(TieredPane tieredPane) {
    this.tieredPane = tieredPane;
  }

  public void attachUnTieredPane(UnTieredPane unTieredPane) {
    this.unTieredPane = unTieredPane;
  }

  public Stage getMainStage() {
    return mainStage;
  }

  public MainPane getMainPane() { return mainPane; }

  public TieredPane getTieredPane() { return tieredPane; }

  public UnTieredPane getUnTieredPane() { return unTieredPane; }

  public GraphicsConstructor constructorInstance() { return graphicsConstructor; }

}
