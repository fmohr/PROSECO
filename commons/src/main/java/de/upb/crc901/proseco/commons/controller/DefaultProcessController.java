package de.upb.crc901.proseco.commons.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.config.ProcessConfig;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

/**
 *
 * DefaultProcessController
 *
 */
public class DefaultProcessController {

	private final File prosecoConfigFile;
	private final PROSECOConfig config;

	/**
	 * Instantiates a DefaultProcessController with given prosecoConfigFile
	 *
	 * @param prosecoConfigFile file pointing to prosecoConfig
	 */
	public DefaultProcessController(final File prosecoConfigFile) {
		super();
		this.prosecoConfigFile = prosecoConfigFile;
		this.config = PROSECOConfig.get(prosecoConfigFile);
	}

	/**
	 * Creates a new PROSECO service construction process for a given prototype. The prototype skeleton is copied for the new process.
	 *
	 * @param domainName domainName
	 * @return {@link PROSECOProcessEnvironment}
	 * @throws IOException thrown when dealing with file system
	 */
	public PROSECOProcessEnvironment createConstructionProcessEnvironment(final String domainName) throws IOException {
		final String id = domainName + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toLowerCase();
		final File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + id);
		FileUtils.forceMkdir(processFolder);
		final ProcessConfig pc = new ProcessConfig(id, domainName, this.prosecoConfigFile);
		new ObjectMapper().writeValue(new File(processFolder + File.separator + "process.json"), pc);
		return new PROSECOProcessEnvironment(processFolder);
	}

	/**
	 * Returns {@link PROSECOProcessEnvironment} with given id
	 *
	 * @param processId id of process
	 * @return {@link PROSECOProcessEnvironment}
	 */
	public PROSECOProcessEnvironment getConstructionProcessEnvironment(final String processId) {
		try {
			final File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + processId);
			return new PROSECOProcessEnvironment(processFolder);
		} catch (final Exception e) {
			throw new PROSECORuntimeException("Could not create an environment object for process id " + processId, e);
		}
	}

}
