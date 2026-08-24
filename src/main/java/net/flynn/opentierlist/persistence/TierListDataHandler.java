package net.flynn.opentierlist.persistence;

import net.flynn.opentierlist.model.models.TierList;
import net.flynn.opentierlist.ui.manual.TieredPane;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface TierListDataHandler {

    void save(File file, TierList tierList);

    void export(Path path, TieredPane node);

    Optional<TierList> load(File file);

}
