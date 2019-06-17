package de.upb.crc901.proseco.commons.interview.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.interview.QuestionCollection;

public class QuestionCollectionTest {
	@Test
	public void collectionTest() {
		final QuestionCollection collection = new QuestionCollection();
		collection.setQuestions(null);
		assertEquals(null, collection.getQuestions());

		final List<Question> questions = new ArrayList<>();
		final Question question = new Question();
		question.setId("q1");
		questions.add(question);
		collection.setQuestions(questions);
		assertEquals(question, collection.getQuestionById("q1"));
	}
}
