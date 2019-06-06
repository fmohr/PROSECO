package de.upb.crc901.proseco.commons.html.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.upb.crc901.proseco.commons.html.Option;

public class OptionTest {

	@Test
	public void equalsTest() {
		final Option option1 = new Option();
		final Option option2 = new Option();
		assertTrue(option1.equals(option2));
	}

	@Test
	public void toHTMLTest() {
		final Map<String, String> attributes = new HashMap<>();
		attributes.put("value", "option1");
		final String content = "Value1";
		final Option option = new Option(content, attributes);

		final String actual = option.toHTML();
		final String expected = "<option value=\"option1\">Value1</option>";

		assertEquals(expected, actual);

	}

}
