package de.upb.crc901.proseco.commons.interview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.upb.crc901.proseco.commons.util.ListUtil;

/**
 * QuestionCollection provides easy access to questions by their path
 *
 * @author kadirayk
 *
 */
public class QuestionCollection {
	private List<Question> questions;
	private Map<String, Question> questionMap;

	public List<Question> getQuestions() {
		return this.questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
		if (ListUtil.isNotEmpty(questions)) {
			this.questionMap = new HashMap<>();
			for (Question q : questions) {
				this.questionMap.put(q.getId(), q);
			}
		}
	}

	public Question getQuestionById(String id) {
		return this.questionMap.get(id);
	}

}
