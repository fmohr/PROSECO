package de.upb.crc901.proseco.commons.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File Utility class
 *
 * @author kadirayk
 *
 */
public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil() {
	}

	/**
	 * Write given string content to given filePath
	 *
	 * @param filePath path in file system
	 * @param content string value
	 */
	public static void writeToFile(final String filePath, final String content) {
		PrintWriter writer;
		try {
			final File output = new File(filePath);
			writer = new PrintWriter(output);
			writer.print(content);
			writer.close();
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Returns content of a file as String by given filePath
	 *
	 * @param filePath path in file system
	 * @return String content
	 */
	public static String readFile(final String filePath) {
		byte[] encoded = null;
		String content = null;
		try {
			encoded = Files.readAllBytes(Paths.get(filePath));
		} catch (final NoSuchFileException e) {
			return content;
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
		try {
			if (encoded != null) {
				content = new String(encoded, "utf-8");
			}
		} catch (final UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		return content;
	}

	/**
	 * Returns the content of a file after given line number
	 *
	 * @param filePath path in file system
	 * @param line line number in file
	 * @return content of the file after the given line number
	 */
	public static String readFileByLineNumber(final String filePath, final Integer line) {
		String content = readFile(filePath);
		if (content != null) {
			final String[] arr = content.split("\n");
			final StringBuilder str = new StringBuilder();
			if (arr.length > line) {
				for (int i = line; i < arr.length; i++) {
					str.append(arr[i]).append("\n");
				}
				content = str.toString();
			}
		}
		return content;
	}

}
