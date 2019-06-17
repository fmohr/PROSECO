package de.upb.crc901.proseco.commons.config.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.upb.crc901.proseco.commons.config.DomainConfig;

public class DomainConfigTest {
	@Test
	public void configTest() {
		final Path currentRelativePath = Paths.get("");
		final String p = currentRelativePath.toAbsolutePath().toString().replaceAll("commons", "core/domains/test/domain.conf");
		DomainConfig config = DomainConfig.get(new File(p));
		assertEquals("interview", config.getNameOfInterviewFolder());

		config = DomainConfig.get("");
		assertEquals("res", config.getNameOfInterviewResourceFolder());

		config = DomainConfig.get(new File(""));
		assertEquals("interview_state.json", config.getNameOfInterviewStateFile());

	}
}
