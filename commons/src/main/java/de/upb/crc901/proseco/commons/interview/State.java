package de.upb.crc901.proseco.commons.interview;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import de.upb.crc901.proseco.commons.util.ListUtil;

/**
 *
 * @author fmohr
 *
 */
public class State implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -849218511658141465L;
	private String name;
	private Map<String, String> transition;
	private List<Question> questions;

	public Question getQuestionById(String id) {
		Question question = null;
		if (ListUtil.isNotEmpty(this.questions)) {
			for (Question q : this.questions) {
				if (id.equals(q.getId())) {
					return q;
				}
			}
		}
		return question;
	}

	public List<Question> getQuestions() {
		return this.questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getTransition() {
		return this.transition;
	}

	public void setTransition(Map<String, String> transition) {
		this.transition = transition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.questions == null) ? 0 : this.questions.hashCode());
		result = prime * result + ((this.transition == null) ? 0 : this.transition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		State other = (State) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.questions == null) {
			if (other.questions != null) {
				return false;
			}
		} else if (!this.questions.equals(other.questions)) {
			return false;
		}
		if (this.transition == null) {
			if (other.transition != null) {
				return false;
			}
		} else if (!this.transition.equals(other.transition)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "State [name=" + this.name + ", transition=" + this.transition + ", questions=" + this.questions + "]";
	}
}
