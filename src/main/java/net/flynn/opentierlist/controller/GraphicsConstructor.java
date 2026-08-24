package net.flynn.opentierlist.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import net.flynn.opentierlist.ConfigHolder;
import net.flynn.opentierlist.model.models.TierItem;
import net.flynn.opentierlist.ui.manual.ItemView;
import net.flynn.opentierlist.ui.manual.ItemsPane;
import net.flynn.opentierlist.ui.manual.TierBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicsConstructor {

    private final TierListController tierListController;
    private final GraphicsController graphicsController;
    private final Map<Integer, Image> imageCache;

    public GraphicsConstructor(TierListController tierListController, GraphicsController graphicsController) {
        this.tierListController = tierListController;
        this.graphicsController = graphicsController;
        this.imageCache = new HashMap<>();
    }

    public ObservableList<TierBox> constructTierBoxes() {
        return FXCollections.observableArrayList(
                tierListController
                        .getTiers().stream()
                        .map(t -> new TierBox(t.hashCode(), tierListController, graphicsController))
                        .toList());
    }

    public ObservableList<ItemView> constructItemViews(ItemsPane flowPane, List<TierItem> items) {

        final ObservableList<ItemView> images = FXCollections.observableArrayList();

        items.forEach(item -> {

            final String url = item.getImageUriOrDefault();

            Image img = imageCache.get(item.hashCode());

            if (img == null) {
                img = new Image(url,
                        ConfigHolder.DEFAULT_CELL_SIZE,
                        ConfigHolder.DEFAULT_CELL_SIZE,
                        false,
                        false);
                imageCache.put(item.hashCode(), img);
            }

            final var imageViewer = new ItemView(
                    img,
                    tierListController,
                    graphicsController,
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

    public void reloadImageCache() {
        System.err.println("[INFO] --- Reloading image cache ---");
        imageCache.clear();
    }

    void updateTiered() {

        if (graphicsController.getTieredPane() == null) {
            System.err.println(
                    "[ERROR] --- Cannot update Tiers: Controller is missing the necessary instance ---");
            return;
        }
        graphicsController.getTieredPane().update();
    }

     void updateUnTiered() {
        if (graphicsController.getUnTieredPane() == null) {
            System.err.println(
                    "[ERROR] --- Cannot update untiered: Controller is missing the necessary instance ---");
            return;
        }
        graphicsController.getUnTieredPane().update();
    }

    public void updateAll() {

        if (graphicsController.getMainStage() == null) {
            System.err.println(
                    "[ERROR] --- Cannot update Tier List: Controller is missing a Stage instance ---");
            return;
        }

        if (graphicsController.getMainPane() == null) {
            System.err.println(
                    "[ERROR] --- Cannot update Tier List: Controller is missing a MainPane instance ---");
            return;
        }

        final String newTitle = tierListController.getTierListName();
        graphicsController.getMainPane().updateTitleLabel(newTitle);
        graphicsController.getMainStage().setTitle("OpenTL - " + newTitle);

        updateTiered();
        updateUnTiered();
    }


}
