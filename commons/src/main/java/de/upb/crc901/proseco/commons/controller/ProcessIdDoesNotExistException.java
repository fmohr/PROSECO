package de.upb.crc901.proseco.commons.controller;

/**
 * Notifies that a process with the given processId is tried to be accessed, but the given processId does not exist
 *
 * @author kadirayk
 *
 */
public class ProcessIdDoesNotExistException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 8369949540661017449L;

	/**
	 * Default constructor
	 */
	public ProcessIdDoesNotExistException() {
		super();
	}
}
