package de.upb.crc901.proseco.commons.controller;

import java.io.File;
import java.util.Map;

/**
 * Default implementation of IPrototypeExtractor interface.
 * Extracts prototype from interview fillout.
 *
 * @author kadirayk
 *
 */
public class DefaultPrototypeExtractor implements IPrototypeExtractor {

	@Override
	public String getPrototype(final String domain, final Map<String, String> answers) throws PrototypeCouldNotBeExtractedException {
		String prototype = null;
		if (answers != null) {
			prototype = answers.get("Please select prototype");
			if (prototype == null) {
				prototype = answers.get("prototype");
			}
		}

		if (prototype == null || !prototypeExists(domain, prototype)) {
			throw new PrototypeCouldNotBeExtractedException();
		}
		return prototype;
	}

	private static boolean prototypeExists(final String domain, final String prototype) {
		boolean exists = false;
		final File prototypesFolder = new File("domains/" + domain + "/prototypes");
		for (final File file : prototypesFolder.listFiles()) {
			if (file.getName().equalsIgnoreCase(prototype)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

}
