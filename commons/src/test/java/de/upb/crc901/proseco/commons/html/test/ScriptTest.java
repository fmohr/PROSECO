package de.upb.crc901.proseco.commons.html.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.upb.crc901.proseco.commons.html.Script;

public class ScriptTest {

	@Test
	public void equalsTest() {
		final Script script1 = new Script();
		final Script script2 = new Script();
		assertTrue(script1.equals(script2));
	}

	@Test
	public void toHTMLTest() {
		final Script script = new Script("content");
		final String actual = script.toHTML();
		final String expected = "<script>\ncontent\n</script>";
		assertEquals(expected, actual);
	}
}
