package de.upb.crc901.proseco.core.composition;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.config.ProcessConfig;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.ProcessIdDoesNotExistException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;
import de.upb.crc901.proseco.commons.util.SerializationUtil;

public class FileBasedConfigurationProcess extends AProsecoConfigurationProcess {

	private final File prosecoConfigFile;
	private final PROSECOConfig config;

	public FileBasedConfigurationProcess(File prosecoConfigFile) {
		try {
			super.updateProcessState(EProcessState.INIT);
		} catch (InvalidStateTransitionException e) {
			logger.error(e.getMessage());
		}
		this.prosecoConfigFile = prosecoConfigFile;
		this.config = PROSECOConfig.get(prosecoConfigFile);
	}

	@Override
	public void createNew() throws ProcessIdAlreadyExistsException, InvalidStateTransitionException {
		super.updateProcessState(EProcessState.CREATED);
	}

	@Override
	public void createNew(String processId) throws ProcessIdAlreadyExistsException, InvalidStateTransitionException {
		if (processId != null) {
			File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + processId);
			if (processFolder.exists()) {
				throw new ProcessIdAlreadyExistsException();
			}
		}

		this.processId = processId;

		super.updateProcessState(EProcessState.CREATED);
	}

	private void createEnvironment(String domain) {
		if (this.processId == null) {
			String id = domain + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toLowerCase();
			this.processId = id;
		}
		File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + this.processId);

		try {
			FileUtils.forceMkdir(processFolder);
		} catch (IOException e) {
			// File IO exception is only relevant for FileBasedConfigurationProcess
			logger.error(e.getMessage());
		}

		ProcessConfig pc = new ProcessConfig(this.processId, domain, this.prosecoConfigFile);
		try {
			new ObjectMapper().writeValue(new File(processFolder + File.separator + "process.json"), pc);
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		try {
			this.processEnvironment = new PROSECOProcessEnvironment(processFolder);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}

	@Override
	public void fixDomain(String domain) throws InvalidStateTransitionException {
		super.fixDomain(domain);
		this.createEnvironment(domain);

	}

	@Override
	public PROSECOProcessEnvironment getProcessEnvironment() throws InvalidStateTransitionException {
		if (super.getProcessState() == EProcessState.INIT || super.getProcessState() == EProcessState.CREATED) {
			throw new InvalidStateTransitionException();
		}
		return this.processEnvironment;
	}

	@Override
	public void attach(String processId) throws ProcessIdDoesNotExistException, InvalidStateTransitionException {
		File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + processId);
		if (!processFolder.exists()) {
			throw new ProcessIdDoesNotExistException();
		}
		this.processId = processId;

		try {
			this.processEnvironment = new PROSECOProcessEnvironment(processFolder);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		if (this.getProcessState() != EProcessState.DOMAIN_DEFINITION) { // no need to update state
			super.updateProcessState(EProcessState.CREATED);
			// domain is already known after attaching
			super.updateProcessState(EProcessState.DOMAIN_DEFINITION);
		}

	}

	@Override
	public void updateInterview(Map<String, String> answers) throws InvalidStateTransitionException {
		super.updateProcessState(EProcessState.INTERVIEW);
		if (this.answers == null) {
			this.answers = new HashMap<>();
		}
		this.answers.putAll(answers);

		File interviewFile = new File(this.processEnvironment.getInterviewDirectory().getAbsolutePath() + File.separator + "interview.yaml");
		Parser parser = new Parser();
		InterviewFillout fillout = null;
		try {
			fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
			fillout.updateAnswers(this.answers);
			fillout = new InterviewFillout(fillout.getInterview(), fillout.getAnswers());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		SerializationUtil.writeAsJSON(this.processEnvironment.getInterviewStateFile(), fillout);

	}

	@Override
	protected void extractPrototype() throws PrototypeCouldNotBeExtractedException, InvalidStateTransitionException {
		super.extractPrototype();
		File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + this.processId);

		// update environment with prototype info
		try {
			this.processEnvironment = new PROSECOProcessEnvironment(processFolder);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

}
