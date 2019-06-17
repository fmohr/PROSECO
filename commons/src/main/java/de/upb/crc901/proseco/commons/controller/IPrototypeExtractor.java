package de.upb.crc901.proseco.commons.controller;

import java.util.Map;

/**
 * Interface describing property extraction
 *
 * @author kadirayk
 *
 */
public interface IPrototypeExtractor {

	/**
	 * Returns a prototype name which is extracted from the given answers and a domain name
	 *
	 * @param domain domain name to search for a prototype
	 * @param answers map of question-answer pairs
	 * @return prototype name
	 * @throws PrototypeCouldNotBeExtractedException thrown when prototype could not be extracted
	 */
	String getPrototype(String domain, Map<String, String> answers) throws PrototypeCouldNotBeExtractedException;

}
