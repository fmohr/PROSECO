package de.upb.crc901.proseco.commons.interview;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.upb.crc901.proseco.commons.html.HTMLConstants;
import de.upb.crc901.proseco.commons.html.UIElement;
import de.upb.crc901.proseco.commons.util.ListUtil;

/**
 * Keeps a state of interview with already given answers, and the current state of the interview
 *
 */
@SuppressWarnings("serial")
public class InterviewFillout implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 3224230481272935314L;
	private Interview interview;
	private Map<String, String> answers; // this is a map from question IDs to answers. Using String instead of Question
											// is ON PURPOSE to ease serialization with Jackson!
	private State currentState;

	public InterviewFillout() {
	}

	public InterviewFillout(final Interview interview) {
		super();
		this.interview = interview;
		this.answers = new HashMap<>();
		this.currentState = interview.getStates().get(0);
	}

	public InterviewFillout(final Interview interview, final Map<String, String> answers, final State currentState) {
		super();
		this.interview = interview;
		this.answers = answers;
		this.currentState = currentState;
	}

	/**
	 * This constructor automatically activates the first state with an unanswered
	 * question
	 *
	 * @param interview
	 * @param answers
	 */
	public InterviewFillout(final Interview interview, final Map<String, String> answers) {
		super();
		this.interview = interview;
		this.answers = answers;
		for (final State s : interview.getStates()) {
			final List<Question> questions = s.getQuestions();
			if (ListUtil.isNotEmpty(questions)) {
				for (final Question q : questions) {
					if (!answers.containsKey(q.getId())) {
						this.currentState = s;
						return;
					}
				}
			}
		}
		this.currentState = interview.getStates().get(0);
	}

	public Interview getInterview() {
		return this.interview;
	}

	/**
	 * Returns a map of questions and their answers
	 *
	 * @return map of question-answer pairs
	 */
	public Map<String, String> retrieveQuestionAnswerMap() {
		final Map<String, String> questionAnswerMap = new HashMap<>();
		for (final State s : this.interview.getStates()) {
			final List<Question> questions = s.getQuestions();
			if (ListUtil.isNotEmpty(questions)) {
				for (final Question q : questions) {
					questionAnswerMap.put(q.getContent(), null);
					if (this.answers.containsKey(q.getId())) {
						questionAnswerMap.put(q.getContent(), this.answers.get(q.getId()));
					}
				}
			}
		}
		return questionAnswerMap;
	}

	/**
	 * Update the interview state according to given questionAnswerMap
	 *
	 * @param questionAnswerMap map of question-answer pairs
	 */
	public void updateAnswers(final Map<String, String> questionAnswerMap) {
		for (final Entry<String, String> e : questionAnswerMap.entrySet()) {
			if (e.getValue() != null) {
				for (final State s : this.interview.getStates()) {
					this.handleQuestions(e, s);
				}
			}
		}
	}

	private void handleQuestions(final Entry<String, String> e, final State s) {
		final List<Question> questions = s.getQuestions();
		if (ListUtil.isNotEmpty(questions)) {
			for (final Question q : questions) {
				if ((q.getContent() != null && q.getContent().equals(e.getKey())) || (q.getId().equals(e.getKey()))) {
					this.answers.put(q.getId(), e.getValue());
				}
			}
		}
	}

	public Map<String, String> getAnswers() {
		return this.answers;
	}

	public String getAnswer(final Question q) {
		return this.answers.get(q.getId());
	}

	public String getAnswer(final String questionId) {
		return this.answers.get(questionId);
	}

	/**
	 * return currentState
	 *
	 * @return
	 */
	public State getCurrentState() {
		return this.currentState;
	}

	public boolean allQuestionsInCurrentStateAnswered() {
		// if current state has unanswered questions return current state
		final List<Question> questions = this.currentState.getQuestions();
		if (ListUtil.isNotEmpty(questions)) {
			for (final Question q : questions) {
				if (!this.answers.containsKey(q.getId())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Generates concrete HTML element from the UI Elements of the questions to make
	 * up the form
	 *
	 * @return
	 */
	public String getHTMLOfOpenQuestionsInState(final State s) {
		final StringBuilder htmlElement = new StringBuilder();

		for (final Question q : s.getQuestions()) {
			if (!this.answers.containsKey(q.getId())) {
				final String formQuestion = q.getContent();
				if (formQuestion != null) {
					htmlElement.append(HTMLConstants.LINE_BREAK).append("<h1>" + formQuestion + "</h1>").append(HTMLConstants.LINE_BREAK);
				}
				final UIElement formUiElement = q.getUiElement();
				if (formUiElement != null) {
					htmlElement.append(formUiElement.toHTML()).append(HTMLConstants.LINE_BREAK).append("\n");
				}
			}
		}

		return htmlElement.toString();
	}

	@JsonIgnore
	public String getHTMLOfOpenQuestionsInCurrentState() {
		return this.getHTMLOfOpenQuestionsInState(this.currentState);
	}

	@JsonIgnore
	public String getHTMLOfAllOpenQuestions() {
		final StringBuilder html = new StringBuilder();
		for (final State s : this.interview.getStates()) {
			html.append(this.getHTMLOfOpenQuestionsInState(s)).append("\n");
		}
		return html.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.answers == null) ? 0 : this.answers.hashCode());
		result = prime * result + ((this.currentState == null) ? 0 : this.currentState.hashCode());
		result = prime * result + ((this.interview == null) ? 0 : this.interview.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final InterviewFillout other = (InterviewFillout) obj;
		if (this.answers == null) {
			if (other.answers != null) {
				return false;
			}
		} else if (!this.answers.equals(other.answers)) {
			return false;
		}
		if (this.currentState == null) {
			if (other.currentState != null) {
				return false;
			}
		} else if (!this.currentState.equals(other.currentState)) {
			return false;
		}
		if (this.interview == null) {
			if (other.interview != null) {
				return false;
			}
		} else if (!this.interview.equals(other.interview)) {
			return false;
		}
		return true;
	}

	public void setInterview(final Interview interview) {
		if (this.interview != null) {
			throw new IllegalStateException("Cannot modify interview if it is already set!");
		}
		if (interview == null) {
			throw new IllegalStateException("Cannot set interview to NULL!");
		}
		this.interview = interview;
	}

	public void setAnswers(final Map<String, String> answers) {
		if (this.answers != null) {
			throw new IllegalStateException("Cannot modify answers if they are already set!");
		}
		if (answers == null) {
			throw new IllegalStateException("Cannot set answer to NULL!");
		}
		this.answers = answers;
	}

	public void setCurrentState(final State currentState) {
		if (this.currentState != null) {
			throw new IllegalStateException("Cannot modify state if it is already set!");
		}
		if (currentState == null) {
			throw new IllegalStateException("Cannot set state to NULL!");
		}
		this.currentState = currentState;
	}

}
