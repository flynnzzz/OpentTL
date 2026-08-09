package net.flynn.opentierlist.ui.manual;

import java.net.URISyntaxException;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.persistence.ResourceHolder;
import net.flynn.opentierlist.ui.ConfigHolder;
import net.flynn.opentierlist.controller.GraphicsController;

/**
 * 
 * @version 3.60
 * @since v1.2.5
 */
public class TierBox extends HBox {

  private final TextField tierNameLabel;
  private final ItemsPane tieredPane;
  private final Button editTierButton;
  private Stage colorStage;
  private ColorPicker colorPicker;
  private Button confirmColor;
  private final TierListController tierListController;
  private final GraphicsController graphicsController;
  private final int tierHash;
  private String oldTextValue;

  private ContextMenu colorPickerMenu;
  private MenuItem deleteOption;
  private MenuItem duplicateOption;
  private MenuItem colorOption;

  public TierBox(int tierHash, TierListController tierListController, GraphicsController graphicsController) {
    this.tierListController = tierListController;
    this.graphicsController = graphicsController;
    this.tierHash = tierHash;
    this.oldTextValue = "";

    this.tierNameLabel = new TextField(tierListController.getTierByHash(tierHash).getName());
    this.editTierButton = new Button();
    this.tieredPane = new ItemsPane(tierHash, tierListController, graphicsController);
    setupPane();
  }

  private void setupPane() {
    this.getChildren().addAll(tierNameLabel, tieredPane, editTierButton);

    {
      tierNameLabel.setEditable(true);
      tierNameLabel.setFocusTraversable(false);
      tierNameLabel.setAlignment(Pos.CENTER);
      tierNameLabel.setPrefSize(ConfigHolder.DEFAULT_CELL_SIZE, ConfigHolder.DEFAULT_CELL_SIZE);

      editTierButton.setAlignment(Pos.CENTER);
      editTierButton.setFocusTraversable(false);

      this.setTierNameLabelBackground();
      this.setTierNameLabelBorder(ConfigHolder.DEFAULT_BAR_BORDER_COLOR);

      this.setAlignment(Pos.CENTER);

      this.setSpacing(ConfigHolder.DEFAULT_TIER_SPACING);
      this.setPadding(new Insets(
          ConfigHolder.DEFAULT_TIER_PADDING_TOP,
          ConfigHolder.DEFAULT_TIER_PADDING_RIGHT,
          ConfigHolder.DEFAULT_TIER_PADDING_BOTTOM,
          ConfigHolder.DEFAULT_TIER_PADDING_LEFT));
    }

    colorPickerMenu = new ContextMenu();
    deleteOption = new MenuItem("Delete");
    duplicateOption = new MenuItem("Duplicate");
    colorOption = new MenuItem("Color");
    colorPickerMenu.getItems().addAll(deleteOption, duplicateOption, colorOption);

    setupEditButton();
    setupDragAndDrop();
    setupColorPicker();
    setupEventHandlers();
  }

  private void setupEventHandlers() {

    // TODO: move to GraphicsController

    final var tier = tierListController.getTierByHash(tierHash);

    tierNameLabel.focusedProperty().addListener((_, _, changed) -> {
      if (changed)
        this.oldTextValue = tierNameLabel.getText();
      else
        tierNameLabel.setText(oldTextValue);
    });

    tierNameLabel.setOnAction(_ -> {

      if (!tierNameLabel.getText().isBlank()) {
        tierListController.setTierName(tier, tierNameLabel.getText());
        this.oldTextValue = tierNameLabel.getText();
      }

      tierNameLabel.getScene().getRoot().requestFocus();

    });

    final Tooltip tooltip = new Tooltip("Click to drag and move");
    tierNameLabel.setTooltip(tooltip);

    editTierButton.setOnAction(_ -> colorPickerMenu.show(editTierButton, Side.BOTTOM, 0, 0));

    deleteOption.setOnAction(_ -> {
      tier.getTiered().forEach(tierListController::unTier);

      tierListController.removeTier(tier);
      graphicsController.updateTierList();
    });

    duplicateOption.setOnAction(_ -> {
      final var clone = tier.emptyCopy();
      tierListController.addTier(clone);
      tierListController.moveTier(clone, tierListController.getTiers().indexOf(tier) + 1);

      graphicsController.updateTierList();
    });

    colorOption.setOnAction(_ -> {
      final var mainStage = graphicsController.getMainStage();
      colorStage.show();
      colorStage.setX(mainStage.getX() + (mainStage.getWidth() - colorStage.getWidth()) / 2.0);
      colorStage.setY(mainStage.getY() + (mainStage.getHeight() - colorStage.getHeight()) / 2.0);

      mainStage.setOnCloseRequest(_ -> {
        if (colorStage.isShowing()) {
          colorStage.close();
        }
      });
    });

    confirmColor.setOnAction(_ -> {
      final var chosenColor = Optional.ofNullable(colorPicker.getValue());

      if (chosenColor.isEmpty())
        return;

      tier.setColor(chosenColor.get().toString());
      graphicsController.updateTierList();
      colorStage.close();
    });
  }

