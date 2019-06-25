package de.upb.crc901.proseco.view.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import de.upb.crc901.proseco.commons.controller.ProcessController;
import de.upb.crc901.proseco.commons.html.Script;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.processstatus.EProcessState;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.processstatus.ProcessStateProvider;
import de.upb.crc901.proseco.commons.util.ListUtil;
import de.upb.crc901.proseco.commons.util.PROSECOProcessEnvironment;
import de.upb.crc901.proseco.commons.util.Parser;
import de.upb.crc901.proseco.commons.util.SerializationUtil;
import de.upb.crc901.proseco.core.composition.FileBasedConfigurationProcess;
import de.upb.crc901.proseco.view.app.model.InterviewDTO;
import de.upb.crc901.proseco.view.app.model.StrategyCandidateFoundEvent;
import de.upb.crc901.proseco.view.app.model.StrategyCandidatesDatastore;
import de.upb.crc901.proseco.view.core.NextStateNotFoundException;

/**
 * Interview Controller for web application
 *
 *
 * @author kadirayk
 *
 */
@Controller
public class InterviewController {

	private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);
	private static final String INIT_TEMPLATE = "initiator";
	private static final String RESULT_TEMPLATE = "result";

	private static final String ROOT_TEMPLATE = "index";
	private static final String TIMEOUT = "timeout";
	private static final String STATUS = "status";
	private ProcessController processController;

	private final StrategyCandidatesDatastore datastore = new StrategyCandidatesDatastore();
	private static final Map<String, Long> deadlineCache = new HashMap<>();

	/**
	 * Displays Interview initiator. Interview initiator is the step where the user
	 * inputs the required keywords for corresponding prototype to be found.
	 *
	 * @param model
	 * @return
	 */
	@GetMapping("/")
	public String init(final Model model) {
		model.addAttribute("interviewDTO", new InterviewDTO());
		return INIT_TEMPLATE;
	}

	@GetMapping("/index")
	public String index() {
		return ROOT_TEMPLATE;
	}

	@GetMapping("/strategy/strategyChart")
	public String strategyChartDirective() {
		return "strategy/strategyChart";
	}

	/**
	 * Initiates interview process and decides prototype according to given
	 * information
	 *
	 * @param interviewDTO {@link InterviewDTO} UI-facing definition of the interview
	 * @return String denoting the thymeleaf template name
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	@PostMapping("/")
	public String initSubmit(@ModelAttribute final InterviewDTO interviewDTO) throws InvalidStateTransitionException {

		/* determine domain name */
		final String domainName = interviewDTO.getContent();

		/*
		 * create a new PROSECO service construction process and retrieve the interview
		 */
		try {
			logger.info("Initializing new process folder for domain {}.", domainName);
			this.processController = new FileBasedConfigurationProcess(new File("conf/proseco.conf"));
			this.processController.createNew(null);
			this.processController.fixDomain(domainName);
			final PROSECOProcessEnvironment env = this.processController.getProcessEnvironment();
			final File interviewFile = new File(env.getInterviewDirectory().getAbsolutePath() + File.separator + "interview.yaml");
			logger.info("Reading interview file {}", interviewFile);
			final Parser parser = new Parser();
			interviewDTO.setInterviewFillout(new InterviewFillout(parser.initializeInterviewFromConfig(interviewFile)));
			interviewDTO.setShowInterview(true);
			interviewDTO.setProcessId(env.getProcessId());
			ProcessStateProvider.setProcessStatus(env.getProcessId(), EProcessState.INTERVIEW);
		} catch (final Exception e) {
			logger.error("Error in creating a construction process for domain {}. The exception is as follows:", domainName);
			logger.error(e.getMessage());
		}
		this.saveInterviewState(interviewDTO);
		return RESULT_TEMPLATE;
	}

	/**
	 * Http Get method for /interview/{id} to display the current state of the
	 * interview with the given {id}
	 *
	 * @param id consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @param interviewDTO {@link InterviewDTO} UI-facing definition of the interview
	 * @return String denoting the thymeleaf template name
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	@GetMapping("/interview/{id}")
	public String next(@PathVariable("id") final String id, @ModelAttribute final InterviewDTO interviewDTO) throws InvalidStateTransitionException {
		this.populateInterviewDTO(interviewDTO, id);
		return RESULT_TEMPLATE;
	}

	/**
	 * for moving to previous interview state
	 *
	 * @param init {@link InterviewDTO} UI-facing definition of the interview
	 * @return String denoting the thymeleaf template name
	 */
	@GetMapping("/prev")
	public String prev(@ModelAttribute final InterviewDTO init) {
		return RESULT_TEMPLATE;
	}

	/**
	 * Http Post method for /interview/{id} to post form values and continue to the
	 * next step
	 *
	 * @param interviewDTO {@link InterviewDTO} UI-facing definition of the interview
	 * @param response is any string value that is filled in the form
	 * @param file is any file that is uploaded via the form
	 * @return String denoting the thymeleaf template name
	 * @throws InvalidStateTransitionException
	 * @throws NextStateNotFoundException
	 */
	@PostMapping("/interview/{id}")
	public String nextPost(@PathVariable("id") final String id, @ModelAttribute final InterviewDTO interviewDTO, @RequestParam(required = false, name = "response") final String response,
			@RequestParam(required = false, name = "file") final MultipartFile file) throws InvalidStateTransitionException {

		/* retrieve the interview state */
		logger.info("Receiving response {} and file {} for process id {}. Interview: {}", response, file, id, interviewDTO);
		this.populateInterviewDTO(interviewDTO, id);
		logger.info("Receiving response {} and file {} for process id {}. Interview: {}", response, file, id, interviewDTO);
		final InterviewFillout memorizedInterviewFillout = interviewDTO.getInterviewFillout();
		final PROSECOProcessEnvironment env = ProcessStateProvider.getProcessEnvironment(id, true);

		final Map<String, String> updatedAnswers = new HashMap<>(memorizedInterviewFillout.getAnswers());

		// if it is final state run PrototypeBasedComposer with interview inputs
		if (memorizedInterviewFillout.getCurrentState().getTransition() == null || memorizedInterviewFillout.getCurrentState().getTransition().isEmpty()) {
			final Runnable task = () -> {
				try {
					if (memorizedInterviewFillout.getAnswer(TIMEOUT) == null) {
						logger.error("No question with id 'timeout' has been answered, which is mandatory in PROSECO. The timeout must be an integer and will be interpreted in seconds!");
						return;
					}
					deadlineCache.put(id, (System.currentTimeMillis() + 1000 * Long.parseLong(memorizedInterviewFillout.getAnswer(TIMEOUT))));
					this.processController.startComposition(Integer.parseInt(memorizedInterviewFillout.getAnswer(TIMEOUT)));
				} catch (final Exception e) {
					logger.error(e.getMessage());
				}
			};
			ProcessStateProvider.setProcessStatus(env.getProcessId(), EProcessState.STRATEGY_CHOSEN);
			new Thread(task).start();
			interviewDTO.setShowConfigurationPane(true);
			interviewDTO.setShowConsole(true);
			return RESULT_TEMPLATE;
		}

		// if a file is uploaded save the file to prototype's interview directory set
		// the reference of file (file path) as answer to the respected question in the
		// interview
		if (file != null && !file.isEmpty()) {
			this.handleFileUpload(file, memorizedInterviewFillout, env, updatedAnswers);
		}

		// if any string response is given set the responses as answer to the respected
		// interview question
		if (response != null && !StringUtils.isEmpty(response)) {
			this.handleStringResponse(response, memorizedInterviewFillout, updatedAnswers);
		}
		// update current interview state (to the first state with an unanswered
		// question) and save it
		interviewDTO.setInterviewFillout(new InterviewFillout(memorizedInterviewFillout.getInterview(), updatedAnswers));
		this.processController.updateInterview(updatedAnswers);
		this.saveInterviewState(interviewDTO);
		logger.info("Interview is now in state {}", interviewDTO.getInterviewFillout().getCurrentState());
		return RESULT_TEMPLATE;
	}

	private void handleStringResponse(final String response, final InterviewFillout memorizedInterviewFillout, final Map<String, String> updatedAnswers) {
		final List<String> answers = Arrays.asList(response.split(","));
		final List<Question> questions = memorizedInterviewFillout.getCurrentState().getQuestions();
		if (ListUtil.isNotEmpty(questions)) {
			int i = 0;
			for (final Question q : questions) {
				if (q.getUiElement() instanceof Script) {
					updatedAnswers.put(q.getId(), "script");
					continue;
				}
				final String answerToThisQuestion = answers.get(i);
				logger.info("Processing answer {} to question {}", answerToThisQuestion, q);
				if (q.getUiElement() != null && "file".equals(q.getUiElement().getAttributes().get("type"))) {
					logger.warn("Cannot process file fields in standard process");
					continue;
				}
				if (i < answers.size()) {
					updatedAnswers.put(q.getId(), answerToThisQuestion);
					logger.info("Updating answer of question {} to: {}", q.getId(), answerToThisQuestion);
					i++;
				}
			}
		}
	}

	private void handleFileUpload(final MultipartFile file, final InterviewFillout memorizedInterviewFillout, final PROSECOProcessEnvironment env, final Map<String, String> updatedAnswers) {
		try {
			final List<Question> questions = memorizedInterviewFillout.getCurrentState().getQuestions();
			if (ListUtil.isNotEmpty(questions)) {
				for (final Question q : questions) {
					if ("file".equals(q.getUiElement().getAttributes().get("type"))) {
						final byte[] bytes = file.getBytes();
						if (!env.getInterviewResourcesDirectory().exists()) {
							FileUtils.forceMkdir(env.getInterviewResourcesDirectory());
						}
						final Path path = Paths.get(env.getInterviewResourcesDirectory() + File.separator + q.getId());
						Files.write(path, bytes);
						updatedAnswers.put(q.getId(), path.toFile().getName());
					}
				}
			}

		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Finds interview of the prototype with the given ID
	 *
	 * @param id
	 * @return
	 * @throws InvalidStateTransitionException
	 * @throws Exception
	 */
	private void populateInterviewDTO(final InterviewDTO interviewDTO, final String id) throws InvalidStateTransitionException {
		final PROSECOProcessEnvironment env = this.processController.getProcessEnvironment();
		final InterviewFillout interview = SerializationUtil.readAsJSON(env.getInterviewStateFile());
		interviewDTO.setInterviewFillout(interview);
		interviewDTO.setProcessId(id);
		interviewDTO.setShowInterview(interview.getCurrentState().getTransition() != null);
	}

	/**
	 * saves interview state on current prototype instance's directory
	 *
	 * @param interviewDTO {@link InterviewDTO} UI-facing definition of the interview
	 * @throws InvalidStateTransitionException thrown when a transition to an invalid state is wanted to be performed
	 */
	private void saveInterviewState(final InterviewDTO interviewDTO) throws InvalidStateTransitionException {
		final PROSECOProcessEnvironment env = this.processController.getProcessEnvironment();
		SerializationUtil.writeAsJSON(env.getInterviewStateFile(), interviewDTO.getInterviewFillout());
	}

	@PostMapping(value = "/api/strategy/candidateEval/{id}")
	@ResponseBody
	public ResponseEntity<Object> postCandidateFoundEvent(@PathVariable("id") final String id, @RequestBody final StrategyCandidateFoundEvent e) {
		final Map<String, Object> result = new HashMap<>();
		logger.debug("Received candidate from strategy {}:%n {}", id, e);
		result.put(STATUS, this.datastore.put(id, e));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping(value = "/api/strategy/EvaluationsByTimestamp/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Object> getEvaluationsSortedByTimestamp(@PathVariable("id") final String id) {
		final Map<String, Object> result = new HashMap<>();
		result.put(STATUS, "true");
		result.put("data", this.datastore.getEvaluationsSortedByTimestamp(id));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/api/process/id")
	@ResponseBody
	public Map<String, String> processID(@ModelAttribute final InterviewDTO interviewDTO) {
		logger.debug("Get id of current process");
		final Map<String, String> result = new HashMap<>();
		result.put("processID", interviewDTO.getProcessId());
		return result;
	}

	/**
	 * returns status of the process
	 *
	 * @param processID consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @return status of the process
	 */
	@GetMapping("/api/process/{id}/status")
	@ResponseBody
	public Map<String, String> processStatus(@PathVariable("id") final String processID) {
		logger.trace("Get status of process {}: {}", processID, ProcessStateProvider.getProcessStatus(processID));
		final Map<String, String> result = new HashMap<>();
		result.put(STATUS, ProcessStateProvider.getProcessStatus(processID));
		return result;
	}

	/**
	 * set process of the status
	 *
	 * @param processID consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @param e status to be set
	 * @return {@link ResponseEntity} with http status
	 */
	@PostMapping("/api/process/{id}/status")
	@ResponseBody
	public ResponseEntity<Object> setProcessStatus(@PathVariable("id") final String processID, @RequestBody final Map<String, String> e) {
		logger.trace("Set status of process {} to {}", processID, e.get(STATUS));
		ProcessStateProvider.setProcessStatus(processID, ProcessStateProvider.readProcessStateValue(e.get(STATUS)));
		return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
	}

	/**
	 * Returns remainingTime, completion status and service handle to deployed service
	 *
	 * @param id consists of a domain name and a 10-digit alpha-numeric value (e.g. test-00dc91ae4d)
	 * @return Result of the process
	 */
	@GetMapping(value = "/api/result/{id}")
	@ResponseBody
	public ResponseEntity<Object> pushResult(@PathVariable("id") final String id) {
		final PROSECOProcessEnvironment env = ProcessStateProvider.getProcessEnvironment(id);
		final int remainingTime = this.getTimeoutValue(id);
		final boolean isComplete = this.checkStatus(id);
		String serviceHandle = "";
		try {
			serviceHandle = FileUtils.readFileToString(env.getServiceHandle(), Charset.defaultCharset());
		} catch (final Exception e) {
			logger.trace("No service handle available yet");
		}

		final Map<String, String> result = new HashMap<>();
		result.put("remainingTime", remainingTime + "");
		result.put("isComplete", isComplete + "");
		result.put("serviceHandle", serviceHandle);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private int getTimeoutValue(final String id) {
		final Long deadline = deadlineCache.get(id);
		if (deadline == null) {
			return -1;
		} else {
			return (int) ((deadline - System.currentTimeMillis()) / 1000);
		}
	}

	/**
	 * Checks if the search strategy completed
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private boolean checkStatus(final String id) {
		final PROSECOProcessEnvironment env = ProcessStateProvider.getProcessEnvironment(id);
		return env.getServiceHandle().exists();
	}
}
