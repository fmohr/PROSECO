package de.upb.crc901.proseco.core.composition;

import java.io.File;
import java.io.IOException;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.ProcessIdDoesNotExistException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

/**
 * Implementation of {@link AProsecoConfigurationProcess} for file based configuration
 *
 * @author kadirayk
 *
 */
public class FileBasedConfigurationProcess extends AProsecoConfigurationProcess {

	private final File prosecoConfigFile;
	private final PROSECOConfig config;

	/**
	 * Default constructor that initiates a FileBasedConfigurationProcess with the given prosecoConfig file
	 *
	 * @param prosecoConfigFile file that points to the prosecoConfigFile that configures the values described in {@link PROSECOConfig}
	 */
	public FileBasedConfigurationProcess(final File prosecoConfigFile) {
		try {
			super.updateProcessState(EProcessState.INIT);
		} catch (final InvalidStateTransitionException e) {
			logger.error(e.getMessage());
		}
		this.prosecoConfigFile = prosecoConfigFile;
		this.config = PROSECOConfig.get(prosecoConfigFile);
	}

	@Override
	public void createNew(final String processId) throws ProcessIdAlreadyExistsException, InvalidStateTransitionException {
		this.createNewForConfig(processId, this.config);
	}

	@Override
	public void fixDomain(final String domain) throws InvalidStateTransitionException {
		super.fixDomain(domain);
		this.createEnvironment(domain, this.prosecoConfigFile, this.config);

	}

	@Override
	public PROSECOProcessEnvironment getProcessEnvironment() throws InvalidStateTransitionException {
		if (super.getProcessState() == EProcessState.INIT || super.getProcessState() == EProcessState.CREATED) {
			throw new InvalidStateTransitionException(String.format("CurrentState: %s", this.getProcessState()));
		}
		return this.processEnvironment;
	}

	@Override
	public void attach(final String processId) throws ProcessIdDoesNotExistException, InvalidStateTransitionException {
		final File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + processId);
		if (!processFolder.exists()) {
			throw new ProcessIdDoesNotExistException();
		}
		this.processId = processId;
		if (this.getProcessState() == EProcessState.INIT) {
			super.updateProcessState(EProcessState.CREATED);
		}
		this.fixDomain(processId.split("-")[0]);
		try {
			this.processEnvironment = new PROSECOProcessEnvironment(processFolder);
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}

		if (this.getProcessState() != EProcessState.DOMAIN_DEFINITION) { // no need to update state
			super.updateProcessState(EProcessState.CREATED);
			// domain is already known after attaching
			super.updateProcessState(EProcessState.DOMAIN_DEFINITION);
		}

	}

	@Override
	protected void extractPrototype() throws PrototypeCouldNotBeExtractedException, InvalidStateTransitionException {
		super.extractPrototype();
		final File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + this.processId);

		// update environment with prototype info
		try {
			this.processEnvironment = new PROSECOProcessEnvironment(processFolder);
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
	}

}
