package de.upb.crc901.proseco.commons.html;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.upb.crc901.proseco.commons.interview.Interview;
import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.interview.QuestionCollection;
import de.upb.crc901.proseco.commons.interview.State;
import de.upb.crc901.proseco.commons.util.ListUtil;

/**
 * Interview Parser utility
 * 
 * @author kadirayk
 *
 */
public class Parser {

	/**
	 * Parses question repository with the given path
	 * 
	 * @param filePath
	 * @return
	 */
	public QuestionCollection parseQuestion(String filePath) {
		QuestionCollection qCollection = null;
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			qCollection = mapper.readValue(new File(filePath), QuestionCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qCollection;

	}

	/**
	 * Parses interview definition with the given path and returns interview object
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public Interview initializeInterviewFromConfig(File interviewFile) throws JsonParseException, JsonMappingException, IOException {
		
		/* load and parse interview core file */
		File interviewFolder = interviewFile.getParentFile();
		Interview interview = null;
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		interview = mapper.readValue(interviewFile, Interview.class);
		if (interview == null)
			throw new IllegalStateException("Interview parser returned NULL");
		
		/* retrieve questions specified in question repository (if any exists) */
		String questionPath = interview.getQuestionRepo();
		File questionFile = new File(interviewFolder + File.separator + questionPath);
		QuestionCollection qCollection = parseQuestion(questionFile.getAbsolutePath());
		
		/* check whether questions of the interview file must be overwritten by those in the question file (iff the content is undefined) */
//		Set<String> questionSet = new HashSet<>();
//		interview.setQuestionSet(questionSet);
		if (qCollection != null) {
			for (State s : interview.getStates()) {
				List<Question> questions = s.getQuestions();
				if (ListUtil.isNotEmpty(questions)) {
					for (Question q : new ArrayList<>(questions)) {
//						StringBuilder questionSetItem = new StringBuilder(s.getName());
//						questionSetItem.append(".").append(q.getId());
						if (q.getContent() == null) { // overwrite question by the question in the question file if no content is provided
							questions.remove(q);
							questions.add(qCollection.getQuestionById(q.getId()));
						}
//						questionSet.add(questionSetItem.toString());
					}
				}
			}

		}
		setTimeOutQuestion(interview);
		return interview;
	}

	private void setTimeOutQuestion(Interview interview) {
		List<State> states = interview.getStates();
		State stateBeforeTimeout = states.get(states.size() - 2);
		State stateAfterTimeout = states.get(states.size() - 1);
		Map<String, String> transition = new HashMap<>();
		transition.put("default", "timeout");
		stateBeforeTimeout.setTransition(transition);
		State timeOutState = new State();
		timeOutState.setName("timeout");
		Map<String, String> timeoutTransition = new HashMap<>();
		timeoutTransition.put("default", stateAfterTimeout.getName());
		timeOutState.setTransition(timeoutTransition);
		List<Question> questions = new ArrayList<>();
		Question q = new Question();
		q.setId("timeout");
		q.setContent("Please specify a time out value in seconds");
		Input input = new Input();
		Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put("name", "timeout");
		attributeMap.put("type", "number");
		input.setAttributes(attributeMap);
		q.setUiElement(input);
		questions.add(q);
		timeOutState.setQuestions(questions);
		states.add(timeOutState);
		Map<String, State> stateMap = interview.getStateMap();
		stateMap.put("timeout", timeOutState);
	}

}
