package de.upb.crc901.proseco.command;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.upb.crc901.proseco.ExecutionEnvironment;
import de.upb.crc901.proseco.util.Config;

public class ExecuteGroundingRoutineCommandTest {

	private ExecutionEnvironment executionEnvironment;

	@Before
	public void initEnvironment() throws Exception {
		File prototypeDir = new File(Config.PROTOTYPES.getAbsolutePath() + File.separator + "game");
		InitializeExecutionEnvironmentCommand initCommand = new InitializeExecutionEnvironmentCommand("game-E4D89A377B",
				prototypeDir);
		initCommand.execute();
		executionEnvironment = initCommand.getExecutionEnvironment();
	}
	
	@Test
	public void TestGrounding() throws Exception {
		ExecuteGroundingRoutineCommand command = new ExecuteGroundingRoutineCommand(executionEnvironment);
		command.execute();
	}

}