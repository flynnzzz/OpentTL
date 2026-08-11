package net.flynn.opentierlist.persistence;

import java.io.File;
import java.io.IOException;

import atlantafx.base.theme.NordDark;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.ui.manual.TieredPane;

import javax.imageio.ImageIO;

public class TierListWriter {
  public static void write(File file, TierList tierList) throws IOException {
    final var tierMapper = new ObjectMapper();
    tierMapper.writeValue(file, tierList);
  }

  private static WritableImage screenshot(TieredPane node) {

    final double inboundWidth = node.getContent().getBoundsInLocal().getWidth(),
        inboundHeight = node.getContent().getBoundsInLocal().getHeight();

    final WritableImage image = new WritableImage((int) inboundWidth, (int) inboundHeight);

    final var params = new SnapshotParameters();
    params.setTransform(new Scale(1, 1));

    if (Application.getUserAgentStylesheet().equals(new NordDark().getUserAgentStylesheet()))
      // background color appears white in the screenshot otherwise
      params.setFill(Color.valueOf("#2e3440"));

    node.hideEditButtons();

    node.getContent().snapshot(params, image);

    node.showEditButtons();

    return image;
  }

  public static void export(File file, TieredPane node) throws IOException {

    ImageIO.write(SwingFXUtils.fromFXImage(screenshot(node), null), "png", file);

  }
}
