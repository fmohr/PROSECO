package de.upb.crc901.proseco.core.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.proseco.commons.controller.GroundingNotSuccessfulForAnyStrategyException;
import de.upb.crc901.proseco.commons.controller.NoStrategyFoundASolutionException;
import de.upb.crc901.proseco.commons.controller.PROSECOSolution;
import de.upb.crc901.proseco.commons.controller.ProcessController;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.FileUtil;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;
import de.upb.crc901.proseco.core.composition.FileBasedConfigurationProcess;

public class MultipleStrategiesFailAtGrounding {
	static String processId;
	static PROSECOProcessEnvironment env;
	static String output;
	static final Logger logger = LoggerFactory.getLogger(MultipleStrategiesFailAtGrounding.class);

	@BeforeClass
	public static void initialize()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, IOException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException {
		ProcessController processController = new FileBasedConfigurationProcess(new File(""));
		processController.createNew(null);
		processController.fixDomain("test");
		env = processController.getProcessEnvironment();
		processId = env.getProcessId();
		File interviewFile = new File(env.getInterviewDirectory().getAbsolutePath() + File.separator + "interview.yaml");
		Parser parser = new Parser();
		InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		Map<String, String> answers = fillout.retrieveQuestionAnswerMap();
		answers.put("Please select prototype", "test4");
		processController.updateInterview(answers);

		try {
			PROSECOSolution solution = processController.startComposition(1000);
			processController.chooseAndDeploySolution(solution);
		} catch (GroundingNotSuccessfulForAnyStrategyException e) {
			logger.error(e.getMessage());
		}

		output = FileUtil.readFile("processes/" + processId + "/test.out");
	}

	/**
	 * All strategies fail recursively at grounding step
	 */
	@Test
	public void testWinningAndBackupStrategy() {
		String grounding = "Grounding";
		String param2 = "param2:";
		int startGrounding = output.indexOf(grounding);
		int startWinningStrategyDir = output.indexOf(param2, startGrounding) + param2.length();
		int lineEnd = output.indexOf('\n', startWinningStrategyDir);
		String winningStrategy = output.substring(startWinningStrategyDir, lineEnd).trim();

		startGrounding = output.indexOf(grounding, lineEnd);
		startWinningStrategyDir = output.indexOf(param2, startGrounding) + param2.length();
		lineEnd = output.indexOf('\n', startWinningStrategyDir);
		String backupStrategy1 = output.substring(startWinningStrategyDir, lineEnd).trim();

		startGrounding = output.indexOf(grounding, lineEnd);
		startWinningStrategyDir = output.indexOf(param2, startGrounding) + param2.length();
		lineEnd = output.indexOf('\n', startWinningStrategyDir);
		String backupStrategy2 = output.substring(startWinningStrategyDir, lineEnd).trim();

		assertTrue(winningStrategy.endsWith("strategy2")); // first attempt of grounding with strategy2
		assertTrue(backupStrategy1.endsWith("strategy1")); // grounding with backup strategy1
		assertTrue(backupStrategy2.endsWith("strategy3")); // grounding with backup strategy3

	}

	/**
	 * Process status should not be done
	 */
	@Test
	public void testProcessStatusNotDone() {
		String status = FileUtil.readFile("processes/" + processId + "/process.status");
		assertNotEquals("done", status);
	}
}
