package de.upb.crc901.proseco.commons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface PROSECOConfig extends Mutable, Accessible {
	/* phase 1: identify prototype */
	public static final String DOMAINS_PATH = "proseco.domains";
	public static final String PROCESS_PATH = "proseco.processes";

	/* behavior where to put outputs */
	public static final String OUTPUT_DIR = "proseco.output_directory";

	/* where to store log information */
	public static final String SYSTEM_OUT_FILE = "proseco.system_out_file";
	public static final String SYSTEM_ERR_FILE = "proseco.system_err_file";
	public static final String SYSTEM_ALL_FILE = "proseco.system_all_file";
	public static final String SERVICE_LOG_FILE = "proseco.service_log_file";

	/*
	 * whether or not PROSECO composition should clean up the working directory at
	 * the end
	 */
	public static final String FINAL_CLEAN_UP = "proseco.final_clean_up";

	@Key(DOMAINS_PATH)
	@DefaultValue("domains")
	public File getDirectoryForDomains();

	@Key(PROCESS_PATH)
	@DefaultValue("processes")
	public File getDirectoryForProcesses();

	@Key(FINAL_CLEAN_UP)
	@DefaultValue("true")
	public boolean isFinalCleanupEnabled();

	@Key(OUTPUT_DIR)
	@DefaultValue("out")
	public String getNameOfOutputFolder();

	@Key(SERVICE_LOG_FILE)
	@DefaultValue("service_log")
	public String getNameOfServiceLogFile();

	@Key(SYSTEM_OUT_FILE)
	@DefaultValue("console.out")
	public String getSystemOutFileName();

	@Key(SYSTEM_ERR_FILE)
	@DefaultValue("console.err")
	public String getSystemErrFileName();

	@Key(SYSTEM_ALL_FILE)
	@DefaultValue("console.all")
	public String getSystemMergedOutputFileName();

	public static PROSECOConfig get(String file) {
		return get(new File(file));
	}

	public static PROSECOConfig get(File file) {
		Properties props = new Properties();
		final Logger logger = LoggerFactory.getLogger(PROSECOConfig.class);
		try {
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("Could not find config file {}. Assuming default configuration", file);
		} catch (IOException e) {
			logger.error("Encountered problem with config file {}. Assuming default configuration. Problem: {}", file, e.getMessage());
		}
		return ConfigFactory.create(PROSECOConfig.class, props);
	}
}
