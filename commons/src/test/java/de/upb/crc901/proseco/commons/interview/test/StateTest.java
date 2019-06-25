package de.upb.crc901.proseco.commons.interview.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.interview.State;

public class StateTest {
	@Test
	public void equalsTest() {
		final State s1 = new State();
		assertNotEquals(s1, null);
		assertNotEquals(s1, new Object());
		final State s2 = new State();
		assertEquals(s1, s2);
		s2.setName("s2");
		assertNotEquals(s1, s2);
		s1.setName("s1");
		assertNotEquals(s1, s2);
		s2.setName("s1");
		s1.setQuestions(new ArrayList<>());
		assertNotEquals(s1, s2);
		s1.setQuestions(null);
		s2.setQuestions(new ArrayList<>());
		assertNotEquals(s1, s2);
		final List<Question> questions = new ArrayList<>();
		s1.setQuestions(questions);
		s1.setQuestions(questions);
		assertEquals(s1, s2);
		s2.setTransition(new HashMap<>());
		assertNotEquals(s1, s2);
		s1.setTransition(new HashMap<>());
		assertEquals(s1, s2);
		s2.setTransition(null);
		assertNotEquals(s1, s2);
		assertEquals("State [name=s1, transition={}, questions=[]]", s1.toString());
	}

}
