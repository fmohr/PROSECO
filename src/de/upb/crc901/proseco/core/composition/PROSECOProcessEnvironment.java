package de.upb.crc901.proseco.core.composition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.upb.crc901.proseco.util.PROSECOConfig;
import de.upb.crc901.proseco.util.PrototypeConfig;

/**
 * ExecutionEnvironment, is the directory where an instance of the selected
 * prototype is created.
 * 
 * @author kadirayk
 *
 */
public class PROSECOProcessEnvironment {
	private final PROSECOConfig prosecoConfig;
	private final PrototypeConfig prototypeConfig;
	private final String prototypeId;
	private final String prototypeName;
	private final File prototypeDirectory;
	private final File executionDirectory;
	private final File processDirectory;
	private final File benchmarksDirectory;
	private final File groundingDirectory;
	private final File strategyDirectory;
//	private final File configDirectory;
	private final File paramsDirectory;
	private final File sourceDirectory;
	private final File libsDirectory;
	private final File interviewDirectory; // original interview files
	private final File interviewStateDirectory;
	private final File interviewResourcesDirectory;
	private final File groundingRoutine;

	public PROSECOProcessEnvironment(final PROSECOConfig pConfig, final String pProcessId) throws FileNotFoundException, IOException {
		prosecoConfig = pConfig;
		prototypeId = pProcessId;
		
		/* prototype specific folders and configs */
		if (!pProcessId.contains("-"))
			throw new IllegalArgumentException("Illegal PROSECO process id " + pProcessId);
		prototypeName = pProcessId.substring(0, pProcessId.lastIndexOf("-"));
		prototypeDirectory = new File(pConfig.getPathToPrototypes() + File.separator + prototypeName);
		prototypeConfig = PrototypeConfig.get(prototypeDirectory + File.separator + "prototype.conf");
		interviewDirectory = new File(prototypeDirectory + File.separator + prototypeConfig.getNameOfInterviewFolder());
		
		/* process specific folders */
		executionDirectory = pConfig.getExecutionFolder();
		processDirectory = new File(executionDirectory + File.separator + prototypeId);
		benchmarksDirectory = new File(processDirectory + File.separator + prototypeConfig.getBenchmarkPath());
		groundingDirectory = new File(processDirectory + File.separator + prototypeConfig.getNameOfGroundingFolder());
		groundingRoutine = new File(groundingDirectory + File.separator + prototypeConfig.getNameOfGroundingRoutine());
		strategyDirectory = new File(processDirectory + File.separator + prototypeConfig.getNameOfStrategyFolder());
//		configDirectory = new File(processDirectory + File.separator + prototype());;
		paramsDirectory = new File(processDirectory + File.separator + prototypeConfig.getPathToParams());
		sourceDirectory = new File(processDirectory + File.separator + prototypeConfig.getPathToSource());
		libsDirectory = new File(processDirectory + File.separator + prototypeConfig.getPathToLibs());
		interviewStateDirectory = new File(processDirectory + File.separator + prototypeConfig.getNameOfInterviewFolder());
		interviewResourcesDirectory = new File(interviewStateDirectory + File.separator + prototypeConfig.getNameOfInterviewResourceFolder());
	}

	public String getPrototypeId() {
		return prototypeId;
	}

	public File getPrototypeDirectory() {
		return prototypeDirectory;
	}

	public File getExecutionDirectory() {
		return executionDirectory;
	}

	public File getBenchmarksDirectory() {
		return benchmarksDirectory;
	}

	public File getGroundingDirectory() {
		return groundingDirectory;
	}

	public File getStrategyDirectory() {
		return strategyDirectory;
	}

//	public File getConfigDirectory() {
//		return configDirectory;
//	}

	public File getParamsDirectory() {
		return paramsDirectory;
	}

	public File getSourceDirectory() {
		return sourceDirectory;
	}

	public File getLibsDirectory() {
		return libsDirectory;
	}

	public File getInterviewDirectory() {
		return interviewDirectory;
	}

	public File getInterviewStateDirectory() {
		return interviewStateDirectory;
	}

	public File getInterviewResourcesDirectory() {
		return interviewResourcesDirectory;
	}

	public File getGroundingRoutine() {
		return groundingRoutine;
	}
	
	public File getProcessDirectory() {
		return processDirectory;
	}

	public PROSECOConfig getProsecoConfig() {
		return prosecoConfig;
	}

	public PrototypeConfig getPrototypeConfig() {
		return prototypeConfig;
	}

	public String getPrototypeName() {
		return prototypeName;
	}
}