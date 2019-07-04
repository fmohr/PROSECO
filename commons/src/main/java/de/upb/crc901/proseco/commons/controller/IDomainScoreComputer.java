package de.upb.crc901.proseco.commons.controller;

/**
 * Interface describing domain score computation
 *
 * @author kadirayk
 *
 * @param <T> task description
 */
public interface IDomainScoreComputer<T> {

	/**
	 * Returns a score between 0.0 - 1.0 for the given task description and the domain name
	 *
	 * @param description task description
	 * @param domain candidate domain name
	 * @return confidence value between 0.0 - 1.0
	 * @throws DomainCouldNotBeDetectedException thrown when domain could not be detected
	 */
	Double getDomainScore(T description, String domain) throws DomainCouldNotBeDetectedException;

}
