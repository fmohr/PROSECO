package de.upb.crc901.proseco.commons.util.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

public class PROSECOProcessEnvironmentTest {
	@Test
	public void environmentTest() throws IOException {
		final Path currentRelativePath = Paths.get("");
		final String p = currentRelativePath.toAbsolutePath().toString().replaceAll("commons", "core/processes/test-default");
		final PROSECOProcessEnvironment environment = new PROSECOProcessEnvironment(new File(p));
		assertTrue(environment.getProcessDirectory().toString().contains("test-default"));
		assertTrue(environment.getBenchmarksDirectory().toString().contains("5"));
		assertTrue(environment.getProcessId().equals("test-default"));
		assertTrue(environment.getGroundingDirectory().toString().contains("test"));
		assertTrue(environment.getInterviewDirectory().toString().contains("interview"));
		assertTrue(environment.getInterviewResourcesDirectory().toString().contains("res"));
		assertTrue(environment.getInterviewStateDirectory().toString().contains("interview"));
		assertTrue(environment.getInterviewStateFile().toString().contains("interview_state.json"));
		assertNotEquals(null, environment.getProsecoConfig());
		assertNotEquals(null, environment.getPrototypeConfig());
		assertTrue(environment.getPrototypeName().contains("test"));
		assertTrue(environment.getPrototypeDirectory().toString().contains("prototype"));
		assertTrue(environment.getSearchDirectory().toString().contains("search"));
		assertTrue(environment.getSearchInputDirectory().toString().contains("in"));
		assertTrue(environment.getSearchOutputDirectory().toString().contains("out"));
		assertTrue(environment.getServiceHandle().toString().contains("test"));
		assertTrue(environment.getStrategyDirectory().toString().contains("test"));
		assertTrue(environment.verificationExecutable().toString().contains("analysis"));
		assertTrue(environment.deploymentExecutable().toString().contains("deployment"));
		assertTrue(environment.groundingExecutable().toString().contains("grounding"));
		assertTrue(environment.getGroundingDirectory().toString().contains("test"));
		assertTrue(StringUtils.isNotEmpty(environment.toString()));

	}
}
