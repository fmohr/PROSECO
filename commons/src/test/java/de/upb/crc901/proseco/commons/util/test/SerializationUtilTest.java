package de.upb.crc901.proseco.commons.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;

import org.junit.Test;

import de.upb.crc901.proseco.commons.interview.Interview;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.util.SerializationUtil;

public class SerializationUtilTest {
	@Test
	public void readWriteTest() {
		SerializationUtil.write("./serializationUtilTest.txt", new Interview());
		final Interview interview = SerializationUtil.read("./serializationUtilTest.txt");
		assertNotEquals(null, interview);
	}

	@Test
	public void readWriteAsJSONTest() {
		SerializationUtil.writeAsJSON(new File("./serializationUtilTest.json"), new InterviewFillout());
		final InterviewFillout interview = SerializationUtil.readAsJSON(new File("./serializationUtilTest.json"));
		assertEquals(null, interview);
	}
}
