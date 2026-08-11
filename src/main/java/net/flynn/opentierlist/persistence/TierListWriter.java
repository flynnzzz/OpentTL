package net.flynn.opentierlist.persistence;

import java.io.File;
import java.io.IOException;

import atlantafx.base.theme.NordDark;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.ui.ConfigHolder;
import net.flynn.opentierlist.ui.manual.TierBox;
import net.flynn.opentierlist.ui.manual.TieredPane;

import javax.imageio.ImageIO;

public class TierListWriter {
  public static void write(File file, TierList tierList) throws IOException {
    final var tierMapper = new ObjectMapper();
    tierMapper.writeValue(file, tierList);
  }

  private static WritableImage screenshot(TieredPane node) {

    final Bounds bounds = node.getContent().getBoundsInLocal();
    final double fixedWidth = ConfigHolder.DEFAULT_TIERED_BAR_WIDTH + 128,
                 dynamicWidth = bounds.getWidth();

    final double targetWidth = Math.min(fixedWidth, dynamicWidth),
                 inboundHeight = bounds.getHeight(),
                 x = bounds.getMinX() + (dynamicWidth - targetWidth) / 2;

    final WritableImage image = new WritableImage((int) targetWidth, (int) inboundHeight);

    final var params = new SnapshotParameters();
    params.setTransform(new Scale(1, 1));
    params.setViewport(new Rectangle2D(x, 0, targetWidth, inboundHeight));

    final var darkModeColor = "#2e3440";
    if (ConfigHolder.getCurrentTheme() == ConfigHolder.Theme.DARK)
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
