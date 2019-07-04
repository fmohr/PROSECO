package de.upb.crc901.proseco.commons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unified class for configuration values
 *
 * @author kadirayk, fmohr
 *
 */
public interface PrototypeConfig extends Mutable {

	/* search */
	public static final String STRATEGIES = "pbc.strategies_path";
	public static final String STRATEGY_RUNNABLE = "pbc.strategy.runnable";
	public static final String DISABLED_STRATEGIES = "proseco.disabled.strategies";

	/* pre-grounding filter */
	public static final String PRE_GROUNDING_HOOK = "pbc.hook.preground";

	/* grounding */
	public static final String GROUNDING_FOLDER = "proseco.grounding.folder";
	public static final String GROUNDING_EXEC = "proseco.grounding.executable";
	public static final String GROUNDING_RESERVEDSECONDS = "proseco.grounding.reservedseconds";

	/* deployment */
	public static final String DEPLOYMENT_EXEC = "proseco.deployment.executable";
	public static final String DEPLOYMENT_HOST = "proseco.deployment.host";
	public static final String DEPLOYMENT_PORT_MIN = "proseco.deployment.minport";
	public static final String DEPLOYMENT_PORT_MAX = "proseco.deployment.maxport";
	public static final String DEPLOYMENT_ENTRYPOINT = "proseco.deployment.entrypoint";
	public static final String DEPLOYMENT_RESERVEDSECONDS = "proseco.deployment.reservedseconds";

	/* benchmarking */
	public static final String BENCHMARK_SERVICE = "benchmarkService.bat";
	public static final String BENCHMARK_PATH = "pbc.benchmarks_path";
	public static final String INTERNAL_BENCHMARK_FOLDER = "benchmarks/";

	public static final String EXEC_FINAL_TEST = "src/test.bat";

	@Key(BENCHMARK_PATH)
	@DefaultValue("5")
	public String getBenchmarkPath();

	@Key(STRATEGIES)
	@DefaultValue("strategies")
	public String getNameOfStrategyFolder();

	@Key(DISABLED_STRATEGIES)
	@DefaultValue("")
	public String getDisabledStrategies();

	@Key(GROUNDING_FOLDER)
	@DefaultValue("")
	public String getNameOfGroundingFolder();

	@Key(GROUNDING_EXEC)
	@DefaultValue("grounding")
	public String getGroundingCommand();

	@Key(GROUNDING_RESERVEDSECONDS)
	@DefaultValue("5")
	public int getSecondsReservedForGrounding();

	@Key(DEPLOYMENT_EXEC)
	@DefaultValue("deployment")
	public String getDeploymentCommand();

	@Key(DEPLOYMENT_HOST)
	@DefaultValue("localhost")
	public String getDeploymentHost();

	@Key(DEPLOYMENT_PORT_MIN)
	@DefaultValue("8100")
	public int getDeploymentMinPort();

	@Key(DEPLOYMENT_PORT_MAX)
	@DefaultValue("8200")
	public int getDeploymentMaxPort();

	@Key(DEPLOYMENT_ENTRYPOINT)
	@DefaultValue("")
	public String getDeploymentEntryPoint();

	@Key(DEPLOYMENT_RESERVEDSECONDS)
	@DefaultValue("5")
	public int getSecondsReservedForDeployment();

	@Key(STRATEGY_RUNNABLE)
	@DefaultValue("run")
	public String getSearchRunnable();

	public static PrototypeConfig get(final PROSECOConfig prosecoConfig, final String prototypeName) {
		return get(new File(prosecoConfig.getDirectoryForDomains() + File.separator + prototypeName + File.separator + "prototype.conf"));
	}

	public static PrototypeConfig get(final String file) {
		return get(new File(file));
	}

	public static PrototypeConfig get(final File file) {
		Properties props = new Properties();
		final Logger logger = LoggerFactory.getLogger(PrototypeConfig.class);
		try {
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("Could not find config file {}. Assuming default configuration", file);
		} catch (IOException e) {
			logger.error("Encountered problem with config file {}. Assuming default configuration. Problem: {}", file, e.getMessage());
		}

		return ConfigFactory.create(PrototypeConfig.class, props);
	}

	@Key(PRE_GROUNDING_HOOK)
	@DefaultValue("analysis")
	public File getHookForPreGrounding();
}
