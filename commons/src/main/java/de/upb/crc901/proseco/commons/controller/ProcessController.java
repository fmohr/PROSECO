package de.upb.crc901.proseco.commons.controller;

import java.util.Map;

import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;

/**
 * ProcessController interfaces defines a set of methods which should be called in a certain order to complete a configuration process.
 * A PROSECO configuration process keeps an internal state to provide transparency on what is going on during the configuration process.
 *
 * <br>
 * <br>
 * <b>Method Call Order and State Transitions:</b><br>
 *
 * <table border="1">
 * <tr>
 * <td><b>Current State</b></td>
 * <td><b>Method</b></td>
 * <td><b>Next State</b></td>
 * </tr>
 * <tr>
 * <td>INIT</td>
 * <td>CreateNew()</td>
 * <td>CREATED</td>
 * </tr>
 * <tr>
 * <td>CREATED</td>
 * <td>fixDomain()</td>
 * <td>DOMAIN_DEFINITION</td>
 * </tr>
 * <tr>
 * <td>DOMAIN_DEFINITION</td>
 * <td>updateInterview()</td>
 * <td>INTERVIEW</td>
 * </tr>
 * <tr>
 * <td>INTERVIEW</td>
 * <td>updateInterview()</td>
 * <td>INTERVIEW</td>
 * </tr>
 * <tr>
 * <td>INTERVIEW</td>
 * <td>startComposition()</td>
 * <td>STRATEGY_CHOSEN</td>
 * </tr>
 * <td>STRATEGY_CHOSEN</td>
 * <td>chooseAndDeploySolution()</td>
 * <td>DONE</td>
 * </tr>
 * </table>
 *
 * @author kadirayk
 *
 */
public interface ProcessController {

	/**
	 * Create a new prototype with the given ID if available, Create a new prototype
	 * if the given ID is null
	 *
	 * @param processId consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @throws ProcessIdAlreadyExistsException thrown when a new process is wanted to be created with the given id but that id is already in use
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	public void createNew(String processId) throws ProcessIdAlreadyExistsException, InvalidStateTransitionException;

	/**
	 * Create a new prototype with a random process Id
	 *
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	public void createNew() throws InvalidStateTransitionException;

	/**
	 * Attach to an existing process, with given processId
	 *
	 * @param processId consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @throws ProcessIdDoesNotExistException thrown when tried to attach to a processId that does not exist
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	public void attach(String processId) throws ProcessIdDoesNotExistException, InvalidStateTransitionException;

	/**
	 * Set domain of the process
	 *
	 * @param domain domain name
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	public void fixDomain(String domain) throws InvalidStateTransitionException;

	/**
	 * Method to start composition process.
	 *
	 * @param timeoutInSeconds composition will be executed in given timeout constraint.
	 * @return {@link PROSECOSolution}
	 * @throws NoStrategyFoundASolutionException thrown when none of the strategies found a solution
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 * @throws PrototypeCouldNotBeExtractedException thrown when prototype could not be extracted
	 */
	public PROSECOSolution startComposition(int timeoutInSeconds) throws NoStrategyFoundASolutionException, InvalidStateTransitionException, PrototypeCouldNotBeExtractedException;

	/**
	 *
	 * @return processId
	 */
	public String getProcessId();

	/**
	 * Method to call after composition is done. Handles the grounding process for the chosen strategy and deploys it
	 *
	 * @param solution {@link PROSECOSolution} that shows winning strategy
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 * @throws GroundingNotSuccessfulForAnyStrategyException thrown when tried to ground all available strategies but none of them succeeded
	 */
	public void chooseAndDeploySolution(PROSECOSolution solution) throws InvalidStateTransitionException, GroundingNotSuccessfulForAnyStrategyException;

	/**
	 * Updates the interview with given question-answer map
	 *
	 * @param answers a map of questions and answers
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	public void updateInterview(Map<String, String> answers) throws InvalidStateTransitionException;

	/**
	 * Returns the environment values for the current process
	 *
	 * @return {@link PROSECOProcessEnvironment}
	 * @throws InvalidStateTransitionException
	 */
	public PROSECOProcessEnvironment getProcessEnvironment() throws InvalidStateTransitionException;

	/**
	 * Returns the state of the current process
	 *
	 * @return {@link EProcessState}
	 */
	public EProcessState getProcessState();

}
