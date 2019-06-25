package de.upb.crc901.proseco.core.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.upb.crc901.proseco.commons.controller.ProcessController;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.interview.State;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;
import de.upb.crc901.proseco.core.composition.FileBasedConfigurationProcess;

public class PartialInterviewTest {

	static PROSECOProcessEnvironment env;
	static File interviewFile;

	static String dummyAnswer = "dummy answer";

	@BeforeClass
	public static void initialize() throws ProcessIdAlreadyExistsException, InvalidStateTransitionException {
		ProcessController processController = new FileBasedConfigurationProcess(new File(""));
		processController.createNew(null);
		processController.fixDomain("test");
		env = processController.getProcessEnvironment();
		interviewFile = new File(env.getInterviewDirectory().getAbsolutePath() + File.separator + "interview.yaml");
	}

	@Test
	public void testEmptyInterview() throws IOException {
		Parser parser = new Parser();
		InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		State state = fillout.getCurrentState();
		assertEquals("step0", state.getName());
	}

	@Test
	public void testPrototypeSelected() throws IOException {
		Parser parser = new Parser();
		InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		Map<String, String> answers = new HashMap<>(fillout.getAnswers());

		Question prototypeQuestion = fillout.getCurrentState().getQuestions().get(0);
		answers.put(prototypeQuestion.getId(), "test");

		fillout = new InterviewFillout(fillout.getInterview(), answers);
		State state = fillout.getCurrentState();
		assertEquals("step1", state.getName());
	}

	@Test
	public void testAllQuestionsAnsweredUntilStep2() throws IOException {
		Parser parser = new Parser();
		InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		Map<String, String> answers = new HashMap<>(fillout.getAnswers());

		Question prototypeQuestion = fillout.getCurrentState().getQuestions().get(0);
		Question step1 = fillout.getInterview().getStates().get(1).getQuestions().get(0);
		Question step2 = fillout.getInterview().getStates().get(2).getQuestions().get(0);

		answers.put(prototypeQuestion.getId(), "test");
		answers.put(step1.getId(), dummyAnswer);
		answers.put(step2.getId(), dummyAnswer);

		fillout = new InterviewFillout(fillout.getInterview(), answers);
		State state = fillout.getCurrentState();
		assertEquals("step3", state.getName());
	}

	@Test
	public void testOnlyStep2QuestionIsAnswered() throws IOException {
		Parser parser = new Parser();
		InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		Map<String, String> answers = new HashMap<>(fillout.getAnswers());

		Question step2 = fillout.getInterview().getStates().get(2).getQuestions().get(0);

		answers.put(step2.getId(), dummyAnswer);

		fillout = new InterviewFillout(fillout.getInterview(), answers);
		State state = fillout.getCurrentState();
		// should start asking from step0
		assertEquals("step0", state.getName());

		Question prototypeQuestion = fillout.getCurrentState().getQuestions().get(0);
		Question step1 = fillout.getInterview().getStates().get(1).getQuestions().get(0);

		answers.put(prototypeQuestion.getId(), "test");
		answers.put(step1.getId(), dummyAnswer);

		fillout = new InterviewFillout(fillout.getInterview(), answers);
		state = fillout.getCurrentState();
		// should skip to step3 without asking step2
		assertEquals("step3", state.getName());

	}

	@Test
	public void testAllQuestionsAnswered() throws IOException {
		Parser parser = new Parser();
		InterviewFillout fillout = new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile));
		Map<String, String> answers = new HashMap<>(fillout.getAnswers());

		Question prototypeQuestion = fillout.getCurrentState().getQuestions().get(0);
		Question step1 = fillout.getInterview().getStates().get(1).getQuestions().get(0);
		Question step2 = fillout.getInterview().getStates().get(2).getQuestions().get(0);
		Question step3 = fillout.getInterview().getStates().get(3).getQuestions().get(0);
		Question step4 = fillout.getInterview().getStates().get(4).getQuestions().get(0);

		answers.put(prototypeQuestion.getId(), "test");
		answers.put(step1.getId(), dummyAnswer);
		answers.put(step2.getId(), dummyAnswer);
		answers.put(step3.getId(), dummyAnswer);
		answers.put(step4.getId(), dummyAnswer);

		fillout = new InterviewFillout(fillout.getInterview(), answers);
		State state = fillout.getCurrentState();
		assertEquals("step5", state.getName());

	}

}
