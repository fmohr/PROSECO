package de.upb.crc901.proseco.commons.controller.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.controller.DefaultDomainScoreComputer;
import de.upb.crc901.proseco.commons.controller.DomainCouldNotBeDetectedException;
import de.upb.crc901.proseco.commons.controller.IDomainScoreComputer;

public class DefaultDomainScoreComputerTest {
	@Test(expected = DomainCouldNotBeDetectedException.class)
	public void emptyDescriptionTest() throws DomainCouldNotBeDetectedException {
		final IDomainScoreComputer<String> computer = new DefaultDomainScoreComputer();
		computer.getDomainScore("", "test");
	}

	@Test(expected = DomainCouldNotBeDetectedException.class)
	public void emptyDomainTest() throws DomainCouldNotBeDetectedException {
		final IDomainScoreComputer<String> computer = new DefaultDomainScoreComputer();
		computer.getDomainScore("test", "");
	}

	@Test
	public void findDomainTest() throws DomainCouldNotBeDetectedException {
		final IDomainScoreComputer<String> computer = new DefaultDomainScoreComputer();
		assertEquals(Double.valueOf(1.0), computer.getDomainScore("test", "test"));
	}

	@Test
	public void cantFindDomainTest() throws DomainCouldNotBeDetectedException {
		final IDomainScoreComputer<String> computer = new DefaultDomainScoreComputer();
		assertEquals(Double.valueOf(0.0), computer.getDomainScore("some description", "test"));
	}

	@Test
	public void getAvailableDomainsTest() {
		final DefaultDomainScoreComputer computer = new DefaultDomainScoreComputer();
		final List<String> domains = computer.getAvailableDomains(PROSECOConfig.get(""));
		assertTrue(domains.contains("test"));
	}

}
