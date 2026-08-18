package net.flynn.opentierlist.model.exceptions;

import java.io.Serial;

public class TierItemNotFoundException extends IndexOutOfBoundsException {

	@Serial
    private static final long serialVersionUID = -6509042509524524415L;
	
	public TierItemNotFoundException(String message) {
		super(message);
	}
	
	public TierItemNotFoundException() {
		super();
	}
}
