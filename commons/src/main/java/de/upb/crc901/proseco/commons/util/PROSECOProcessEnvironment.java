package de.upb.crc901.proseco.commons.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.aeonbits.owner.ConfigCache;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.upb.crc901.proseco.commons.config.DomainConfig;
import de.upb.crc901.proseco.commons.config.GlobalConfig;
import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.config.ProcessConfig;
import de.upb.crc901.proseco.commons.config.PrototypeConfig;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;

/**
 * ExecutionEnvironment, is the directory where an instance of the selected
 * prototype is created.
 *
 * @author fmohr
 *
 */
public class PROSECOProcessEnvironment {

	/* logging */
	private static final Logger L = LoggerFactory.getLogger(PROSECOProcessEnvironment.class);

	/* Global config for system-wide constants */
	private static final GlobalConfig GLOBAL_CONFIG = ConfigCache.getOrCreate(GlobalConfig.class);

	/* meta data: PROSECO and its runtime environment */
	private enum OperatingSystem {
		WINDOWS, NON_WINDOWS;
	}

	private final PROSECOConfig prosecoConfig;
	private final OperatingSystem os;

	/* process-specific */
	private final String processId;
	private final File processDirectory;

	/* domain-specific but process unspecific */
	private final File interviewDirectory; // original interview files

	/*
	 * domain-specific AND process-specific (specific to domain but not specific to
	 * prototype)
	 */
	private final File domainDirectory;
	private final DomainConfig domainConfig;
	private final File interviewStateDirectory;
	private final File interviewStateFile;
	private final File interviewResourcesDirectory;

	/* prototype-specific */
	private final File prototypeDirectory;
	private final PrototypeConfig prototypeConfig;
	private final String prototypeName;
	private final File strategyDirectory;
	private final File benchmarksDirectory;
	private final File groundingDirectory;
	private final File groundingFile;
	private final File deploymentFile;

	/* configuration-process-specific (specific to prototype) */
	private final File searchDirectory;
	private final File analysisRoutineExecutable;
	private final InterviewFillout interviewFillout;

