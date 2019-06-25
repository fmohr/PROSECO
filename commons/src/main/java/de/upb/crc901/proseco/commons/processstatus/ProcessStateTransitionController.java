package de.upb.crc901.proseco.commons.processstatus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Manages transitions between states ({@link EProcessState}). Each state is allowed to move to only certain states.
 * If an invalid transition is wanted to be performed {@link InvalidStateTransitionException} is thrown
 *
 * <br>
 * <br>
 * <b>Allowed State Transitions:</b><br>
 *
 * <table border="1">
 * <tr>
 * <td><b>Current State</b></td>
 * <td><b>Allowed Next States</b></td>
 * </tr>
 * <tr>
 * <td>INIT</td>
 * <td>INIT, CREATED</td>
 * </tr>
 * <tr>
 * <td>CREATED</td>
 * <td>DOMAIN_DEFINITION</td>
 * </tr>
 * <tr>
 * <td>DOMAIN_DEFINITION</td>
 * <td>INTERVIEW</td>
 * </tr>
 * <tr>
 * <td>INTERVIEW</td>
 * <td>INTERVIEW, COMPOSITION</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>COMPOSITION</td>
 * <td>PROTOTYPE_EXTRACTED</td>
 * </tr>
 * <tr>
 * <td>PROTOTYPE_EXTRACTED</td>
 * <td>STRATEGY_CHOSEN</td>
 * </tr>
 * <tr>
 * <td>STRATEGY_CHOSEN</td>
 * <td>GROUNDING</td>
 * </tr>
 * <tr>
 * <td>GROUNDING</td>
 * <td>DEPLOYMENT</td>
 * </tr>
 * <tr>
 * <td>DEPLOYMENT</td>
 * <td>DONE</td>
 * </tr>
 * <tr>
 * <td>DONE</td>
 * <td>-</td>
 * </tr>
 * </table>
 *
 * @author kadirayk
 *
 */
public class ProcessStateTransitionController {

	// currentState, possible next states
	private static Map<EProcessState, List<EProcessState>> transitionMap = new EnumMap<>(EProcessState.class);

	static {
		final List<EProcessState> initStates = new ArrayList<>();
		initStates.add(EProcessState.CREATED);
		initStates.add(EProcessState.INIT);
		transitionMap.put(EProcessState.INIT, initStates);

		final List<EProcessState> createdStates = new ArrayList<>();
		createdStates.add(EProcessState.DOMAIN_DEFINITION);
		transitionMap.put(EProcessState.CREATED, createdStates);

		final List<EProcessState> domainStates = new ArrayList<>();
		domainStates.add(EProcessState.INTERVIEW);
		transitionMap.put(EProcessState.DOMAIN_DEFINITION, domainStates);

		final List<EProcessState> interviewStates = new ArrayList<>();
		interviewStates.add(EProcessState.INTERVIEW);
		interviewStates.add(EProcessState.COMPOSITION);
		transitionMap.put(EProcessState.INTERVIEW, interviewStates);

		final List<EProcessState> compositionStates = new ArrayList<>();
		compositionStates.add(EProcessState.PROTOTYPE_EXTRACTED);
		transitionMap.put(EProcessState.COMPOSITION, compositionStates);

		final List<EProcessState> prototypeStates = new ArrayList<>();
		prototypeStates.add(EProcessState.STRATEGY_CHOSEN);
		transitionMap.put(EProcessState.PROTOTYPE_EXTRACTED, prototypeStates);

		final List<EProcessState> searchStates = new ArrayList<>();
		searchStates.add(EProcessState.GROUNDING);
		transitionMap.put(EProcessState.STRATEGY_CHOSEN, searchStates);

		final List<EProcessState> groundingStates = new ArrayList<>();
		groundingStates.add(EProcessState.DEPLOYMENT);
		transitionMap.put(EProcessState.GROUNDING, groundingStates);

		final List<EProcessState> deploymentStates = new ArrayList<>();
		deploymentStates.add(EProcessState.DONE);
		transitionMap.put(EProcessState.DEPLOYMENT, deploymentStates);

		final List<EProcessState> doneStates = new ArrayList<>();
		transitionMap.put(EProcessState.DONE, doneStates);

	}

	private ProcessStateTransitionController() {
	}

	/**
	 * Returns the nextState if it is allowed to moved to it from currentState. Else {@link InvalidStateTransitionException} is thrown
	 *
	 * @param currentState the state process is currently in
	 * @param nextState the desired state to move process to
	 * @return {@link EProcessState}
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	public static EProcessState moveToNextState(final EProcessState currentState, final EProcessState nextState) throws InvalidStateTransitionException {
		if (transitionMap.get(currentState).contains(nextState)) {
			return nextState;
		} else {
			throw new InvalidStateTransitionException(String.format("CurrentState: %s, NextState: %s", currentState, nextState));
		}
	}

}
