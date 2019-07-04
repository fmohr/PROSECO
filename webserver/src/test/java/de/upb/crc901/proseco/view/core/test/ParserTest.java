package de.upb.crc901.proseco.view.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.upb.crc901.proseco.commons.interview.Interview;
import de.upb.crc901.proseco.commons.util.Parser;

public class ParserTest {
	Parser parser;

	@Before
	public void init() {
		this.parser = new Parser();
	}

	@Test
	public void parseInterviewTest() throws IOException {
		String filePath = "testdata/interview/interview.yaml";
		File file = new File(filePath);
		Interview interview = this.parser.initializeInterviewFromConfig(file);
		assertTrue(interview != null);

		assertEquals("step0", interview.getStates().get(0).getName());

		assertEquals("prototype", interview.getStates().get(0).getQuestions().get(0).getId());

	}

	@Test(expected = FileNotFoundException.class)
	public void testExceptionCase() throws IOException {
		String filePath = "wrongpath";
		File file = new File(filePath);
		this.parser.initializeInterviewFromConfig(file);
	}

}
