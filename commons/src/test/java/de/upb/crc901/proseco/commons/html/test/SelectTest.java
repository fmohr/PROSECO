package de.upb.crc901.proseco.commons.html.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.upb.crc901.proseco.commons.html.Option;
import de.upb.crc901.proseco.commons.html.Select;

public class SelectTest {

	@Test
	public void hashCodeTest() {
		Select s1 = new Select();
		Select s2 = new Select();
		assertEquals(s1.hashCode(), s2.hashCode());

		s1 = new Select(null, null, new ArrayList<>());
		s2 = new Select(null, null, new ArrayList<>());
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void equalsTest() {
		Select s1 = new Select();
		Select s2 = new Select();
		assertTrue(s1.equals(s2));

		s1 = new Select("content1", null, new ArrayList<>());
		s2 = new Select("content2", null, new ArrayList<>());
		assertFalse(s1.equals(s2));

		s1 = new Select("content1", new HashMap<>(), new ArrayList<>());
		s2 = null;
		assertFalse(s1.equals(s2));

		s1 = new Select("content1", new HashMap<>(), new ArrayList<>());
		s2 = new Select("content1", new HashMap<>(), new ArrayList<>());
		assertTrue(s1.equals(s2));

		s1 = new Select("content1", new HashMap<>(), new ArrayList<>());
		s2 = new Select("content1", new HashMap<>(), null);
		assertFalse(s1.equals(s2));

		s1 = new Select("content1", new HashMap<>(), null);
		s2 = new Select("content1", new HashMap<>(), new ArrayList<>());
		assertFalse(s1.equals(s2));

	}

	@Test
	public void toHTMLTest() {
		final Map<String, String> attributes = new HashMap<>();
		attributes.put("name", "selectName");

		final List<Option> options = new ArrayList<>();

		final Map<String, String> option1Attributes = new HashMap<>();
		option1Attributes.put("value", "option1");
		options.add(new Option("Value1", option1Attributes));

		final Map<String, String> option2Attributes = new HashMap<>();
		option2Attributes.put("value", "option2");
		options.add(new Option("Value2", option2Attributes));

		Select select = new Select(null, attributes, options);

		String actual = select.toHTML();
		String expected = "<select name=\"response\">" + "\n\t<option value=\"option1\">Value1</option>" + "\n\t<option value=\"option2\">Value2</option>" + "\n</select>";

		assertEquals(expected, actual);

		select = new Select(null, null, null);

		actual = select.toHTML();
		expected = "<select>\n</select>";

		assertEquals(expected, actual);

	}

}
