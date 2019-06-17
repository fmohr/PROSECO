package de.upb.crc901.proseco.commons.interview.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.upb.crc901.proseco.commons.interview.Interview;
import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.interview.State;

public class InterviewTest {
	@Test
	public void questionTest() {
		final Interview interview = new Interview();
		Question result = interview.getQuestionByPath("");
		assertEquals(null, result);

		final List<State> states = new ArrayList<>();
		final State state = new State();
		state.setName("s1");
		final List<Question> questions = new ArrayList<>();
		final Question question = new Question();
		question.setId("q1");
		questions.add(question);
		state.setQuestions(questions);
		states.add(state);
		interview.setStates(null);
		assertEquals(null, interview.getStates());
		interview.setStates(states);
		assertEquals(states, interview.getStates());
		result = interview.getQuestionByPath("s1.q1");
		assertEquals(question, result);

		assertEquals(state, interview.getStateMap().get("s1"));

		interview.setQuestionRepo("repo");
		interview.getQuestionRepo();
	}
}
