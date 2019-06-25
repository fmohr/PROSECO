package de.upb.crc901.proseco.core.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.proseco.commons.controller.GroundingNotSuccessfulForAnyStrategyException;
import de.upb.crc901.proseco.commons.controller.NoStrategyFoundASolutionException;
import de.upb.crc901.proseco.commons.controller.PROSECOSolution;
import de.upb.crc901.proseco.commons.controller.ProcessController;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.ProcessIdDoesNotExistException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;
import de.upb.crc901.proseco.core.composition.FileBasedConfigurationProcess;

/*-
 *
 * Possible Transitions:
 * rows: possible state a process can be in
 * cols: possible next states a process can move to
 * + : correct transition
 * - : incorrect transition but programmatically possible (should raise exception)
 * x : programmatically impossible transition
 *
 *
 * 			   INIT	 CREATED DOMAIN	INTERVIEW	COMPOSITION	PROTOTYPE CHOSEN	GROUNDING	DEPLOYMENT	DONE
 *	INIT		x		+		-		-			-			x		x			-			x		x
 *	CREATED		x		-		+		-			-			x		x			-			x		x
 *	DOMAIN		x		-		-		+			-			x		x			-			x		x
 *	INTERVIEW	x		-		-		+			+			x		x			-			x		x
 *	CHOSEN		x		-		-		-			-			x		x			+			x		x
 *	DONE		x		-		-		-			-			x		x			-			x		x
 *
 *
 * states inaccessible from outside: COMPOSITION, PROTOTYPE, GROUNDING, DEPLOYMENT
 *
 * in total 30 different cases are available
 *
 * @author kadirayk
 *
 */

public class StateTransitionTest {
	static String processId;
	static PROSECOProcessEnvironment env;
	static String output;
	static final Logger logger = LoggerFactory.getLogger(StateTransitionTest.class);
	private static final String PROTOTYPE_QUESTION = "Please select prototype";
	private static final String STATE_INACCESSIBLE_MSG = "This State is inaccessible from outside";
	private static final String INTERVIEW_YAML = "interview.yaml";
	private static final String TEST1 = "test1";

	/*
	 * Tests for state = INIT
	 */

