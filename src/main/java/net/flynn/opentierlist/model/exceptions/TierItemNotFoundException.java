package net.flynn.opentierlist.model.exceptions;

import java.io.Serial;

/**
 * Custom Exception class
 * 
 * @version 0.00
 * @since v1.0.0
 */
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
