package de.upb.crc901.proseco.commons.controller.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.upb.crc901.proseco.commons.controller.DefaultPrototypeExtractor;
import de.upb.crc901.proseco.commons.controller.IPrototypeExtractor;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;

public class DefaultPrototypeExtractorTest {
	@Test(expected = PrototypeCouldNotBeExtractedException.class)
	public void prototypeDoesNotExistTest() throws PrototypeCouldNotBeExtractedException {
		final IPrototypeExtractor extractor = new DefaultPrototypeExtractor();
		extractor.getPrototype("test", null);
	}

	@Test(expected = PrototypeCouldNotBeExtractedException.class)
	public void emptyAnswersTest() throws PrototypeCouldNotBeExtractedException {
		final IPrototypeExtractor extractor = new DefaultPrototypeExtractor();
		final Map<String, String> answers = new HashMap<>();
		extractor.getPrototype("test", answers);
	}

	@Test
	public void getPrototypeTest() throws PrototypeCouldNotBeExtractedException {
		final IPrototypeExtractor extractor = new DefaultPrototypeExtractor();
		final Map<String, String> answers = new HashMap<>();
		answers.put("Please select prototype", "test");
		final String result = extractor.getPrototype("test", answers);
		assertEquals("test", result);
	}
}