	/**
	 * INIT -> CREATED [CORRECT]
	 *
	 * @throws GroundingNotSuccessfulForAnyStrategyException
	 * @throws PrototypeCouldNotBeExtractedException
	 * @throws NoStrategyFoundASolutionException
	 * @throws InvalidStateTransitionException
	 * @throws ProcessIdAlreadyExistsException
	 * @throws IOException
	 */
	@Test
	public void testInitToCreated()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INIT);
		if (process != null) {
			process.createNew(null);
			assertEquals(EProcessState.CREATED, process.getProcessState());
		}
	}

	/**
	 * INIT -> DOMAIN_DEFINITON [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testInitToDomain()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INIT);
		if (process != null) {
			process.fixDomain("test");
		}
	}

	/**
	 * INIT -> INTERVIEW [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testInitToInterview()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INIT);
		if (process != null) {
			process.updateInterview(new HashMap<>());
		}
	}

	/**
	 * INIT -> COMPOSITION [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testInitToComposition()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INIT);
		if (process != null) {
			process.startComposition(1000);
		}
	}

	/**
	 * INIT -> GROUNDING [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testInitToGrounding()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INIT);
		if (process != null) {
			process.chooseAndDeploySolution(null);
		}
	}

	/*
	 * Tests for state = CREATED
	 */

	/**
	 * CREATED -> CREATED [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testCreatedToCreated()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.CREATED);
		if (process != null) {
			process.createNew(null);
		}
	}

	/**
	 * CREATED -> DOMAIN_DEFINITON [CORRECT]
	 *
	 * @throws IOException
	 */
	@Test
	public void testCreatedToDomain()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.CREATED);
		if (process != null) {
			process.fixDomain("test");
			assertEquals(EProcessState.DOMAIN_DEFINITION, process.getProcessState());
		}
	}

	/**
	 * CREATED -> INTERVIEW [CORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testCreatedToInterview()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.CREATED);
		if (process != null) {
			process.updateInterview(new HashMap<>());
		}
	}

	/**
	 * CREATED -> COMPOSITON [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testCreatedToComposition()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.CREATED);
		if (process != null) {
			process.startComposition(1000);
		}
	}

	/**
	 * CREATED -> GROUNDING [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testCreatedToGrounding()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.CREATED);
		if (process != null) {
			process.chooseAndDeploySolution(null);
		}
	}

	/*
	 * Tests for state = DOMAIN_DEFINITON
	 */

	/**
	 * DOMAIN_DEFINITON -> CREATED [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDomainToCreated()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DOMAIN_DEFINITION);
		if (process != null) {
			process.createNew(null);
		}
	}

	/**
	 * DOMAIN_DEFINITON -> DOMAIN_DEFINITON [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDomainToDomain()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DOMAIN_DEFINITION);
		if (process != null) {
			process.fixDomain("");
		}
	}

	/**
	 * DOMAIN_DEFINITON -> INTERVIEW [CORRECT]
	 *
	 * @throws IOException
	 */
	@Test
	public void testDomainToInterview()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DOMAIN_DEFINITION);
		if (process != null) {
			process.updateInterview(new HashMap<>());
		}
	}

	/**
	 * DOMAIN_DEFINITON -> COMPOSITION [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDomainToComposition()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DOMAIN_DEFINITION);
		if (process != null) {
			process.startComposition(1000);
		}
	}

	/**
	 * DOMAIN_DEFINITON -> GROUNDING [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDomainToGrounding()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DOMAIN_DEFINITION);
		if (process != null) {
			process.chooseAndDeploySolution(null);
		}
	}

	/*
	 * Tests for state = INTERVIEW
	 */

	/**
	 * INTERVIEW -> CREATED [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testInterviewToCreated()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INTERVIEW);
		if (process != null) {
			process.createNew(null);
		}
	}

	/**
	 * INTERVIEW -> DOMAIN_DEFINITION [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testInterviewToDomain()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INTERVIEW);
		if (process != null) {
			process.fixDomain("test");
		}
	}

	/**
	 * INTERVIEW -> INTERVIEW [CORRECT]
	 *
	 * @throws IOException
	 */
	@Test
	public void testInterviewToInterview()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INTERVIEW);
		if (process != null) {
			process.updateInterview(new HashMap<>());
			assertEquals(EProcessState.INTERVIEW, process.getProcessState());
		}
	}

	/**
	 * INTERVIEW -> COMPOSITION [CORRECT]
	 *
	 * @throws IOException
	 */
	@Test
	public void testInterviewToComposition()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INTERVIEW);
		if (process != null) {
			process.startComposition(1000);
			assertEquals(EProcessState.STRATEGY_CHOSEN, process.getProcessState());
		}
	}

	/**
	 * INTERVIEW -> GROUNDING [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testInterviewToGrounding()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.INTERVIEW);
		if (process != null) {
			process.chooseAndDeploySolution(null);
		}
	}

	/*
	 * Tests for state = STRATEGY_CHOSEN
	 */

	/**
	 * STRATEGY_CHOSEN -> CREATED [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testChosenToCreated()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.STRATEGY_CHOSEN);
		if (process != null) {
			process.createNew(null);
		}
	}

	/**
	 * STRATEGY_CHOSEN -> DOMAIN_DEFINITION [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testChosenToDomain()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.STRATEGY_CHOSEN);
		if (process != null) {
			process.fixDomain("test");
		}
	}

	/**
	 * STRATEGY_CHOSEN -> INTERVIEW [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testChosenToInterview()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.STRATEGY_CHOSEN);
		if (process != null) {
			process.updateInterview(new HashMap<>());
		}
	}

	/**
	 * STRATEGY_CHOSEN -> COMPOSITION [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testChosenToComposition()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.STRATEGY_CHOSEN);
		if (process != null) {
			process.startComposition(1000);
		}
	}

	/**
	 * STRATEGY_CHOSEN -> GROUNDING [CORRECT]
	 *
	 * @throws IOException
	 */
	@Test
	public void testChosenToGrounding()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.STRATEGY_CHOSEN);
		if (process != null) {
			process.chooseAndDeploySolution(null);
			assertEquals(EProcessState.DONE, process.getProcessState());
		}
	}

	/*
	 * Tests for state = DONE
	 */

	/**
	 * DONE -> CREATED [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDoneToCreated()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DONE);
		if (process != null) {
			process.createNew(null);
		}
	}

	/**
	 * DONE -> DOMAIN_DEFINITION [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDoneToDomain()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DONE);
		if (process != null) {
			process.fixDomain("test");
		}
	}

	/**
	 * DONE -> INTERVIEW [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDoneToInterview()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DONE);
		if (process != null) {
			process.updateInterview(new HashMap<>());
		}
	}

	/**
	 * DONE -> COMPOSITION [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDoneToComposition()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DONE);
		if (process != null) {
			process.startComposition(1000);
		}
	}

	/**
	 * DONE -> GROUNDING [INCORRECT]
	 *
	 * @throws IOException
	 */
	@Test(expected = InvalidStateTransitionException.class)
	public void testDoneToGrounding()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController process = getProcessForState(EProcessState.DONE);
		if (process != null) {
			process.chooseAndDeploySolution(null);
		}
	}

	@Test
	public void testWholeTransition()
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		final ProcessController processController = new FileBasedConfigurationProcess(new File(""));
		assertEquals(EProcessState.INIT, processController.getProcessState());

		processController.createNew(null);
		assertEquals(EProcessState.CREATED, processController.getProcessState());

		processController.fixDomain("test");
		assertEquals(EProcessState.DOMAIN_DEFINITION, processController.getProcessState());

		final PROSECOProcessEnvironment environment = processController.getProcessEnvironment();
		final File interviewFile = new File(environment.getInterviewDirectory().getAbsolutePath() + File.separator + INTERVIEW_YAML);
		final Parser parser = new Parser();
		final InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		final Map<String, String> answers = fillout.retrieveQuestionAnswerMap();
		answers.put(PROTOTYPE_QUESTION, TEST1);
		processController.updateInterview(answers);
		assertEquals(EProcessState.INTERVIEW, processController.getProcessState());

		final PROSECOSolution solution = processController.startComposition(1000);
		assertEquals(EProcessState.STRATEGY_CHOSEN, processController.getProcessState());

		processController.chooseAndDeploySolution(solution);
		assertEquals(EProcessState.DONE, processController.getProcessState());
	}

	@Test
	public void testWholeTransitionWithAttached() throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException,
			GroundingNotSuccessfulForAnyStrategyException, ProcessIdDoesNotExistException, IOException {
		ProcessController processController = new FileBasedConfigurationProcess(new File(""));
		processController.createNew(null);
		processController.fixDomain("test");
		PROSECOProcessEnvironment environment = processController.getProcessEnvironment();
		final String mProcessId = environment.getProcessId();

		processController = new FileBasedConfigurationProcess(new File(""));
		processController.attach(mProcessId);
		environment = processController.getProcessEnvironment();

		final File interviewFile = new File(environment.getInterviewDirectory().getAbsolutePath() + File.separator + INTERVIEW_YAML);
		final Parser parser = new Parser();
		final InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		final Map<String, String> answers = fillout.retrieveQuestionAnswerMap();
		answers.put(PROTOTYPE_QUESTION, TEST1);
		processController.updateInterview(answers);
		assertEquals(EProcessState.INTERVIEW, processController.getProcessState());

		final PROSECOSolution solution = processController.startComposition(1000);
		assertEquals(EProcessState.STRATEGY_CHOSEN, processController.getProcessState());

		processController.chooseAndDeploySolution(solution);
		assertEquals(EProcessState.DONE, processController.getProcessState());
	}

	private static ProcessController getProcessForState(final EProcessState requestedState)
			throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException, GroundingNotSuccessfulForAnyStrategyException, IOException {
		ProcessController process = null;
		switch (requestedState) {
		case INIT:
			process = new FileBasedConfigurationProcess(new File(""));
			break;
		case CREATED:
			process = new FileBasedConfigurationProcess(new File(""));
			process.createNew(null);
			break;
		case DOMAIN_DEFINITION:
			process = new FileBasedConfigurationProcess(new File(""));
			process.createNew(null);
			process.fixDomain("test");
			break;
		case INTERVIEW:
			process = new FileBasedConfigurationProcess(new File(""));
			process.createNew(null);
			process.fixDomain("test");
			env = process.getProcessEnvironment();
			processId = env.getProcessId();
			File interviewFile = new File(env.getInterviewDirectory().getAbsolutePath() + File.separator + INTERVIEW_YAML);
			Parser parser = new Parser();
			InterviewFillout fillout = null;
			fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
			Map<String, String> answers = fillout.retrieveQuestionAnswerMap();
			answers.put(PROTOTYPE_QUESTION, TEST1);
			process.updateInterview(answers);
			break;
		case COMPOSITION:
			logger.error(STATE_INACCESSIBLE_MSG);
			break;
		case STRATEGY_CHOSEN:
			process = new FileBasedConfigurationProcess(new File(""));
			process.createNew(null);
			process.fixDomain("test");
			env = process.getProcessEnvironment();
			processId = env.getProcessId();
			interviewFile = new File(env.getInterviewDirectory().getAbsolutePath() + File.separator + INTERVIEW_YAML);
			parser = new Parser();
			fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
			answers = fillout.retrieveQuestionAnswerMap();
			answers.put(PROTOTYPE_QUESTION, TEST1);
			process.updateInterview(answers);
			process.startComposition(1000);
			break;
		case DEPLOYMENT:
			logger.error(STATE_INACCESSIBLE_MSG);
			break;
		case DONE:
			process = new FileBasedConfigurationProcess(new File(""));
			process.createNew(null);
			process.fixDomain("test");
			env = process.getProcessEnvironment();
			processId = env.getProcessId();
			interviewFile = new File(env.getInterviewDirectory().getAbsolutePath() + File.separator + INTERVIEW_YAML);
			parser = new Parser();
			fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
			answers = fillout.retrieveQuestionAnswerMap();
			answers.put(PROTOTYPE_QUESTION, TEST1);
			process.updateInterview(answers);
			final PROSECOSolution solution = process.startComposition(1000);
			process.chooseAndDeploySolution(solution);
			break;
		case GROUNDING:
			logger.error(STATE_INACCESSIBLE_MSG);
			break;
		case PROTOTYPE_EXTRACTED:
			logger.error(STATE_INACCESSIBLE_MSG);
			break;
		default:
			process = new FileBasedConfigurationProcess(new File(""));
			break;

		}
		return process;
	}
}
