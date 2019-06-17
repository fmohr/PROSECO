package de.upb.crc901.proseco.view.app.model;

import java.util.HashMap;
import java.util.Map;

import de.upb.crc901.proseco.commons.util.ToJSONStringUtil;

/**
 *
 * @author kadirayk
 *
 */
public class LogPair {

	private String prototypeName;
	private String strategyName;
	private String systemOutLog;
	private String systemErrorLog;
	private String systemAllLog;

	/**
	 * Default constructor
	 *
	 * @param prototypeName name of the prototype
	 * @param strategyName name of the strategy
	 * @param systemOutLog log for system out
	 * @param systemErrorLog log for system error
	 * @param systemAllLog log for both system out and error
	 */
	public LogPair(final String prototypeName, final String strategyName, final String systemOutLog, final String systemErrorLog, final String systemAllLog) {
		this.prototypeName = prototypeName;
		this.strategyName = strategyName.replace(" ", "-");
		this.systemOutLog = systemOutLog;
		this.systemErrorLog = systemErrorLog;
		this.systemAllLog = systemAllLog;
	}

	public String getStrategyName() {
		return this.strategyName;
	}

	public void setStrategyName(final String strategyName) {
		this.strategyName = strategyName;
	}

	public String getSystemOutLog() {
		return this.systemOutLog;
	}

	public void setSystemOutLog(final String systemLog) {
		this.systemOutLog = systemLog;
	}

	public String getSystemErrorLog() {
		return this.systemErrorLog;
	}

	public void setSystemErrorLog(final String errorLog) {
		this.systemErrorLog = errorLog;
	}

	public String getSystemAllLog() {
		return this.systemAllLog;
	}

	public void setSystemAllLog(final String systemAllLog) {
		this.systemAllLog = systemAllLog;
	}

	public String getPrototypeName() {
		return this.prototypeName;
	}

	public void setPrototypeName(final String prototypeName) {
		this.prototypeName = prototypeName;
	}

	@Override
	public String toString() {
		final Map<String, Object> fields = new HashMap<>();
		fields.put("strategyName", this.strategyName);
		fields.put("systemOutLog", this.systemOutLog);
		fields.put("systemErrorLog", this.systemErrorLog);
		fields.put("systemAllLog", this.systemAllLog);
		fields.put("prototypeName", this.prototypeName);
		return ToJSONStringUtil.toJSONString("LogPair", fields);
	}

}
