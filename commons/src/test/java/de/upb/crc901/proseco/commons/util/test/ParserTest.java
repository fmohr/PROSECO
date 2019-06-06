package de.upb.crc901.proseco.commons.util.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.upb.crc901.proseco.commons.interview.Interview;
import de.upb.crc901.proseco.commons.util.Parser;

public class ParserTest {

	@Test
	public void initializeInterviewFromConfigTest() throws IOException {
		final Parser parser = new Parser();
		final Path currentRelativePath = Paths.get("");
		final String interviewFile = currentRelativePath.toAbsolutePath().toString().replaceAll("commons", "core/domains/test/interview/interview.yaml");
		final Interview interview = parser.initializeInterviewFromConfig(new File(interviewFile));
		assertNotEquals(null, interview.getStates());
	}
}
