package de.upb.crc901.proseco.view.core;

/**
 *
 * @author kadirayk
 *
 */
public class NextStateNotFoundException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public NextStateNotFoundException() {
		super("None of the previous conditions were true to find the next state, a default condition should be defined. i.e. \ntransition:\n  default:state_x");
	}

}
