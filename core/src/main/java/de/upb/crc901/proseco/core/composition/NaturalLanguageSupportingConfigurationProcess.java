package de.upb.crc901.proseco.core.composition;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.controller.CannotFixDomainInThisProcessException;
import de.upb.crc901.proseco.commons.controller.DefaultDomainScoreComputer;
import de.upb.crc901.proseco.commons.controller.DomainCouldNotBeDetectedException;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.ProcessIdDoesNotExistException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;

/**
 * Implementation of {@link AProsecoConfigurationProcess} for natural language supporting configuration
 *
 * @author kadirayk
 *
 */
public class NaturalLanguageSupportingConfigurationProcess extends AProsecoConfigurationProcess {

	private final File prosecoConfigFile;
	private final PROSECOConfig config;

	/**
	 * Constructor that creates a {@link NaturalLanguageSupportingConfigurationProcess} with given prosecoConfigFile
	 *
	 * @param prosecoConfigFile file that points to the prosecoConfigFile that configures the values described in {@link PROSECOConfig}
	 */
	public NaturalLanguageSupportingConfigurationProcess(final File prosecoConfigFile) {
		try {
			super.updateProcessState(EProcessState.INIT);
		} catch (final InvalidStateTransitionException e) {
			logger.error(e.getMessage());
		}
		this.prosecoConfigFile = prosecoConfigFile;
		this.config = PROSECOConfig.get(prosecoConfigFile);
	}

	/**
	 * Method that accepts a general task description and detects the desired domain from the given description.
	 * A description can be any type of class, String type is currently supported
	 *
	 * @param description Task description that can be in any type
	 * @throws DomainCouldNotBeDetectedException thrown when domain could not be detected
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	public <T> void receiveGeneralTaskDescription(final T description) throws DomainCouldNotBeDetectedException, InvalidStateTransitionException {
		if (description instanceof String) {
			final String descriptionString = (String) description;
			final DefaultDomainScoreComputer domainScoreComputer = new DefaultDomainScoreComputer();
			final List<String> avilableDomains = domainScoreComputer.getAvailableDomains(this.config);

			Double bestScore = 0.0;
			String detectedDomain = null;
			for (final String domain : avilableDomains) {
				final Double score = domainScoreComputer.getDomainScore(descriptionString, domain);
				if (score > bestScore) {
					bestScore = score;
					detectedDomain = domain;
				}
			}
			super.fixDomain(detectedDomain);
			this.createEnvironment(detectedDomain, this.prosecoConfigFile, this.config);
			this.answerInterview();
		}
	}

	private void answerInterview() throws InvalidStateTransitionException {
		this.updateProcessState(EProcessState.INTERVIEW);
		final File interviewFile = new File(this.processEnvironment.getInterviewDirectory().getAbsolutePath() + File.separator + "interview.yaml");
		final Parser parser = new Parser();
		InterviewFillout fillout = null;
		try {
			fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
		final Map<String, String> answers = fillout.retrieveQuestionAnswerMap();
		for (final String question : answers.keySet()) {
			final String answer = this.askOracle(question);
			answers.put(question, answer);
		}
		this.updateInterview(answers);
	}

	private String askOracle(final String question) {
		String answer = null;
		if (question.equals("Please select prototype")) {
			answer = "test1";
		}
		return answer;
	}

	@Override
	public void fixDomain(final String domain) {
		throw new CannotFixDomainInThisProcessException();
	}

	@Override
	public void createNew(final String processId) throws ProcessIdAlreadyExistsException, InvalidStateTransitionException {
		this.createNewForConfig(processId, this.config);
	}

	@Override
	public void attach(final String processId) throws ProcessIdDoesNotExistException, InvalidStateTransitionException {
		final File processFolder = new File(this.config.getDirectoryForProcesses() + File.separator + processId);
		if (!processFolder.exists()) {
			throw new ProcessIdDoesNotExistException();
		}
		this.processId = processId;
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

	@Override
	public PROSECOProcessEnvironment getProcessEnvironment() {
		return this.processEnvironment;
	}

}
