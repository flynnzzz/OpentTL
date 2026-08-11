package net.flynn.opentierlist.persistence;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.flynn.opentierlist.model.models.TierList;

public class TierListReader {
  public static TierList read(File file) throws IOException {
    final var tierMapper = new ObjectMapper();
    return tierMapper.readValue(file, TierList.class);
  }
}
