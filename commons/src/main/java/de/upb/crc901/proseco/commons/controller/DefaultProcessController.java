package de.upb.crc901.proseco.commons.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.config.ProcessConfig;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

public class DefaultProcessController implements ProcessController {

	private final File prosecoConfigFile;
	private final PROSECOConfig config;

	public DefaultProcessController(File prosecoConfigFile) {
		super();
		this.prosecoConfigFile = prosecoConfigFile;
		config = PROSECOConfig.get(prosecoConfigFile);
	}
	

	public PROSECOProcessEnvironment createConstructionProcessEnvironment(String domainName, String processId) throws IOException {
		processId = processId.toLowerCase();
		File processFolder = new File(config.getDirectoryForProcesses() + File.separator + processId);
		FileUtils.forceMkdir(processFolder);
		ProcessConfig pc = new ProcessConfig(processId, domainName, prosecoConfigFile);
		new ObjectMapper().writeValue(new File(processFolder + File.separator + "process.json"), pc);
		return new PROSECOProcessEnvironment(processFolder);

	}

	public PROSECOProcessEnvironment getConstructionProcessEnvironment(String processId) {
		try {
			File processFolder = new File(config.getDirectoryForProcesses() + File.separator + processId);
			return new PROSECOProcessEnvironment(processFolder);
		} catch (Exception e) {
			throw new RuntimeException("Could not create an environment object for process id " + processId, e);
		}
	}

}
