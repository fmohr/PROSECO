package de.upb.crc901.proseco.commons.processstatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.proseco.commons.controller.DefaultProcessController;
import de.upb.crc901.proseco.commons.util.FileUtil;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

/**
 *
 * Utility class for keeping track of the process state
 *
 */
public class ProcessStateProvider {

	private static final Logger logger = LoggerFactory.getLogger(ProcessStateProvider.class);

	private static final String NO_PROCESS_PROCESSID = "NaN";
	private static final EProcessState DEFAULT_PROCESS_STATE = EProcessState.DOMAIN_DEFINITION;

	private static final DefaultProcessController PROCESS_CONTROLLER = new DefaultProcessController(new File("conf/proseco.conf"));
	private static final Map<String, PROSECOProcessEnvironment> envCache = new HashMap<>();

	private ProcessStateProvider() {
	}

	/**
	 * Returns current status of the process by given processId
	 * Status of the processes are stored in "process.status" file in the process directory
	 *
	 * @param processID consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @return Status of the process as String
	 */
	public static String getProcessStatus(final String processID) {
		if (processID.equals(NO_PROCESS_PROCESSID)) {
			return DEFAULT_PROCESS_STATE.toString();
		}
		final PROSECOProcessEnvironment env = getProcessEnvironment(processID);
		final File processStatus = new File(env.getProcessDirectory(), "process.status");
		if (processStatus.exists()) {
			return FileUtil.readFile(processStatus.getAbsolutePath());
		} else {
			setProcessStatus(processID, DEFAULT_PROCESS_STATE);
			return DEFAULT_PROCESS_STATE.toString();
		}
	}

	/**
	 * Updates the status of the process by given processId and with given status
	 * Status of the processes are stored in "process.status" file in the process directory
	 *
	 * @param processID consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @param newStatus new status as {@link EProcessState}
	 */
	public static void setProcessStatus(final String processID, final EProcessState newStatus) {
		final PROSECOProcessEnvironment env = getProcessEnvironment(processID);
		final File processStatus = new File(env.getProcessDirectory(), "process.status");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(processStatus))) {
			bw.write(newStatus.toString());
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Returns {@link EProcessState} value of the given state
	 *
	 * @param value
	 * @return
	 */
	public static EProcessState readProcessStateValue(final String value) {
		final Optional<EProcessState> res = Arrays.stream(EProcessState.values()).filter(x -> x.toString().equals(value)).findFirst();
		if (res.isPresent()) {
			return res.get();
		} else {
			throw new NoSuchElementException("No EProcessState member with name " + value);
		}
	}

	/**
	 * returns {@link PROSECOProcessEnvironment} of the given processId
	 *
	 * @param processID consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @param invalidateIfExists if set true remove the processId from cache
	 * @return {@link PROSECOProcessEnvironment} of the given processId
	 */
	public static PROSECOProcessEnvironment getProcessEnvironment(final String processID, final boolean invalidateIfExists) {
		if (invalidateIfExists && envCache.containsKey(processID)) {
			envCache.remove(processID);
		}
		return getProcessEnvironment(processID);
	}

	/**
	 * Returns {@link PROSECOProcessEnvironment} by given processId
	 *
	 * @param processID consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @return PROSECOProcessEnvironment
	 */
	public static PROSECOProcessEnvironment getProcessEnvironment(final String processID) {
		if (envCache.containsKey(processID)) {
			logger.trace("return env from cache for processid {}", processID);
			return envCache.get(processID);
		} else {
			logger.trace("create new process environment for {}", processID);
			final PROSECOProcessEnvironment env = PROCESS_CONTROLLER.getConstructionProcessEnvironment(processID);
			envCache.put(processID, env);
			return env;
		}
	}
}
