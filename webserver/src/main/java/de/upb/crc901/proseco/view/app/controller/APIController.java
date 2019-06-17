package de.upb.crc901.proseco.view.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aeonbits.owner.ConfigCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.upb.crc901.proseco.commons.config.GlobalConfig;
import de.upb.crc901.proseco.commons.config.PROSECOConfig;
import de.upb.crc901.proseco.commons.controller.ProcessController;
import de.upb.crc901.proseco.commons.controller.ProcessIdDoesNotExistException;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.processstatus.ProcessStateProvider;
import de.upb.crc901.proseco.commons.util.FileUtil;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.ToJSONStringUtil;
import de.upb.crc901.proseco.core.composition.FileBasedConfigurationProcess;
import de.upb.crc901.proseco.view.app.model.LogPair;
import de.upb.crc901.proseco.view.app.model.LogResponseBody;
import de.upb.crc901.proseco.view.util.LogLine;
import de.upb.crc901.proseco.view.util.LogLineTracker;

/**
 * API End Point for web service calls
 *
 *
 * @author kadirayk
 *
 */
@RestController
public class APIController {

	/*
	 * Config for basic properties of proseco's environment, e.g., paths to common
	 * properties files.
	 */
	private static final GlobalConfig PROSECO_ENV_CONFIG = ConfigCache.getOrCreate(GlobalConfig.class);

	private final PROSECOConfig config = ConfigCache.getOrCreate(PROSECOConfig.class);

	private final ProcessController processController = new FileBasedConfigurationProcess(PROSECO_ENV_CONFIG.prosecoConfigFile());

	/**
	 * Returns SystemOut and SystemError logs of Strategies of prototype with the
	 * given ID
	 *
	 * @param id consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @return StrategyLogs
	 * @throws Exception
	 */
	@GetMapping(value = "/api/strategyLogs/{id}")
	public ResponseEntity<Object> getStrategyLogs(@PathVariable("id") final String id) {
		final LogResponseBody result = new LogResponseBody();
		result.setLogList(this.findLogById(id));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Returns SystemOut and SystemError logs of Strategies of prototype with the
	 * given ID
	 *
	 * @param id consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @return log as json object
	 * @throws Exception
	 */
	@GetMapping("/api/log/{id}")
	@ResponseBody
	public ResponseEntity<Object> getLog(@PathVariable("id") final String id) {
		final LogResponseBody result = new LogResponseBody();
		result.setLogList(this.findLogById(id));
		return new ResponseEntity<>(Arrays.asList(ToJSONStringUtil.parseObjectToJsonNode(result, new ObjectMapper())), HttpStatus.OK);
	}

	/**
	 * Finds the deployed web application for with the given session id and kills
	 * the process.
	 *
	 * @param id id of the session
	 * @return success if task is killed, failure if exception occured
	 * @throws InvalidStateTransitionException thrown if tried to access invalid state
	 * @throws ProcessIdDoesNotExistException thrown if process with the given id does not exist
	 */
	@GetMapping("/api/stopService/{id}")
	public String stopService(@PathVariable("id") final String id) throws ProcessIdDoesNotExistException, InvalidStateTransitionException {
		final String result = "success";
		final String pid = this.findServicePID(id);
		try {
			final String cmd = "tskill " + pid;
			Runtime.getRuntime().exec(cmd);
		} catch (final IOException e) {
			return "failure";
		}

		return result;
	}

	/**
	 * Returns the service log file content as string with the given id.
	 *
	 * @param id processId
	 * @return
	 * @throws InvalidStateTransitionException thrown if tried to access invalid state
	 * @throws ProcessIdDoesNotExistException thrown if process with the given id does not exist
	 */
	private String getServiceLog(final String id) throws ProcessIdDoesNotExistException, InvalidStateTransitionException {
		this.processController.attach(id);
		final PROSECOProcessEnvironment env = this.processController.getProcessEnvironment();
		final String serviceLogFile = env.getGroundingDirectory() + File.separator + this.config.getNameOfServiceLogFile();
		return FileUtil.readFile(serviceLogFile);
	}

	/**
	 * Finds service's process ID on OS.
	 *
	 * @param id
	 * @return
	 * @throws InvalidStateTransitionException
	 * @throws ProcessIdDoesNotExistException
	 */
	private String findServicePID(final String id) throws ProcessIdDoesNotExistException, InvalidStateTransitionException {
		String pid = null;
		final String serviceLog = this.getServiceLog(id);

		if (serviceLog != null) {
			final String searchStartString = "with PID ";
			final String searchEndString = " ";
			int startIndex = serviceLog.indexOf(searchStartString);
			if (startIndex < 0) {
				return pid;
			}

			startIndex += searchStartString.length();

			final int endIndex = serviceLog.indexOf(searchEndString, startIndex);
			if (endIndex < 0) {
				return pid;
			}
			pid = serviceLog.substring(startIndex, endIndex).trim();
		}

		return pid;

	}

	/**
	 * returns list of log pairs(SystemOut, SystemErr) of strategies of prototype
	 * with the given id
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private List<LogPair> findLogById(final String id) {
		final List<LogPair> logList = new ArrayList<>();
		final PROSECOProcessEnvironment env = ProcessStateProvider.getProcessEnvironment(id);

		final File processFolder = env.getProcessDirectory();
		if (processFolder == null || env.getPrototypeName() == null || !env.getSearchOutputDirectory().exists()) {
			return logList;
		}
		final File strategyDirectory = env.getStrategyDirectory();

		final File[] strategySubFolders = strategyDirectory.listFiles(File::isDirectory);

		final String outputPath = env.getSearchOutputDirectory().getAbsolutePath();
		for (final File strategyFolder : strategySubFolders) {
			final String outputPathOfThisStrategy = outputPath + File.separator + strategyFolder.getName();
			final String systemOut = outputPathOfThisStrategy + File.separator + this.config.getSystemOutFileName();
			final String systemErr = outputPathOfThisStrategy + File.separator + this.config.getSystemErrFileName();
			final String systemAll = outputPathOfThisStrategy + File.separator + this.config.getSystemMergedOutputFileName();

			final LogLine logLine = LogLineTracker.getLogLines(id, strategyFolder.getName());

			final String outLog = FileUtil.readFileByLineNumber(systemOut, logLine.getOutLineNumber());
			final String errLog = FileUtil.readFileByLineNumber(systemErr, logLine.getErrLineNumber());
			final String allLog = FileUtil.readFileByLineNumber(systemAll, logLine.getAllLineNumber());

			logLine.setStrategyName(strategyFolder.getName());
			logLine.setAllLineNumber(logLine.getAllLineNumber() + allLog.split("\n").length);
			logLine.setErrLineNumber(logLine.getErrLineNumber() + errLog.split("\n").length);
			logLine.setOutLineNumber(logLine.getOutLineNumber() + outLog.split("\n").length);
			LogLineTracker.updateLog(id, logLine);

			final LogPair logPair = new LogPair(env.getPrototypeName(), strategyFolder.getName(), outLog, errLog, allLog);
			logList.add(logPair);
		}

		return logList;
	}

}