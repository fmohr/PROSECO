package de.upb.crc901.proseco.view.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogLineTracker {

	private LogLineTracker() {
	}

	private static Map<String, List<LogLine>> logMap = new HashMap<>();

	public static void updateLog(String prototypeId, LogLine logLine) {
		List<LogLine> logLineList = logMap.get(prototypeId);
		if (logLineList != null) {
			for (LogLine e : logLineList) {
				if (e.getStrategyName().equalsIgnoreCase(logLine.getStrategyName())) {
					e.setAllLineNumber(logLine.getAllLineNumber());
					e.setErrLineNumber(logLine.getErrLineNumber());
					e.setOutLineNumber(logLine.getOutLineNumber());
				}
			}
		} else {
			logLineList = new ArrayList<>();
			logLineList.add(logLine);
			logMap.put(prototypeId, logLineList);
		}
	}

	public static LogLine getLogLines(String prototypeId, String strategyName) {
		List<LogLine> logLineList = logMap.get(prototypeId);
		if (logLineList != null) {
			for (LogLine e : logLineList) {
				if (e.getStrategyName().equalsIgnoreCase(strategyName)) {
					return e;
				}
			}
		}
		return new LogLine(strategyName);
	}

}
