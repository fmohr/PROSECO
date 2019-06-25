package de.upb.crc901.proseco.view.app.controller.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.upb.crc901.proseco.commons.controller.ProcessIdDoesNotExistException;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.view.app.controller.APIController;
import de.upb.crc901.proseco.view.app.model.LogResponseBody;

public class APIControllerTest {

	private static final String STRATEGY = "strategy1";

	@Test
	public void getStrategyLogsTest() {
		final APIController controller = new APIController();
		final ResponseEntity<Object> response = controller.getStrategyLogs("test-default");
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		final LogResponseBody logResponse = (LogResponseBody) response.getBody();
		assertEquals(STRATEGY, logResponse.getLogList().get(0).getStrategyName());
	}

	@Test
	public void getLogTest() {
		final APIController controller = new APIController();
		final ResponseEntity<Object> response = controller.getLog("test-default");
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		final List logResponse = (List) response.getBody();
		assertTrue(logResponse.get(0).toString().contains(STRATEGY));
	}

	@Test
	public void stopServiceSuccessTest() throws ProcessIdDoesNotExistException, InvalidStateTransitionException {
		final APIController controller = new APIController();
		final String response = controller.stopService("test-default");
	}

}
