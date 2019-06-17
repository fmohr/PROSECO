package de.upb.crc901.proseco.core.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.upb.crc901.proseco.commons.controller.NoStrategyFoundASolutionException;
import de.upb.crc901.proseco.commons.controller.ProcessController;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;
import de.upb.crc901.proseco.core.composition.FileBasedConfigurationProcess;

public class MultipleStrategiesAllFail {
	static String processId;
	static PROSECOProcessEnvironment env;
	static String output;
	static ProcessController processController;

	@BeforeClass
	public static void initialize() throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, IOException {
		processController = new FileBasedConfigurationProcess(new File(""));
		processController.createNew(null);
		processController.fixDomain("test");
		env = processController.getProcessEnvironment();
		processId = env.getProcessId();
		File interviewFile = new File(env.getInterviewDirectory().getAbsolutePath() + File.separator + "interview.yaml");
		Parser parser = new Parser();
		InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		Map<String, String> answers = fillout.retrieveQuestionAnswerMap();
		answers.put("Please select prototype", "test2");
		processController.updateInterview(answers);
	}

	/**
	 * No winning strategy, process should not move to grounding step
	 *
	 * @throws PrototypeCouldNotBeExtractedException
	 * @throws InvalidStateTransitionException
	 * @throws NoStrategyFoundASolutionException
	 */
	@Test(expected = NoStrategyFoundASolutionException.class)
	public void testGroundingNotExists() throws NoStrategyFoundASolutionException, InvalidStateTransitionException, PrototypeCouldNotBeExtractedException {
		processController.startComposition(1000);
	}

	/**
	 * Process status should not be done
	 */
	@Test
	public void testProcessStatusNotDone() {
		assertNotEquals("done", processController.getProcessState());
	}
}
