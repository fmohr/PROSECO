package de.upb.crc901.proseco.core.composition;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.upb.crc901.proseco.commons.config.GlobalConfig;
import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.config.ProcessConfig;
import de.upb.crc901.proseco.commons.controller.DefaultPrototypeExtractor;
import de.upb.crc901.proseco.commons.controller.GroundingNotSuccessfulForAnyStrategyException;
import de.upb.crc901.proseco.commons.controller.NoStrategyFoundASolutionException;
import de.upb.crc901.proseco.commons.controller.PROSECOSolution;
import de.upb.crc901.proseco.commons.controller.ProcessController;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.processstatus.ProcessStateProvider;
import de.upb.crc901.proseco.commons.processstatus.ProcessStateTransitionController;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;
import de.upb.crc901.proseco.commons.util.SerializationUtil;

/**
 * Abstract class that implements {@link ProcessController} interface
 *
 * @author kadirayk
 *
 */
public abstract class AProsecoConfigurationProcess implements ProcessController {

	private EProcessState processState;
	protected String processId;
	protected PROSECOProcessEnvironment processEnvironment;
	protected int timeoutInSeconds;
	protected static final Logger logger = LoggerFactory.getLogger(AProsecoConfigurationProcess.class);
	private static final GlobalConfig GLOBAL_CONFIG = ConfigFactory.create(GlobalConfig.class);
	private String domain;
	protected String prototype;
	protected Map<String, String> answers;
	protected PROSECOSolution solution;

	/**
	 * Default constructor
	 */
	public AProsecoConfigurationProcess() {
		this.processState = EProcessState.INIT;
	}

	@Override
	public String getProcessId() {
		return this.processId;
	}

	protected void setProcessId(final String processId) {
		this.processId = processId;
	}

	@Override
	public void createNew() throws InvalidStateTransitionException {
		this.updateProcessState(EProcessState.CREATED);
	}

	@Override
	public void fixDomain(final String domain) throws InvalidStateTransitionException {
		this.domain = domain;
		this.updateProcessState(EProcessState.DOMAIN_DEFINITION);

	}

	protected void extractPrototype() throws PrototypeCouldNotBeExtractedException, InvalidStateTransitionException {
		final DefaultPrototypeExtractor prototypeExtractor = new DefaultPrototypeExtractor();
		this.prototype = prototypeExtractor.getPrototype(this.domain, this.answers);
		this.updateProcessState(EProcessState.PROTOTYPE_EXTRACTED);
	}

	@Override
	public PROSECOSolution startComposition(final int timeoutInSeconds) throws NoStrategyFoundASolutionException, InvalidStateTransitionException, PrototypeCouldNotBeExtractedException {
		this.updateProcessState(EProcessState.COMPOSITION);

		this.extractPrototype();

		final int secondsReservedForGrounding = this.processEnvironment.getPrototypeConfig().getSecondsReservedForGrounding();
		final int secondsReservedForDeployment = this.processEnvironment.getPrototypeConfig().getSecondsReservedForDeployment();
		final int timeout = Math.max(1, this.timeoutInSeconds - (secondsReservedForGrounding + secondsReservedForDeployment));
		logger.debug("Create command for executing strategies and execute them with a timeout of {} = max(1, specifiedTimeout - (secondsForGrounding + secondsForDeployment)) = max(1, {} - ({} + {})) ...", timeout, this.timeoutInSeconds,
				secondsReservedForGrounding, secondsReservedForDeployment);
		final StrategyExecutor executeStrategiesCommand = new StrategyExecutor(this.processEnvironment);
		try {
			this.updateProcessState(EProcessState.STRATEGY_CHOSEN);
			executeStrategiesCommand.execute(timeout * 1000);
		} catch (IOException | InterruptedException e1) {
			logger.error(e1.getMessage());
			Thread.currentThread().interrupt();
		}
		logger.info("Execution of strategies finished!");

		/* execute hooks that should run after configuration */

		/* determine strategy that delivered the best solution */
		Optional<File> winningStrategy = Optional.empty();
		double bestScoreSeen = Double.MAX_VALUE;
		for (final File strategy : this.processEnvironment.getStrategyDirectory().listFiles()) {
			if (!strategy.isDirectory()) {
				continue;
			}
			final File fValueFile = this.getFvalueFile(strategy);
			if (!fValueFile.exists()) {
				continue;
			}
			final Double parsedValue = this.getParsedScore(fValueFile);
			if (parsedValue < bestScoreSeen) {
				winningStrategy = Optional.of(strategy);
				bestScoreSeen = parsedValue;
			}
			logger.info("Strategy {} reports a solution performance of {}", strategy.getName(), parsedValue);
		}
		if (!winningStrategy.isPresent()) {
			logger.info("None of the strategies has found a solution.");
			throw new NoStrategyFoundASolutionException();
		}
		logger.info("Identified {} as a winning strategy with score {}", winningStrategy.get(), bestScoreSeen);
		final PROSECOSolution prosecoSolution = new PROSECOSolution();
		prosecoSolution.setProcessId(this.processId);
		prosecoSolution.setWinningScore(bestScoreSeen);
		prosecoSolution.setWinningStrategyFolder(winningStrategy.get());
		this.solution = prosecoSolution;
		return prosecoSolution;
	}

