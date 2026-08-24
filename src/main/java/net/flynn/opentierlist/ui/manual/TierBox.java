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
import net.flynn.opentierlist.ConfigHolder;
import net.flynn.opentierlist.persistence.ResourceHolder;
import net.flynn.opentierlist.controller.GraphicsController;

public class TierBox extends HBox {

  private final TextField tierNameText;
    private final Button editTierButton;
  private final TierListController tierListController;
  private final GraphicsController graphicsController;
  private int tierHash;
  private String oldTextValue;

  private Stage colorStage;
  private ColorPicker colorPicker;
  private Button confirmColor;
  private final ContextMenu colorPickerMenu;
  private final MenuItem deleteOption;
  private final MenuItem duplicateOption;
  private final MenuItem colorOption;

  public TierBox(int tierHash, TierListController tierListController, GraphicsController graphicsController) {
    this.tierListController = tierListController;
    this.graphicsController = graphicsController;
    this.tierHash = tierHash;
    this.tierNameText = new TextField(tierListController.getTierByHash(tierHash).getName());
    this.editTierButton = new Button();
      ItemsPane tieredPane = new ItemsPane(tierHash, tierListController, graphicsController);
    this.oldTextValue = "";

    getChildren().addAll(tierNameText, tieredPane, editTierButton);

    {
      tierNameText.setEditable(true);
      tierNameText.setFocusTraversable(false);
      tierNameText.setAlignment(Pos.CENTER);
      tierNameText.setPrefSize(ConfigHolder.DEFAULT_CELL_SIZE, ConfigHolder.DEFAULT_CELL_SIZE);

      editTierButton.setAlignment(Pos.CENTER);
      editTierButton.setFocusTraversable(false);

      setTierNameLabelBackground();
      setTierNameLabelBorder(ConfigHolder.DEFAULT_BAR_BORDER_COLOR);

      setAlignment(Pos.CENTER);

      setSpacing(ConfigHolder.DEFAULT_TIER_SPACING);
      setPadding(new Insets(
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

  private void updateOldTitle(final String newTitle) {
    oldTextValue = newTitle;
  }

  private void setTierHash(int hash) {
    this.tierHash = hash;
  }

  private void setupEventHandlers() {

    final var tier = tierListController.getTierByHash(tierHash);

    tierNameText.focusedProperty().addListener((_, _, changed) -> {
      if (changed)
        updateOldTitle(tierNameText.getText());
      else
        tierNameText.setText(oldTextValue);
    });

    tierNameText.setOnAction(_ -> {

      if (!tierNameText.getText().isBlank()) {
        tierListController.setTierName(tier, tierNameText.getText());
        setTierHash(tier.hashCode());
        updateOldTitle(tierNameText.getText());
      }

      tierNameText.getScene().getRoot().requestFocus();

    });

    final Tooltip tooltip = new Tooltip("Click to drag and move");
    tierNameText.setTooltip(tooltip);

    editTierButton.setOnAction(_ -> colorPickerMenu.show(editTierButton, Side.BOTTOM, 0, 0));

    deleteOption.setOnAction(_ -> {
      tier.getItems().forEach(tierListController::unTier);

      tierListController.removeTier(tier);
      graphicsController.updateAll();
    });

    duplicateOption.setOnAction(_ -> {
      final var clone = tier.emptyCopy();
      tierListController.addTier(clone);
      tierListController.moveTier(clone, tierListController.getTiers().indexOf(tier) + 1);

      graphicsController.updateAll();
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
      graphicsController.updateAll();
      colorStage.close();
    });
  }

  private void setupColorPicker() {
    colorStage = new Stage();

    final BorderPane colorPane = new BorderPane();

    final Scene colorMenu = new Scene(colorPane, ConfigHolder.COLOR_MENU_WIDTH,
        ConfigHolder.COLOR_MENU_HEIGHT);
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
    tierNameText.setOnDragDetected(this::handleDragDetected);
    tierNameText.setOnDragOver(event -> {
      if (event.getDragboard().hasString() && !event.getDragboard().hasImage())
        event.acceptTransferModes(TransferMode.MOVE);
      event.consume();
    });

    tierNameText.setOnDragEntered(event -> {
      if (event.getTarget() instanceof TextField target && event.getGestureSource() != target
          && event.getDragboard().hasString() && !event.getDragboard().hasImage())
        setTierNameLabelBorder(ConfigHolder.DEFAULT_BAR_HIGHLIGHT_COLOR);
      event.consume();
    });

    tierNameText.setOnDragExited(event -> {
      if (event.getTarget() instanceof TextField target && event.getGestureSource() != target
          && event.getDragboard().hasString() && !event.getDragboard().hasImage())
        setTierNameLabelBorder(ConfigHolder.DEFAULT_BAR_BORDER_COLOR);
      event.consume();
    });

    tierNameText.setOnDragDone(event -> {
      if (event.getTransferMode() == TransferMode.MOVE)
        graphicsController.updateAll();
      event.consume();
    });

    tierNameText.setOnDragDropped(this::handleDragDropped);
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
        final var target = tierListController.getTierByHash(((TierBox) potentialTarget).getTierHash());
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
    tierNameText.setBorder(tierNameLabelBorder);
  }

  private void setTierNameLabelBackground() {
    final var backgroundColor = Paint.valueOf(tierListController.getTierByHash(tierHash).getColor());
    final var nameLabelBackground = Background.fill(backgroundColor);
    tierNameText.setBackground(nameLabelBackground);
  }

  public int getTierHash() {
    return tierHash;
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
