package de.upb.crc901.proseco.commons.config.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.config.PrototypeConfig;

public class PrototypeConfigTest {

	@Test
	public void configTest() {
		final Path currentRelativePath = Paths.get("");
		final String p = currentRelativePath.toAbsolutePath().toString().replaceAll("commons", "core/domains/test/prototypes/test/prototype.conf");
		PrototypeConfig config = PrototypeConfig.get(new File(p));
		assertEquals("run", config.getSearchRunnable());

		config = PrototypeConfig.get("");
		assertEquals("strategies", config.getNameOfStrategyFolder());

		config = PrototypeConfig.get(new File(""));
		assertEquals("deployment", config.getDeploymentCommand());
		final PROSECOConfig prosecoConfig = PROSECOConfig.get("");
		config = PrototypeConfig.get(prosecoConfig, "");
		assertEquals("", config.getDeploymentEntryPoint());

	}
}
