package de.upb.crc901.proseco.view.app.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.proseco.commons.config.DomainConfig;
import de.upb.crc901.proseco.commons.html.HTMLConstants;
import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.interview.Question;
import de.upb.crc901.proseco.commons.interview.State;
import de.upb.crc901.proseco.commons.util.ListUtil;

/**
 * Interview Data Transfer Object class
 *
 *
 * @author kadirayk
 *
 */
public class InterviewDTO {

	private static final Logger logger = LoggerFactory.getLogger(DomainConfig.class);

	private String processId; // the PROSECO service construction process id to which this interview belongs

	private String content;

	private InterviewFillout interviewFillout;

	private String interviewHTML;

	private String debugHTML;

	private boolean showInterview;

	private boolean showSubmit;

	private boolean showConfigurationPane;

	private boolean showConsole;

	private boolean upload;

	public boolean isUpload() {
		return this.upload;
	}

	public void setUpload(final boolean upload) {
		this.upload = upload;
	}

	public boolean isShowConsole() {
		return this.showConsole;
	}

	public void setShowConsole(final boolean showConsole) {
		this.showConsole = showConsole;
	}

	public boolean isShowSubmit() {
		return this.showSubmit;
	}

	public void setShowSubmit(final boolean showSubmit) {
		this.showSubmit = showSubmit;
	}

	public String getProcessId() {
		return this.processId;
	}

	public void setProcessId(final String id) {
		this.processId = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public InterviewFillout getInterviewFillout() {
		return this.interviewFillout;
	}

	/**
	 * Do not show submit button for questions without uiElements (i.e. pages that
	 * do not require input)
	 */
	private void setShowSubmitValue() {
		List<Question> questions = this.interviewFillout.getCurrentState().getQuestions();
		if (ListUtil.isNotEmpty(questions)) {
			for (Question q : questions) {
				if (q.getUiElement() != null) {
					this.showSubmit = true;
				}
			}
		}

	}

	public void setInterviewFillout(final InterviewFillout interviewFillout) {
		this.interviewFillout = interviewFillout;
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Appending HTML for current state: %s", interviewFillout.getCurrentState()));
		}
		this.interviewHTML = interviewFillout.getHTMLOfOpenQuestionsInCurrentState();
		this.setShowSubmitValue();
		this.createDebugHtmlTable(interviewFillout);
	}

	/**
	 * Creates debug HTML table for interview
	 *
	 * @param interview
	 */
	private void createDebugHtmlTable(final InterviewFillout interviewFillout) {
		StringBuilder htmlElement = new StringBuilder();
		htmlElement.append("<div id=\"debugBox\" ng-show=\"pac.showDebugTable();\">");
		htmlElement.append(HTMLConstants.LINE_BREAK).append("Debug: ");
		htmlElement.append("<table style=\"width: 100%\" border=\"1\">").append(HTMLConstants.TR_OPEN).append(HTMLConstants.TH_OPEN).append("State").append(HTMLConstants.TH_CLOSE).append(HTMLConstants.TH_OPEN).append("qId")
				.append(HTMLConstants.TH_CLOSE).append(HTMLConstants.TH_OPEN).append("Question").append(HTMLConstants.TH_CLOSE).append(HTMLConstants.TH_OPEN).append("answer").append(HTMLConstants.TH_CLOSE).append(HTMLConstants.TR_CLOSE);
		this.createStatesHTML(interviewFillout, htmlElement);
		htmlElement.append("</table>").append("</div>");
		this.debugHTML = htmlElement.toString();
	}

	private void createStatesHTML(final InterviewFillout interviewFillout, StringBuilder htmlElement) {
		for (State state : interviewFillout.getInterview().getStates()) {
			if (state.getName().equals("timeout")) {
				continue;
			}
			htmlElement.append("<tr");
			if (state.getName().equals(interviewFillout.getCurrentState().getName())) {
				htmlElement.append(" class=\"currentState\" ");
			}
			htmlElement.append("><td rowspan=\"").append(state.getQuestions().size()).append("\">").append(state.getName()).append(HTMLConstants.TD_CLOSE);

			this.createQuestionsHTML(interviewFillout, htmlElement, state);

		}
	}

	private void createQuestionsHTML(final InterviewFillout interviewFillout, StringBuilder htmlElement, State state) {
		boolean isFirstLoop = false;
		for (Question question : state.getQuestions()) {
			if (isFirstLoop) {
				htmlElement.append("<tr");
				if (state.getName().equals(interviewFillout.getCurrentState().getName())) {
					htmlElement.append(" class=\"currentState\" ");
				}
				htmlElement.append(">");
			}
			htmlElement.append(HTMLConstants.TD_OPEN).append(question.getId()).append(HTMLConstants.TD_CLOSE);
			htmlElement.append(HTMLConstants.TD_OPEN).append(question.getContent()).append(HTMLConstants.TD_CLOSE);
			htmlElement.append(HTMLConstants.TD_OPEN);
			if (interviewFillout.getAnswers().containsKey(question.getId())) {
				htmlElement.append(interviewFillout.getAnswers().get(question.getId()));
			}
			htmlElement.append(HTMLConstants.TD_CLOSE).append(HTMLConstants.TR_CLOSE);
			isFirstLoop = true;
		}
	}

	public boolean isShowConfigurationPane() {
		return this.showConfigurationPane;
	}

	public boolean isShowInterview() {
		return this.showInterview;
	}

	public void setShowInterview(final boolean showInterview) {
		this.showInterview = showInterview;
	}

	public void setShowConfigurationPane(final boolean showConfigurationPane) {
		this.showConfigurationPane = showConfigurationPane;
	}

	public String getInterviewHTML() {
		return this.interviewHTML;
	}

	public void setInterviewHTML(final String interviewHTML) {
		this.interviewHTML = interviewHTML;
	}

	public String getDebugHTML() {
		return this.debugHTML;
	}

	public void setDebugHTML(final String debugHTML) {
		this.debugHTML = debugHTML;
	}

	@Override
	public String toString() {
		return "InterviewDTO [processId=" + this.processId + ", content=" + this.content + ", interviewFillout=" + this.interviewFillout + ", interviewHTML=" + this.interviewHTML + ", debugHTML=" + this.debugHTML + ", showInterview="
				+ this.showInterview + ", showSubmit=" + this.showSubmit + ", showConfigurationPane=" + this.showConfigurationPane + ", showConsole=" + this.showConsole + ", upload=" + this.upload + "]";
	}
}
