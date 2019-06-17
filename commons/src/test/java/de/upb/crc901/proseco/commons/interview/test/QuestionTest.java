package de.upb.crc901.proseco.commons.interview.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import de.upb.crc901.proseco.commons.html.Input;
import de.upb.crc901.proseco.commons.html.Select;
import de.upb.crc901.proseco.commons.html.UIElement;
import de.upb.crc901.proseco.commons.interview.Question;

public class QuestionTest {
	@Test
	public void equalsTest() {
		final Question q1 = new Question();
		assertNotEquals(q1, null);
		assertNotEquals(q1, new Object());
		final Question q2 = new Question();
		assertEquals(q1, q2);

		q2.setContent("content");
		assertNotEquals(q1, q2);
		q1.setContent("content1");
		assertNotEquals(q1, q2);
		q1.setContent("content");
		assertEquals(q1, q2);

		q2.setId("q2");
		assertNotEquals(q1, q2);

		q1.setId("q1");
		q2.setId("q2");
		assertNotEquals(q1, q2);

		q1.setId("q1");
		q2.setId("q1");
		assertEquals(q1, q2);

		final UIElement ui = new Input();
		q1.setUiElement(ui);
		q2.setUiElement(ui);
		assertEquals(q1, q2);

		q2.setUiElement(new Select());
		assertNotEquals(q1, q2);

		q1.setUiElement(null);
		assertNotEquals(q1, q2);

		assertEquals("Question [id=q1, content=content, uiElement=null]", q1.toString());
	}
}