	/**
	 * Default constructor
	 *
	 * @param processFolder The process folder MUST, by convention, contain a
	 *            process.json that contains its id, the domain, the
	 *            prototype, and the proseco configuration that is used to
	 *            run it
	 * @throws IOException thrown on IO error in File System
	 */
	public PROSECOProcessEnvironment(final File processFolder) throws IOException {
		L.debug("Initializing PROSECO process environment for process folder {}.", processFolder.getAbsolutePath());

		final ProcessConfig processConfig = this.loadAndValidateProcessConfig(processFolder);

		/* Figure out what operating system PROSECO is running in. */
		this.os = (SystemUtils.IS_OS_WINDOWS ? OperatingSystem.WINDOWS : OperatingSystem.NON_WINDOWS);
		final String osName = this.os.name();
		L.debug("Detected {} operating system.", osName);

		/* read PROSECO configuration and configure process */
		final File prosecoConfigFile = processConfig.getProsecoConfigFile().isAbsolute() ? processConfig.getProsecoConfigFile() : new File(processFolder + File.separator + processConfig.getProsecoConfigFile());
		L.debug("Load PROSECO config from {}", prosecoConfigFile);
		this.prosecoConfig = PROSECOConfig.get(prosecoConfigFile);

		/* current process specific data. */
		this.processId = processConfig.getProcessId();
		this.processDirectory = processFolder;
		L.debug("Current process with process id {} is located in {}", this.processId, this.processDirectory);

		/* General domain directories and config */
		this.domainDirectory = new File(this.prosecoConfig.getDirectoryForDomains() + File.separator + processConfig.getDomain()).getCanonicalFile();
		this.domainConfig = DomainConfig.get(this.domainDirectory + File.separator + "domain.conf");
		L.debug("Domain directory is set to {} and domain config is loaded from file {}", this.domainDirectory, new File(this.domainDirectory + File.separator + "domain.conf"));

		/* domain specific folders */
		this.interviewDirectory = new File(this.domainDirectory + File.separator + this.domainConfig.getNameOfInterviewFolder());
		this.interviewStateDirectory = new File(this.processDirectory + File.separator + this.domainConfig.getNameOfInterviewFolder());
		this.interviewStateFile = new File(this.interviewStateDirectory + File.separator + this.domainConfig.getNameOfInterviewStateFile());
		this.interviewResourcesDirectory = new File(this.interviewStateDirectory + File.separator + this.domainConfig.getNameOfInterviewResourceFolder());

		this.searchDirectory = new File(this.processDirectory, "search");

		/* extract prototype from interview */
		L.debug("Trying to read interview from file {}. Existent: {}", this.interviewStateFile.getAbsolutePath(), this.interviewStateFile.exists());
		this.interviewFillout = this.interviewStateFile.exists() ? SerializationUtil.readAsJSON(this.interviewStateFile) : null;
		L.debug("Interview fillout is {}", this.interviewFillout);

		/* prototype specific folders if prototype has been set in the interview */
		if (this.interviewFillout != null && this.interviewFillout.getAnswer("prototype") != null) {
			this.prototypeName = this.interviewFillout.getAnswer("prototype");
			this.prototypeDirectory = new File(this.domainDirectory + File.separator + this.domainConfig.getPrototypeFolder() + File.separator + this.prototypeName);
			this.prototypeConfig = PrototypeConfig.get(this.prototypeDirectory + File.separator + "prototype.conf");
			this.benchmarksDirectory = new File(this.prototypeDirectory + File.separator + this.prototypeConfig.getBenchmarkPath());
			this.groundingDirectory = new File(this.prototypeDirectory + File.separator + this.prototypeConfig.getNameOfGroundingFolder());
			this.groundingFile = this.appendExecutableScriptExtension(new File(this.groundingDirectory + File.separator + this.prototypeConfig.getGroundingCommand())).getCanonicalFile();
			this.strategyDirectory = new File(this.prototypeDirectory + File.separator + this.prototypeConfig.getNameOfStrategyFolder());
			this.analysisRoutineExecutable = new File(this.prototypeDirectory + File.separator + this.prototypeConfig.getHookForPreGrounding());
			this.deploymentFile = this.appendExecutableScriptExtension(new File(this.prototypeDirectory + File.separator + this.prototypeConfig.getDeploymentCommand()));
		} else {
			L.debug("Either the interview is not filled out or the prototype has not been set. So setting all prototype specific configs to null");
			this.prototypeName = null;
			this.prototypeDirectory = null;
			this.prototypeConfig = null;
			this.benchmarksDirectory = null;
			this.groundingDirectory = null;
			this.groundingFile = null;
			this.strategyDirectory = null;
			this.analysisRoutineExecutable = null;
			this.deploymentFile = null;
		}

	}

	private ProcessConfig loadAndValidateProcessConfig(final File processFolder) throws IOException {
		/* read the process.json */
		final String processConfigFilename = GLOBAL_CONFIG.processConfigFilename();
		final File processConfigFile = new File(processFolder, processConfigFilename);
		if (!processConfigFile.exists()) {
			throw new FileNotFoundException("Cannot create a PROSECOProcess environment for a folder without " + processConfigFilename);
		}
		final ProcessConfig processConfig = new ObjectMapper().readValue(processConfigFile, ProcessConfig.class);
		if (processConfig.getProcessId() == null) {
			throw new IllegalArgumentException("The " + processConfigFilename + " MUST define a process id");
		}
		if (processConfig.getDomain() == null) {
			throw new IllegalArgumentException("The " + processConfigFilename + " MUST define a domain");
		}
		return processConfig;
	}

	public File getPrototypeDirectory() {
		return this.prototypeDirectory;
	}

	public File getBenchmarksDirectory() {
		return this.benchmarksDirectory;
	}

	public File getGroundingDirectory() {
		return this.groundingDirectory;
	}

	public File getStrategyDirectory() {
		return this.strategyDirectory;
	}

	public File getInterviewDirectory() {
		return this.interviewDirectory;
	}

	public File getInterviewStateDirectory() {
		return this.interviewStateDirectory;
	}

	public File getInterviewStateFile() {
		return this.interviewStateFile;
	}

	public File getInterviewResourcesDirectory() {
		return this.interviewResourcesDirectory;
	}

	public PROSECOConfig getProsecoConfig() {
		return this.prosecoConfig;
	}

	public PrototypeConfig getPrototypeConfig() {
		return this.prototypeConfig;
	}

