package de.upb.crc901.proseco.commons.interview.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.upb.crc901.proseco.commons.interview.Interview;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.interview.State;

public class InterviewFilloutTest {

	@Test
	public void interviewAnswerUpdateTest() {
		final Interview interview = new Interview();
		final List<State> states = new ArrayList<>();
		final State state1 = new State();
		state1.setName("state1");
		final List<Question> questions = new ArrayList<>();
		final Question q1 = new Question();
		q1.setContent("content");
		q1.setId("q1");
		questions.add(q1);
		final Question q2 = new Question();
		q2.setContent("content2");
		q2.setId("q2");
		questions.add(q2);
		state1.setQuestions(questions);
		states.add(state1);
		interview.setStates(states);
		InterviewFillout fillout = new InterviewFillout(interview);
		final Interview resultingInterview = fillout.getInterview();
		assertEquals(interview, resultingInterview);

		Map<String, String> answers = new HashMap<>();
		fillout = new InterviewFillout(interview, answers);
		Map<String, String> resultingAnswers = fillout.retrieveQuestionAnswerMap();
		assertTrue(resultingAnswers.get("content") == null);
		assertFalse(fillout.allQuestionsInCurrentStateAnswered());

		answers = new HashMap<>();
		answers.put("q1", "answer");
		fillout = new InterviewFillout(interview, answers);
		resultingAnswers = fillout.retrieveQuestionAnswerMap();
		assertTrue(resultingAnswers.get("content") == "answer");

		final Map<String, String> questionAnswerMap = new HashMap<>();
		questionAnswerMap.put("content", "another answer");
		fillout.updateAnswers(questionAnswerMap);
		final String updatedAnswer = fillout.getAnswers().get("q1");
		assertEquals(updatedAnswer, fillout.getAnswer("q1"));
		assertEquals(updatedAnswer, fillout.getAnswer(q1));

		final State currentState = fillout.getCurrentState();
		assertTrue(currentState.getName().equals("state1"));

		final String html = fillout.getHTMLOfAllOpenQuestions().trim();

		assertEquals(html, fillout.getHTMLOfOpenQuestionsInCurrentState());

		fillout = new InterviewFillout(interview, answers, state1);
		questionAnswerMap.put("content2", "content2 answer");
		fillout.updateAnswers(questionAnswerMap);
		assertTrue(fillout.allQuestionsInCurrentStateAnswered());
	}

	@Test(expected = IllegalStateException.class)
	public void setAnswersToNullTest() {
		final InterviewFillout fillout2 = new InterviewFillout();
		fillout2.setAnswers(null);
	}

	@Test(expected = IllegalStateException.class)
	public void setAnswersMultipleTimesTest() {
		final InterviewFillout fillout2 = new InterviewFillout();
		final Map<String, String> answers = new HashMap<>();
		fillout2.setAnswers(answers);
		fillout2.setAnswers(answers);
	}

	@Test(expected = IllegalStateException.class)
	public void setInterviewToNullTest() {
		final InterviewFillout fillout2 = new InterviewFillout();
		fillout2.setInterview(null);
	}

	@Test(expected = IllegalStateException.class)
	public void setInterviewMultipleTimesTest() {
		final InterviewFillout fillout2 = new InterviewFillout();
		fillout2.setInterview(new Interview());
		fillout2.setInterview(new Interview());
	}

	@Test(expected = IllegalStateException.class)
	public void setCurrentStateToNullTest() {
		final InterviewFillout fillout2 = new InterviewFillout();
		fillout2.setCurrentState(null);
	}

	@Test(expected = IllegalStateException.class)
	public void setCurrentStateMultipleTimesTest() {
		final InterviewFillout fillout2 = new InterviewFillout();
		fillout2.setCurrentState(new State());
		fillout2.setCurrentState(new State());
	}

	@Test
	public void equalsTest() {
		final Interview interview = new Interview();
		final List<State> states = new ArrayList<>();
		final State state1 = new State();
		state1.setName("state");
		final List<Question> questions = new ArrayList<>();
		final Question q1 = new Question();
		q1.setContent("content");
		q1.setId("q");
		questions.add(q1);
		state1.setQuestions(questions);
		states.add(state1);
		interview.setStates(states);
		InterviewFillout fillout = new InterviewFillout(interview);
		InterviewFillout fillout2 = fillout;
		// same obj
		assertTrue(fillout.equals(fillout2));
		assertEquals(fillout.hashCode(), fillout2.hashCode());
		// other obj null
		assertFalse(fillout.equals(null));
		// different class
		assertFalse(fillout.equals(new Object()));
		// answers null
		assertFalse(new InterviewFillout().equals(fillout));
		// other obj answers null
		final Map<String, String> answers = new HashMap<>();
		fillout = new InterviewFillout(interview, answers);
		assertFalse(fillout.equals(new InterviewFillout()));
		// same answers
		Map<String, String> answers2 = new HashMap<>();
		fillout = new InterviewFillout(interview, answers);
		fillout2 = new InterviewFillout(interview, answers2);
		assertTrue(fillout.equals(fillout2));
		assertEquals(fillout.hashCode(), fillout2.hashCode());

		// different answers
		answers2 = new HashMap<>();
		answers2.put("q1", "a1");
		fillout = new InterviewFillout(interview, answers);
		fillout2 = new InterviewFillout(interview, answers2);
		assertFalse(fillout.equals(fillout2));
		assertNotEquals(fillout.hashCode(), fillout2.hashCode());
	}

	@Test
	public void hashCodeTest() {
		final InterviewFillout fillout = new InterviewFillout();
		final InterviewFillout fillout2 = fillout;
		assertEquals(fillout.hashCode(), fillout2.hashCode());
	}

}
