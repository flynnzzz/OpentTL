package net.flynn.opentierlist.model.exceptions;

import java.io.Serial;

public class TierNotFoundException extends IndexOutOfBoundsException {

	@Serial
    private static final long serialVersionUID = 1827696283991396826L;

	public TierNotFoundException(String message) {
		super(message);
	}

}
