package de.upb.crc901.proseco.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

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

public class MultipleStrategies4Fail {
	static String processId;
	static PROSECOProcessEnvironment env;
	static String output;

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
		answers.put("Please select prototype", "test3");
		processController.updateInterview(answers);

		PROSECOSolution solution = processController.startComposition(20);
		processController.chooseAndDeploySolution(solution);

		output = FileUtil.readFile("processes/" + processId + "/test.out");
	}

	@Test
	public void testWinningAndBackupStrategy() {
		int startGrounding = output.indexOf("Grounding");
		String param2 = "param2:";
		int startWinningStrategyDir = output.indexOf(param2, startGrounding) + param2.length();
		int lineEnd = output.indexOf('\n', startWinningStrategyDir);
		String winningStrategyDir = output.substring(startWinningStrategyDir, lineEnd).trim();

		startGrounding = output.indexOf("Grounding", lineEnd);
		startWinningStrategyDir = output.indexOf(param2, startGrounding) + param2.length();
		lineEnd = output.indexOf('\n', startWinningStrategyDir);
		String backupStrategyDir = output.substring(startWinningStrategyDir, lineEnd).trim();

		assertTrue(winningStrategyDir.endsWith("strategy2")); // first attempt of grounding with strategy2
		assertTrue(backupStrategyDir.endsWith("strategy5")); // grounding with backup strategy5

	}

	@Test
	public void testProcessStatusDone() {
		String status = FileUtil.readFile("processes/" + processId + "/process.status");
		assertEquals("done", status);
	}
}
