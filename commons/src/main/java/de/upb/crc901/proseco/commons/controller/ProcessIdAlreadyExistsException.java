package de.upb.crc901.proseco.commons.controller;

/**
 * Notifies that a new process is wanted to be created with a given processId but the processId is already in use
 *
 * @author kadirayk
 *
 */
public class ProcessIdAlreadyExistsException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 7958112245728714324L;

	/**
	 * Constructor with errorMessage
	 *
	 * @param errorMessage error message string
	 */
	public ProcessIdAlreadyExistsException(final String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Default constructor
	 */
	public ProcessIdAlreadyExistsException() {
		super();
	}

}
