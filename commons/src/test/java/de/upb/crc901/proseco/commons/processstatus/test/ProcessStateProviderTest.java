package de.upb.crc901.proseco.commons.processstatus.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.Test;

import de.upb.crc901.proseco.commons.controller.DefaultProcessController;
import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.ProcessStateProvider;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

public class ProcessStateProviderTest {

	private static final String DEFAULT_PROCESSID = "test-default";

	@Test(expected = NoSuchElementException.class)
	public void processStatusTest() throws IOException {
		final DefaultProcessController controller = new DefaultProcessController(new File(""));
		PROSECOProcessEnvironment env = ProcessStateProvider.getProcessEnvironment(DEFAULT_PROCESSID, true);
		assertEquals(env.getProcessId(), DEFAULT_PROCESSID);
		env = ProcessStateProvider.getProcessEnvironment(DEFAULT_PROCESSID, false);
		assertEquals(env.getProcessId(), DEFAULT_PROCESSID);
		env = ProcessStateProvider.getProcessEnvironment(DEFAULT_PROCESSID, true);
		assertEquals(env.getProcessId(), DEFAULT_PROCESSID);
		ProcessStateProvider.setProcessStatus(DEFAULT_PROCESSID, EProcessState.DEPLOYMENT);
		final String status = ProcessStateProvider.getProcessStatus(DEFAULT_PROCESSID);
		assertEquals("deployment", status);

		assertEquals("domain", ProcessStateProvider.getProcessStatus("NaN"));

		final EProcessState state = ProcessStateProvider.readProcessStateValue("deployment");
		assertEquals(EProcessState.DEPLOYMENT, state);

		ProcessStateProvider.readProcessStateValue("DEPLOYMENT");
	}

}
