package de.upb.crc901.proseco.commons.config;

import java.io.File;

import org.aeonbits.owner.Config;

/**
 * This config interface provides basic constants for the PROSECO environment.
 *
 * @author wever
 */
public interface GlobalConfig extends Config {

	public static final String K_DEBUG_MODE = "proseco.debug.mode";
	public static final String K_REDIRECT_PROCESS_OUTPUTS = "proseco.debug.redirectoutputs";
	public static final String K_DISABLE_GROUNDING = "proseco.debug.disableGrounding";
	public static final String K_DISABLE_DEPLOYMENT = "proseco.debug.disableDeployment";
	public static final String K_PROSECO_CONFIG_FILE = "proseco.config_file";
	public static final String K_SCRIPT_EXTENSION_WIN = "proseco.script.extension.windows";
	public static final String K_SCRIPT_EXTENSION_NOWIN = "proseco.script.extension.nonwindows";
	public static final String K_PROCESS_CONFIG_FILENAME = "proseco.process.config_filename";

	/**
	 * @return A flag stating whether PROSECO is executed in DEBUG mode.
	 */
	@Key(K_DEBUG_MODE)
	@DefaultValue("false")
	public boolean debugMode();

	@Key(K_REDIRECT_PROCESS_OUTPUTS)
	@DefaultValue("false")
	public boolean redirectProcessOutputs();

	@Key(K_DISABLE_DEPLOYMENT)
	@DefaultValue("true")
	public boolean debugDisableDeployment();

	@Key(K_DISABLE_GROUNDING)
	@DefaultValue("true")
	public boolean debugDisableGrounding();

	/**
	 * @return The file of the proseco config.
	 */
	@Key(K_PROSECO_CONFIG_FILE)
	@DefaultValue("res/proseco.conf")
	public File prosecoConfigFile();

	/**
	 * @return File extension for script files on Windows systems.
	 */
	@Key(K_SCRIPT_EXTENSION_WIN)
	@DefaultValue(".bat")
	public String scriptExtensionWindows();

	/**
	 * @return File extension for script files on non-Windows systems.
	 */
	@Key(K_SCRIPT_EXTENSION_NOWIN)
	@DefaultValue(".sh")
	public String scriptExtensionNonWindows();

	@Key(K_PROCESS_CONFIG_FILENAME)
	@DefaultValue("process.json")
	public String processConfigFilename();

}
