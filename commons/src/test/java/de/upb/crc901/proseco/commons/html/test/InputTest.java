package de.upb.crc901.proseco.commons.html.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.upb.crc901.proseco.commons.html.Input;

public class InputTest {

	@Test
	public void equalsTest() {
		final Input input1 = new Input();
		final Input input2 = new Input();
		assertTrue(input1.equals(input2));
	}

	@Test
	public void toHTMLTest() {
		final Map<String, String> attributes = new HashMap<>();
		attributes.put("name", "inputName");
		attributes.put("type", "text");
		final Input input = new Input(null, attributes);

		final String actual = input.toHTML();
		final String expected = "<input name=\"response\" type=\"text\"></input>";

		assertEquals(expected, actual);
	}

}
