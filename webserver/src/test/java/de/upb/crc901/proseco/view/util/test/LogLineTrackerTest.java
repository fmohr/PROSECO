package de.upb.crc901.proseco.view.util.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.upb.crc901.proseco.view.util.LogLine;
import de.upb.crc901.proseco.view.util.LogLineTracker;

public class LogLineTrackerTest {
	private static final String STRATEGY = "strategy1";
	private static final String PROTOTYPE = "test-id";

	@Test
	public void updateLogTest() {
		final LogLine logline = new LogLine("");
		logline.setStrategyName(STRATEGY);
		logline.setAllLineNumber(0);
		logline.setErrLineNumber(1);
		logline.setOutLineNumber(2);
		assertEquals(Integer.valueOf(0), logline.getAllLineNumber());
		assertEquals(Integer.valueOf(1), logline.getErrLineNumber());
		assertEquals(Integer.valueOf(2), logline.getOutLineNumber());
		LogLine result = LogLineTracker.getLogLines(PROTOTYPE, STRATEGY);
		assertEquals(STRATEGY, result.getStrategyName());
		LogLineTracker.updateLog(PROTOTYPE, logline);
		result = LogLineTracker.getLogLines(PROTOTYPE, STRATEGY);
		assertEquals(STRATEGY, result.getStrategyName());
		LogLineTracker.updateLog(PROTOTYPE, logline);
		result = LogLineTracker.getLogLines(PROTOTYPE, "another strategy");
		assertEquals("another strategy", result.getStrategyName());
	}
}
