package de.upb.crc901.proseco.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import de.upb.crc901.proseco.commons.controller.CannotFixDomainInThisProcessException;
import de.upb.crc901.proseco.commons.controller.DomainCouldNotBeDetectedException;
import de.upb.crc901.proseco.commons.controller.GroundingNotSuccessfulForAnyStrategyException;
import de.upb.crc901.proseco.commons.controller.NoStrategyFoundASolutionException;
import de.upb.crc901.proseco.commons.controller.PROSECOSolution;
import de.upb.crc901.proseco.commons.controller.ProcessIdAlreadyExistsException;
import de.upb.crc901.proseco.commons.controller.PrototypeCouldNotBeExtractedException;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.FileUtil;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.core.composition.NaturalLanguageSupportingConfigurationProcess;

public class NaturalLanguageSupportingConfigurationTest {
	static String processId;
	static PROSECOProcessEnvironment env;
	static String output;

	@BeforeClass
	public static void initialize() throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, DomainCouldNotBeDetectedException, NoStrategyFoundASolutionException, PrototypeCouldNotBeExtractedException,
			GroundingNotSuccessfulForAnyStrategyException {
		final NaturalLanguageSupportingConfigurationProcess processController = new NaturalLanguageSupportingConfigurationProcess(new File(""));
		processController.createNew(null);
		processController.receiveGeneralTaskDescription("test");
		env = processController.getProcessEnvironment();
		processId = env.getProcessId();

		final PROSECOSolution solution = processController.startComposition(1000);
		processController.chooseAndDeploySolution(solution);

		output = FileUtil.readFile("processes/" + processId + "/test.out");
	}

	@Test
	public void testWinningStrategy() {
		final int startGrounding = output.indexOf("Grounding");
		final int startWinningStrategyDir = output.indexOf("param2:", startGrounding) + "param2:".length();
		final int lineEnd = output.indexOf('\n', startWinningStrategyDir);
		final String winningStrategyDir = output.substring(startWinningStrategyDir, lineEnd).trim();
		assertTrue(winningStrategyDir.endsWith("strategy3"));
	}

	@Test
	public void testProcessStatusDone() {
		final String status = FileUtil.readFile("processes/" + processId + "/process.status");
		assertEquals("done", status);
	}

	@Test(expected = CannotFixDomainInThisProcessException.class)
	public void testFixDomain() throws ProcessIdAlreadyExistsException, InvalidStateTransitionException {
		final NaturalLanguageSupportingConfigurationProcess processController = new NaturalLanguageSupportingConfigurationProcess(new File(""));
		processController.createNew(null);
		processController.fixDomain("test");
	}

	@Test(expected = DomainCouldNotBeDetectedException.class)
	public void testDomainCouldNotBeDetected() throws ProcessIdAlreadyExistsException, InvalidStateTransitionException, DomainCouldNotBeDetectedException {
		final NaturalLanguageSupportingConfigurationProcess processController = new NaturalLanguageSupportingConfigurationProcess(new File(""));
		processController.createNew(null);
		processController.receiveGeneralTaskDescription("");
	}
}
