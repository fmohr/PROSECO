package de.upb.crc901.proseco.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.upb.crc901.proseco.commons.interview.Interview;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;

/**
 * Serialization utility class
 *
 * @author kadirayk
 *
 */
public class SerializationUtil {

	private static final Logger logger = LoggerFactory.getLogger(SerializationUtil.class);

	private SerializationUtil() {
	}

	public static void writeAsJSON(final File file, final InterviewFillout interview) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final String path = file.getAbsoluteFile().getAbsolutePath();
			logger.info("Saving interview state {} to {}", interview, path);
			if (!file.getParentFile().exists()) {
				FileUtils.forceMkdir(file.getParentFile());
			}
			mapper.writeValue(file.getAbsoluteFile(), interview);
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
		final boolean fileExists = file.getAbsoluteFile().exists();
		logger.info("File exists: {}", fileExists);
	}

	public static InterviewFillout readAsJSON(final File file) {
		InterviewFillout interview = null;
		final ObjectMapper mapper = new ObjectMapper();
		try {
			interview = mapper.readValue(file, InterviewFillout.class);
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
		return interview;
	}

	public static void write(final String path, final Interview interview) {
		final String filePath = path + "interview_state";
		try (FileOutputStream f = new FileOutputStream(new File(filePath)); ObjectOutputStream o = new ObjectOutputStream(f)) {
			o.writeObject(interview);
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
	}

	public static Interview read(final String path) {
		final String filePath = path + "interview_state";
		Interview interview = null;
		try (FileInputStream f = new FileInputStream(new File(filePath)); ObjectInputStream o = new ObjectInputStream(f)) {
			interview = (Interview) o.readObject();
		} catch (IOException | ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
		return interview;
	}

}
