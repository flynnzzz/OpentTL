package net.flynn.opentierlist.model.enums;

import net.flynn.opentierlist.model.models.Tier;
import net.flynn.opentierlist.persistence.DataHandler;

public enum DefaultTier {
  S(DataHandler.ConfigHolder.DEFAULT_S_COLOR),
  A(DataHandler.ConfigHolder.DEFAULT_A_COLOR),
  B(DataHandler.ConfigHolder.DEFAULT_B_COLOR),
  C(DataHandler.ConfigHolder.DEFAULT_C_COLOR),
  D(DataHandler.ConfigHolder.DEFAULT_D_COLOR),
  E(DataHandler.ConfigHolder.DEFAULT_E_COLOR),
  F(DataHandler.ConfigHolder.DEFAULT_F_COLOR),
  UNTIERED(DataHandler.ConfigHolder.DEFAULT_UNTIERED_COLOR);

  private final Tier value;

  DefaultTier(String color) {
    this.value = new Tier(name(), color);
  }

  public Tier value() {
    return value;
  }
}
