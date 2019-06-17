package de.upb.crc901.proseco.commons.util.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.upb.crc901.proseco.commons.util.FileUtil;

public class FileUtilTest {
	@Test
	public void readWriteFileTest() {
		FileUtil.writeToFile("./fileUtilWriteTest.txt", "content");
		final String result = FileUtil.readFile("./fileUtilWriteTest.txt");
		assertEquals("content", result);

		// exception cases
		FileUtil.writeToFile("", "");
		FileUtil.readFile("nonExistingFile");
		FileUtil.readFile("");
	}

	@Test
	public void readFileByLineNumberTest() {
		FileUtil.writeToFile("./fileUtilWriteTest.txt", "line1\nline2");
		String result = FileUtil.readFileByLineNumber("./fileUtilWriteTest.txt", 1).trim();
		assertEquals("line2", result);

		result = FileUtil.readFileByLineNumber("./fileUtilWriteTest.txt", 2).trim();
		assertEquals("line1\nline2", result);

		result = FileUtil.readFileByLineNumber("", 1);
		assertEquals(null, result);

	}
}
