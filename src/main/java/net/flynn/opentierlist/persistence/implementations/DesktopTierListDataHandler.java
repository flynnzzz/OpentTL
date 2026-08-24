package net.flynn.opentierlist.persistence.implementations;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import net.flynn.opentierlist.ConfigHolder;
import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.persistence.TierListDataHandler;
import net.flynn.opentierlist.ui.manual.TieredPane;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class DesktopTierListDataHandler implements TierListDataHandler {

  private WritableImage screenshot(TieredPane node) {

    final Bounds bounds = node.getContent().getBoundsInLocal();
    final double fixedWidth = ConfigHolder.SCREENSHOT_WIDTH,
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

  private TierList read(File file) throws IOException {
    final var tierMapper = new ObjectMapper();
    return tierMapper.readValue(file, TierList.class);
  }

  private void write(File file, TierList tierList) throws IOException {
    final var tierMapper = new ObjectMapper();
    tierMapper.writeValue(file, tierList);
  }

  private void export(File file, TieredPane node) throws IOException {
    ImageIO.write(SwingFXUtils.fromFXImage(screenshot(node), null), "png", file);
  }

  @Override
  public void save(File file, TierList tierList) {
    try {
      write(file, tierList);
    } catch (IOException _) {
      System.err.println("[ERROR] --- Could not save tier list '" + tierList.getName() + "', aborting ---");
    }
  }

  @Override
  public void export(Path path, TieredPane node) {
    try {
      export(path.toFile(), node);
    } catch (IOException _) {
      System.err.println(
          "[ERROR] --- IO exception: could not export Tier List to " + path.getFileName() + " ---");
    }
  }

  @Override
  public Optional<TierList> load(File file) {
    try {
       return Optional.of(read(file));
    } catch (DatabindException e) {
      System.err
          .println("[ERROR|DataBindException] --- Failed to parse tier list from file '" + file.getAbsolutePath() + "', aborting ---");
    } catch (IOException _) {
      System.err
          .println("[ERROR|IOException] --- Could not load tier list from path '" + file.getAbsolutePath() + "', aborting ---");
    }
      return Optional.empty();
  }
}