	public String getPrototypeName() {
		return this.prototypeName;
	}

	public File getSearchDirectory() {
		return this.searchDirectory;
	}

	public File getServiceHandle() {
		return new File(this.getProcessDirectory() + File.separator + "service.handle");
	}

	/*
	 * ################################################## Block: Dynamic Prototype
	 * Specific Configs ##################################################
	 */
	/**
	 * @return The ID of the current process.
	 */
	public String getProcessId() {
		return this.processId;
	}

	/**
	 * @return The directory of the current process.
	 */
	public File getProcessDirectory() {
		return this.processDirectory;
	}

	/**
	 * @return The input directory for the search.
	 */
	public File getSearchInputDirectory() {
		return new File(this.searchDirectory + File.separator + "inputs");
	}

	/**
	 * @return The output directory of the search.
	 */
	public File getSearchOutputDirectory() {
		return new File(this.searchDirectory + File.separator + "outputs");
	}

	/**
	 * Returns a strategy's output directory for a given strategy.
	 *
	 * @param strategy The name of the strategy for which the output directory shall
	 *            be provided.
	 * @return The output directory of the given strategy.
	 */
	public File getSearchStrategyOutputDirectory(final String strategy) {
		return new File(this.getSearchOutputDirectory() + File.separator + strategy);
	}

	/*
	 * ################################################## Block: Static Prototype
	 * Specific Configs ##################################################
	 */

	/*
	 * ################################################## Block: Prototype
	 * Executable Scripts ##################################################
	 */

	/**
	 * @return Returns the executable script for the verification of candidate
	 *         solutions.
	 */
	public File verificationExecutable() {
		return this.analysisRoutineExecutable;
	}

	/**
	 * @return Returns the executable for the deployment of the solution.
	 */
	public File deploymentExecutable() {
		return this.deploymentFile;
	}

	/**
	 * @return Returns the executable for the grounding of a candidate.
	 */
	public File groundingExecutable() {
		return this.groundingFile;
	}

	/*
	 * ################################################## Block: Utils and Other
	 * ##################################################
	 */

	/**
	 * Appends the OS specific script file extension for executable scripts, i.e.
	 * either .bat or .sh.
	 *
	 * @param file The file to which the file extension is to be appended.
	 * @return The prepared file with OS specific file extension.
	 */
	public File appendExecutableScriptExtension(final File file) {
		if (file.getName().contains(".")) {
			throw new IllegalArgumentException("The file already has a file extension.");
		}

		String extension;
		if (this.os == OperatingSystem.WINDOWS) {
			extension = GLOBAL_CONFIG.scriptExtensionWindows();
		} else {
			extension = GLOBAL_CONFIG.scriptExtensionNonWindows();
		}
		return new File(file.getAbsolutePath() + extension);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("##############################\n");
		sb.append("# Environment				#\n");
		sb.append("##############################\n");

		sb.append("Proseco Config: " + this.prosecoConfig + "\n");
		sb.append("Process ID: " + this.getProcessId() + "\n");
		sb.append("Domain Config: " + this.domainConfig + "\n");
		sb.append("Prototype Config: " + this.prototypeConfig + "\n");
		sb.append("Prototype Name: " + this.prototypeName + "\n");
		sb.append("Interview Fillout: " + this.interviewFillout + "\n\n");

		final Map<String, File> fileMap = new HashMap<>();
		fileMap.put("Process Directory", this.processDirectory);
		fileMap.put("Interview Directory: ", this.interviewDirectory);
		fileMap.put("Domain Directory: ", this.domainDirectory);
		fileMap.put("Prototype Directory: ", this.prototypeDirectory);
		fileMap.put("Strategy Directory: ", this.strategyDirectory);
		fileMap.put("Benchmark Directory: ", this.benchmarksDirectory);
		fileMap.put("Grounding Directory: ", this.groundingDirectory);
		fileMap.put("Grounding File: ", this.groundingFile);
		fileMap.put("Search Directory: ", this.searchDirectory);
		fileMap.put("Analysis Routine: ", this.analysisRoutineExecutable);

		for (final Entry<String, File> file : fileMap.entrySet()) {
			sb.append(file.getKey() + ": " + ((file.getValue() == null) ? "null" : file.getValue().getAbsolutePath()) + "\n");
		}

		return sb.toString();
	}
}
