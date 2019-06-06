package de.upb.crc901.proseco.commons.config.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import de.upb.crc901.proseco.commons.config.ProcessConfig;

public class ProcessConfigTest {
	@Test
	public void configTest() {
		final ProcessConfig config1 = new ProcessConfig();
		config1.setDomain("domain");
		config1.setProcessId("pid");
		config1.setProsecoConfigFile(new File(""));
		final ProcessConfig config2 = new ProcessConfig("pid", "domain", new File(""));
		assertEquals(config1.getDomain(), config2.getDomain());
		assertEquals(config1.getProcessId(), config2.getProcessId());
		assertEquals(config1.getProsecoConfigFile(), config2.getProsecoConfigFile());
	}
}
