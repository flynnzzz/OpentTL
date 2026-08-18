package net.flynn.opentierlist.persistence;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.ui.manual.TieredPane;

public class TierListWriter {
  public static void write(File file, TierList tierList) throws IOException {
    final var tierMapper = new ObjectMapper();
    tierMapper.writeValue(file, tierList);
  }

  private static WritableImage screenshot(TieredPane node) {

    final Bounds bounds = node.getContent().getBoundsInLocal();
    final double fixedWidth = DataHandler.ConfigHolder.SCREENSHOT_WIDTH,
        dynamicWidth = bounds.getWidth();

    final double targetWidth = Math.min(fixedWidth, dynamicWidth),
        inboundHeight = bounds.getHeight(),
        x = bounds.getMinX() + (dynamicWidth - targetWidth) / 2;

    final WritableImage image = new WritableImage((int) targetWidth, (int) inboundHeight);

    final var params = new SnapshotParameters();
    params.setTransform(new Scale(1, 1));
    params.setViewport(new Rectangle2D(x, 0, targetWidth, inboundHeight));

    final var darkModeColor = "#2e3440";
    if (DataHandler.ConfigHolder.getCurrentTheme() == DataHandler.ConfigHolder.Theme.DARK)
      // background color appears white in the screenshot otherwise
      params.setFill(Color.valueOf((darkModeColor)));

    node.hideEditButtons();

    node.getContent().snapshot(params, image);

    node.showEditButtons();

    return image;
  }

  public static void export(File file, TieredPane node) throws IOException {

    ImageIO.write(SwingFXUtils.fromFXImage(screenshot(node), null), "png", file);

  }
}