	private Double getParsedScore(final File fValueFile) {
		Double parsedValue = Double.MAX_VALUE;
		try {
			parsedValue = Double.parseDouble(FileUtils.readFileToString(fValueFile, Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			logger.error(e.getMessage());
		}
		return parsedValue;
	}

	public PROSECOSolution getSolution() {
		return this.solution;
	}

	@Override
	public void chooseAndDeploySolution(PROSECOSolution solution) throws InvalidStateTransitionException, GroundingNotSuccessfulForAnyStrategyException {
		this.updateProcessState(EProcessState.GROUNDING);
		if (solution == null) {
			if (this.solution != null) {
				solution = this.solution;
			} else {
				return;
			}
		}
		try {
			ProcessStateProvider.setProcessStatus(this.processEnvironment.getProcessId(), EProcessState.GROUNDING);
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		this.handleGrounding(solution);

		/*
		 * deploy service. First determine available port. Then execute the deployment.
		 */
		this.handleDeployment();
		this.updateProcessState(EProcessState.DONE);
		ProcessStateProvider.setProcessStatus(this.processEnvironment.getProcessId(), EProcessState.DONE);

	}

	private void handleDeployment() throws InvalidStateTransitionException {
		this.updateProcessState(EProcessState.DEPLOYMENT);
		ProcessStateProvider.setProcessStatus(this.processEnvironment.getProcessId(), EProcessState.DEPLOYMENT);
		final String host = this.processEnvironment.getPrototypeConfig().getDeploymentHost();
		int port = this.processEnvironment.getPrototypeConfig().getDeploymentMinPort();
		boolean portIsAvailable = true;
		do {
			portIsAvailable = true;
			try (Socket s = new Socket(host, port)) {
				portIsAvailable = false;
				port++;
			} catch (final IOException ex) {
				logger.error(ex.getMessage());
			}
		} while (!portIsAvailable);
		final String[] deploymentCommand = new String[4];
		deploymentCommand[0] = this.processEnvironment.deploymentExecutable().getAbsolutePath();
		deploymentCommand[1] = this.processEnvironment.getProcessId();
		deploymentCommand[2] = host;
		deploymentCommand[3] = "" + port;
		if (!new File(deploymentCommand[0]).setExecutable(true)) {
			logger.error("cannot set deploymentCommand as executable");
		}
		logger.info("Deploying service {} to {}:{}", deploymentCommand[1], deploymentCommand[2], deploymentCommand[3]);
		final ProcessBuilder pb = new ProcessBuilder(deploymentCommand);
		pb.redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
		if (GLOBAL_CONFIG.debugMode() && (GLOBAL_CONFIG.debugDisableDeployment() || GLOBAL_CONFIG.debugDisableGrounding())) {
			if (GLOBAL_CONFIG.debugDisableGrounding()) {
				logger.warn("Deployment has been disabled as grounding routine was disabled for debugging. To enable both check");
			} else {
				logger.warn("Deployment has been disabled for debugging! You can enable it in the GlobalConfig properties.");
			}
		} else {
			try {
				pb.start().waitFor();
			} catch (InterruptedException | IOException e) {
				logger.error(e.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		logger.info("Deployment completed.");

		/* create handle file */
		try {
			FileUtils.writeStringToFile(this.processEnvironment.getServiceHandle(), "http://" + deploymentCommand[2] + ":" + port + "/" + this.processEnvironment.getPrototypeConfig().getDeploymentEntryPoint(), Charset.defaultCharset());
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void handleGrounding(final PROSECOSolution solution) throws GroundingNotSuccessfulForAnyStrategyException {
		int groundingStatus = 0;
		final String[] groundingCommand = new String[4];
		groundingCommand[0] = this.processEnvironment.groundingExecutable().getAbsolutePath();
		groundingCommand[1] = this.processEnvironment.getProcessId();
		groundingCommand[2] = this.processEnvironment.getSearchOutputDirectory().getAbsolutePath() + File.separator + solution.getWinningStrategyFolder().getName();
		groundingCommand[3] = this.processEnvironment.getSearchOutputDirectory().getAbsolutePath() + File.separator + "final";
		if (!new File(groundingCommand[0]).setExecutable(true)) {
			logger.error("cannot set groundingCommand as executable");
		}
		final ProcessBuilder pb = new ProcessBuilder(groundingCommand).directory(this.processEnvironment.getGroundingDirectory());
		pb.redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
		if (logger.isInfoEnabled()) {
			logger.info("Execute grounding command {}. Working directory is set to {}", Arrays.toString(groundingCommand), this.processEnvironment.getGroundingDirectory());
		}
		if (GLOBAL_CONFIG.debugMode() && GLOBAL_CONFIG.debugDisableGrounding()) {
			logger.warn("Grounding has been disabled for debugging! You can enable it in the GlobalConfig properties.");
		} else {
			try {
				final Process p = pb.start();
				p.waitFor();
				groundingStatus = p.exitValue();
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		}
		if (groundingStatus != 0) {
			try {
				this.executeGroundingForBackupStrategy(solution.getWinningScore());
			} catch (final IOException e) {
				logger.error(e.getMessage());
			}
		}
		logger.info("Grounding completed.");
	}

	private void executeGroundingForBackupStrategy(double bestScoreSeen) throws GroundingNotSuccessfulForAnyStrategyException, IOException {
		final Entry<Double, File> secondBestStrategy = this.findSecondBestStrategy(bestScoreSeen);
		if (secondBestStrategy == null) {
			logger.error("Grounding did not succeed for any of the strategies");
			throw new GroundingNotSuccessfulForAnyStrategyException();
		}
		bestScoreSeen = secondBestStrategy.getKey();
		this.handleGroundingForBackup(bestScoreSeen, secondBestStrategy);
	}

	private void handleGroundingForBackup(final double bestScoreSeen, final Entry<Double, File> secondBestStrategy) throws IOException, GroundingNotSuccessfulForAnyStrategyException {
		int groundingStatus = 0;
		final String[] groundingCommand = new String[4];
		groundingCommand[0] = this.processEnvironment.groundingExecutable().getAbsolutePath();
		groundingCommand[1] = this.processEnvironment.getProcessId();
		groundingCommand[2] = this.processEnvironment.getSearchOutputDirectory().getAbsolutePath() + File.separator + secondBestStrategy.getValue().getName();
		groundingCommand[3] = this.processEnvironment.getSearchOutputDirectory().getAbsolutePath() + File.separator + "final";
		if (!new File(groundingCommand[0]).setExecutable(true)) {
			logger.error("cannot set groundingCommand as executable");
		}
		final ProcessBuilder pb = new ProcessBuilder(groundingCommand).directory(this.processEnvironment.getGroundingDirectory());
		pb.redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
		if (logger.isInfoEnabled()) {
			logger.info("Execute grounding command {}. Working directory is set to {}", Arrays.toString(groundingCommand), this.processEnvironment.getGroundingDirectory());
		}
		if (GLOBAL_CONFIG.debugMode() && GLOBAL_CONFIG.debugDisableGrounding()) {
			logger.warn("Grounding has been disabled for debugging! You can enable it in the GlobalConfig properties.");
		} else {
			final Process p = pb.start();
			try {
				p.waitFor();
			} catch (final InterruptedException e) {
				logger.error(e.getMessage());
				Thread.currentThread().interrupt();
			}
			groundingStatus = p.exitValue();
		}
		if (groundingStatus != 0) {
			this.executeGroundingForBackupStrategy(bestScoreSeen);

		}
		logger.info("Grounding completed.");
	}

	private Entry<Double, File> findSecondBestStrategy(final Double bestScore) {
		final TreeMap<Double, File> strategiesByScore = new TreeMap<>(Collections.reverseOrder());
		Entry<Double, File> secondBestStrategy = null;
		this.orderStrategiesByScore(strategiesByScore);
		final Entry<Double, File> secondBest = strategiesByScore.lowerEntry(bestScore);
		if (secondBest == null) {
			return null;
		} else {
			secondBestStrategy = strategiesByScore.lowerEntry(bestScore);
		}

		return secondBestStrategy;
	}

	private void orderStrategiesByScore(final TreeMap<Double, File> strategiesByScore) {
		for (final File strategy : this.processEnvironment.getStrategyDirectory().listFiles()) {
			if (!strategy.isDirectory()) {
				continue;
			}
			final File fValueFile = this.getFvalueFile(strategy);
			if (!fValueFile.exists()) {
				continue;
			}
			final Double parsedValue = this.getParsedScore(fValueFile);
			strategiesByScore.put(parsedValue, strategy);
		}
	}

	private File getFvalueFile(final File strategy) {
		final File fValueFile = new File(this.processEnvironment.getSearchOutputDirectory() + File.separator + strategy.getName() + File.separator + "score");
		if (!fValueFile.exists()) {
			logger.info("score file was not found in file {} for strategy {}", fValueFile.getAbsolutePath(), strategy.getName());
		}
		return fValueFile;
	}

	@Override
	public void updateInterview(final Map<String, String> answers) throws InvalidStateTransitionException {
		this.updateProcessState(EProcessState.INTERVIEW);
		if (this.answers == null) {
			this.answers = new HashMap<>();
		}
		this.answers.putAll(answers);

		final File interviewFile = new File(this.processEnvironment.getInterviewDirectory().getAbsolutePath() + File.separator + "interview.yaml");
		final Parser parser = new Parser();
		InterviewFillout fillout = null;
		try {
			fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
			fillout.updateAnswers(this.answers);
			fillout = new InterviewFillout(fillout.getInterview(), fillout.getAnswers());
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}

		SerializationUtil.writeAsJSON(this.processEnvironment.getInterviewStateFile(), fillout);

	}

	protected void createEnvironment(final String domain, final File prosecoConfigFile, final PROSECOConfig config) {
		if (this.processId == null) {
			final String id = domain + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toLowerCase();
			this.processId = id;
		}
		final File processFolder = new File(config.getDirectoryForProcesses() + File.separator + this.processId);

		try {
			FileUtils.forceMkdir(processFolder);
		} catch (final IOException e) {
			// File IO exception is only relevant for FileBasedConfigurationProcess
			logger.error(e.getMessage());
		}

		final ProcessConfig pc = new ProcessConfig(this.processId, domain, prosecoConfigFile);
		try {
			new ObjectMapper().writeValue(new File(processFolder + File.separator + "process.json"), pc);
		} catch (final IOException e1) {
			logger.error(e1.getMessage());
		}
		try {
			this.processEnvironment = new PROSECOProcessEnvironment(processFolder);
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}

	}

	protected void createNewForConfig(final String processId, final PROSECOConfig config) throws ProcessIdAlreadyExistsException, InvalidStateTransitionException {
		if (processId != null) {
			final File processFolder = new File(config.getDirectoryForProcesses() + File.separator + processId);
			if (processFolder.exists()) {
				throw new ProcessIdAlreadyExistsException();
			}
		}

		this.processId = processId;

		this.updateProcessState(EProcessState.CREATED);
	}

	protected void updateProcessState(final EProcessState newState) throws InvalidStateTransitionException {
		this.processState = ProcessStateTransitionController.moveToNextState(this.processState, newState);
	}

	@Override
	public EProcessState getProcessState() {
		return this.processState;
	}

}
