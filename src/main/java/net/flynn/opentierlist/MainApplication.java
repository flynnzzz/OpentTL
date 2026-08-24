package net.flynn.opentierlist;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.flynn.opentierlist.controller.TierListController;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.persistence.ResourceHolder;
import net.flynn.opentierlist.ui.manual.MainPane;

import java.awt.*;

public class MainApplication extends Application {

  @Override
  public void start(Stage stage) {

    if (MainApplication.isOnAndroid()) throw new UnsupportedOperationException("Android Not Yet Implemented");

    final var controller = TierListController.ofDefaultTiers();
    final BorderPane root = new MainPane(controller, stage);
    final var scene = new Scene(root);

    stage.setTitle("OpenTL - " + TierList.DEFAULT_TIER_LIST_NAME);
    stage.setHeight(900);
    stage.setWidth(1100);

    stage.getIcons().add(
            new Image(ResourceHolder.APPLICATION_ICON)
    );
    stage.setScene(scene);


    final var dim = Toolkit.getDefaultToolkit().getScreenSize();
    stage.show();
    stage.setX((dim.getWidth() - stage.getWidth()) / 2);
    stage.setY(dim.getHeight() - stage.getHeight() / 2);

  }

  public static boolean isOnAndroid() {
    try {
      Class.forName("android.os.Build");
      return true;
    } catch (ClassNotFoundException _) { }

    return false;
  }
}
