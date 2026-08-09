package net.flynn.opentierlist.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.fasterxml.jackson.databind.DatabindException;

import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.ui.manual.TieredPane;

public class DataHandler {
  public DataHandler() {
  }

  public void save(File file, TierList tierList) {
    try {
      TierListWriter.write(file, tierList);
    } catch (IOException e) {
      System.err.println("[ERROR] --- Could not save tier list '" + tierList.getName() + "', aborting ---");
    }
  }

  public void export(Path path, TieredPane node) {
    try {
      TierListWriter.export(path.toFile(), node);
    } catch (IOException _) {
      System.err.println(
          "[ERROR] --- IO exception: could not export Tier List to " + path.getFileName() + " ---");
    }
  }

  public Optional<TierList> load(File file) {
    Optional<TierList> res = Optional.empty();
    try {
      res = Optional.of(TierListReader.read(file));
    } catch (DatabindException _) {
      System.err
          .println("[ERROR] --- Failed to parse tier list from file '" + file.getAbsolutePath() + "', aborting ---");
    } catch (IOException _) {
      System.err
          .println("[ERROR] --- Could not load tier list from path '" + file.getAbsolutePath() + "', aborting ---");
    }
    return res;
  }
}
