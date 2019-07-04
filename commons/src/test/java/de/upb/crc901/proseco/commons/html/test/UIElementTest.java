package de.upb.crc901.proseco.commons.html.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.upb.crc901.proseco.commons.html.Option;
import de.upb.crc901.proseco.commons.html.Select;
import de.upb.crc901.proseco.commons.html.UIElement;

public class UIElementTest {
	private static final String DUMMY_CONTENT = "content";

	@Test
	public void equalsTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// equals
		UIElement e1 = new Option(DUMMY_CONTENT, new HashMap<>());
		UIElement e2 = e1;
		assertTrue(e1.equals(e2));

		// null
		e1 = new Option(DUMMY_CONTENT, new HashMap<>());
		e2 = null;
		assertFalse(e1.equals(e2));

		// different class
		e1 = new Option(DUMMY_CONTENT, new HashMap<>());
		e2 = new Select(DUMMY_CONTENT, new HashMap<>(), new ArrayList<>());
		assertFalse(e1.equals(e2));

		// same class null attributes
		e1 = new Option(null, null);
		e2 = new Option(DUMMY_CONTENT, new HashMap<>());
		assertFalse(e1.equals(e2));

		// same class other obj null attributes
		e1 = new Option(null, null);
		e2 = new Option(DUMMY_CONTENT, null);
		assertFalse(e1.equals(e2));

		// same class different attributes
		e1 = new Option(DUMMY_CONTENT, new HashMap<>());
		final Map<String, String> attributes = new HashMap<>();
		attributes.put("value", "option1");
		e2 = new Option(DUMMY_CONTENT, attributes);
		assertFalse(e1.equals(e2));

		// same class same attributes different content
		final Map<String, String> attr = new HashMap<>();
		attr.put("value", "option1");
		e1 = new Option(DUMMY_CONTENT, attr);
		e2 = new Option("different content", attr);
		assertFalse(e1.equals(e2));

		// same class same attributes contents null
		e1 = new Option(null, attr);
		e2 = new Option(null, attr);
		assertTrue(e1.equals(e2));

		// same class same attributes different content different tag
		e1 = new Option(DUMMY_CONTENT, attr);
		e2 = new Option("different content", attr);
		e1.setTag("tag1");
		e2.setTag("tag2");
		assertFalse(e1.equals(e2));

		// same class same attributes same content different tag
		e1 = new Option(DUMMY_CONTENT, attr);
		e2 = new Option(DUMMY_CONTENT, attr);
		e1.setTag("tag1");
		e2.setTag("tag2");
		assertFalse(e1.equals(e2));

		// same class same attributes same content null tag
		e1 = new Option(DUMMY_CONTENT, attr);
		e2 = new Option(DUMMY_CONTENT, attr);
		e1.setTag(null);
		e2.setTag("tag2");
		assertFalse(e1.equals(e2));

		// same class same attributes same content null tags
		e1 = new Option(DUMMY_CONTENT, attr);
		e2 = new Option(DUMMY_CONTENT, attr);
		e1.setTag(null);
		e2.setTag(null);
		assertTrue(e1.equals(e2));

	}

	@Test
	public void hashCodeTest() {
		UIElement e1 = new Option(DUMMY_CONTENT, new HashMap<>());
		UIElement e2 = e1;
		assertEquals(e1.hashCode(), e2.hashCode());

		e1 = new Option(null, null);
		e1.setTag(null);
		e2 = e1;
		assertEquals(e1.hashCode(), e2.hashCode());
	}

	@Test
	public void toHTMLTest() {
		final Map<String, String> attributes = new HashMap<>();
		attributes.put("value", "option1");
		final String content = "Value1";
		final UIElement option = new Option(content, attributes);

		final String actual = option.toHTML();
		final String expected = "<option value=\"option1\">Value1</option>";

		assertEquals(expected, actual);

	}

}
