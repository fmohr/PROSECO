package de.upb.crc901.proseco.commons.controller.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.TreeMap;

import org.junit.Test;

import de.upb.crc901.proseco.commons.controller.PROSECOSolution;

public class PROSECOSolutionTest {

	@Test
	public void solutionTest() {
		final PROSECOSolution solution = new PROSECOSolution();
		solution.setProcessId("test-id");
		assertEquals("test-id", solution.getProcessId());
		solution.setWinningScore(1.0);
		assertEquals(new Double(1.0), solution.getWinningScore());
		solution.setWinningStrategyId("strategy1");
		assertEquals("strategy1", solution.getWinningStrategyId());
		solution.setWinningStrategyFolder(new File("winningFolder"));
		assertEquals(new File("winningFolder"), solution.getWinningStrategyFolder());
		solution.setBackupStrategies(new TreeMap<>());
		assertEquals(new TreeMap<>(), solution.getBackupStrategies());

		solution.setBackupStrategyFolders(new TreeMap<>());
		assertEquals(new TreeMap<>(), solution.getBackupStrategyFolders());

	}
}
