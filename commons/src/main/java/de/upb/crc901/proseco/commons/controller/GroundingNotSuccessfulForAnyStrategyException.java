package de.upb.crc901.proseco.commons.controller;

/**
 * Notifies that Grounding was performed for all the available strategies but none of them succeeded
 *
 * @author kadirayk
 *
 */
public class GroundingNotSuccessfulForAnyStrategyException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 4803122703700883235L;

	/**
	 * Default constructor
	 */
	public GroundingNotSuccessfulForAnyStrategyException() {
		super();
	}
}
