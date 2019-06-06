package de.upb.crc901.proseco.commons.processstatus.test;

import org.junit.Test;

import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.processstatus.ProcessStateTransitionController;

public class ProcessStateTransitionControllerTest {
	@Test
	public void validTransitionTest() throws InvalidStateTransitionException {
		ProcessStateTransitionController.moveToNextState(EProcessState.INIT, EProcessState.CREATED);
	}

	@Test(expected = InvalidStateTransitionException.class)
	public void invalidTransitionTest() throws InvalidStateTransitionException {
		ProcessStateTransitionController.moveToNextState(EProcessState.CREATED, EProcessState.INIT);
	}
}
