package de.upb.crc901.proseco.commons.config;

import java.io.File;

public class ProcessConfig {
	private String processId;
	private String domain; // knowing the domain, one can derive the interview state file and, from that file, extract knowledge about the prototype
	private File prosecoConfigFile;

	public ProcessConfig() {

	}

	public ProcessConfig(String processId, String domain, File prosecoConfigFile) {
		super();
		this.processId = processId;
		this.domain = domain;
		this.prosecoConfigFile = prosecoConfigFile;
	}

	public String getProcessId() {
		return this.processId;
	}

	public String getDomain() {
		return this.domain;
	}

	public File getProsecoConfigFile() {
		return this.prosecoConfigFile;
	}

	public void setProsecoConfigFile(File prosecoConfigFile) {
		this.prosecoConfigFile = prosecoConfigFile;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