  private void setupColorPicker() {
    colorStage = new Stage();

    final BorderPane colorPane = new BorderPane();

    final Scene colorMenu = new Scene(colorPane, ConfigHolder.COLOR_MENU_WIDTH, ConfigHolder.COLOR_MENU_HEIGHT);
    colorPicker = new ColorPicker();
    colorPicker.setPadding(
        new Insets(
            ConfigHolder.COLOR_PADDING_TOP,
            ConfigHolder.COLOR_PADDING_RIGHT,
            ConfigHolder.COLOR_PADDING_BOTTOM,
            ConfigHolder.COLOR_PADDING_LEFT));

    final VBox colorBox = new VBox();
    final Label colorLabel = new Label("Pick a color");
    confirmColor = new Button("Ok");
    confirmColor.setAlignment(Pos.BASELINE_RIGHT);

    colorBox.getChildren().addAll(colorLabel, colorPicker, confirmColor);
    colorBox.setAlignment(Pos.CENTER);
    colorBox.setSpacing(ConfigHolder.COLOR_SPACING);

    colorPane.setCenter(colorBox);
    colorStage.setTitle("Color picker");
    colorStage.setScene(colorMenu);
    colorStage.initModality(Modality.WINDOW_MODAL);
    colorStage.initStyle(StageStyle.UTILITY);
    colorStage.setAlwaysOnTop(true);

  }

  private void setupEditButton() {
    try {
      final String resource = ConfigHolder.getCurrentTheme() == ConfigHolder.Theme.LIGHT
          ? ResourceHolder.EDIT_BUTTON_ICON_LIGHT
          : ResourceHolder.EDIT_BUTTON_ICON_DARK;

      final var imageURI = getClass().getResource(resource);
      if (imageURI == null)
        throw new URISyntaxException("imageURI", "[ERROR] --- Default edit tier resource not found, exiting ---");
      editTierButton.setGraphic(new ImageView(new Image(imageURI.toURI().toString())));
    } catch (URISyntaxException e) {
      System.err.println(e.getReason());
      System.exit(-1);
    }
  }

  private void setupDragAndDrop() {
    tierNameLabel.setOnDragDetected(this::handleDragDetected);
    tierNameLabel.setOnDragOver(event -> {
      if (event.getDragboard().hasString() && !event.getDragboard().hasImage())
        event.acceptTransferModes(TransferMode.MOVE);
      event.consume();
    });

    tierNameLabel.setOnDragEntered(event -> {
      if (event.getTarget() instanceof TextField target && event.getGestureSource() != target
          && event.getDragboard().hasString() && !event.getDragboard().hasImage())
        this.setTierNameLabelBorder(ConfigHolder.DEFAULT_BAR_HIGHLIGHT_COLOR);
      event.consume();
    });

    tierNameLabel.setOnDragExited(event -> {
      if (event.getTarget() instanceof TextField target && event.getGestureSource() != target
          && event.getDragboard().hasString() && !event.getDragboard().hasImage())
        this.setTierNameLabelBorder(ConfigHolder.DEFAULT_BAR_BORDER_COLOR);
      event.consume();
    });

    tierNameLabel.setOnDragDone(event -> {
      if (event.getTransferMode() == TransferMode.MOVE)
        graphicsController.updateTierList();
      event.consume();
    });

    tierNameLabel.setOnDragDropped(this::handleDragDropped);
  }

  private void handleDragDetected(MouseEvent event) {

    if (!(event.getSource() instanceof TextField eventSource))
      return;

    Dragboard dragBoard = eventSource.startDragAndDrop(TransferMode.MOVE);

    final var content = new ClipboardContent();

    content.putString(String.valueOf(tierHash));
    dragBoard.setContent(content);
    event.consume();
  }

  private void handleDragDropped(DragEvent event) {
    final Dragboard db = event.getDragboard();
    boolean success = false;

    if (db.hasString() && !event.getDragboard().hasImage()) {

      final var source = tierListController.getTierByHash(Integer.parseInt(db.getString()));

      Node potentialTarget = (Node) event.getTarget();

      while (potentialTarget != null && !(potentialTarget instanceof TierBox)) {
        potentialTarget = potentialTarget.getParent();
      }
      if (potentialTarget != null) {
        final var target =
                tierListController.getTierByHash(((TierBox) potentialTarget).getTierHash());
        tierListController.moveTier(source, target);
        success = true;
      }
    }
    event.setDropCompleted(success);
    event.consume();
  }

  private void setTierNameLabelBorder(String color) {
    final var tierNameLabelBorder = new Border(
        new BorderStroke(
            Paint.valueOf(color),
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            BorderWidths.DEFAULT));
    tierNameLabel.setBorder(tierNameLabelBorder);
  }

  private void setTierNameLabelBackground() {
    final var backgroundColor = Paint.valueOf(tierListController.getTierByHash(tierHash).getColor());
    final var nameLabelBackground = Background.fill(backgroundColor);
    tierNameLabel.setBackground(nameLabelBackground);
  }

  public int getTierHash() {
    return this.tierHash;
  }

  public void hideEditButton() {
    getChildren().remove(editTierButton);
  }

  public void showEditButton() {
    getChildren().add(editTierButton);
  }

  public void setButtonTheme(ConfigHolder.Theme theme) {

    switch (theme) {
      case LIGHT -> graphicsController.setGraphic(editTierButton, ResourceHolder.EDIT_BUTTON_ICON_LIGHT);
      case DARK -> graphicsController.setGraphic(editTierButton, ResourceHolder.EDIT_BUTTON_ICON_DARK);
    }

  }

}
