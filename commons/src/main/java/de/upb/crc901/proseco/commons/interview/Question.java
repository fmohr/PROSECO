package de.upb.crc901.proseco.commons.interview;

import java.io.Serializable;

import de.upb.crc901.proseco.commons.html.UIElement;

/**
 * Question is the building block of Interview which consists of UI element,
 * question content, and answer
 *
 * @author kadirayk
 *
 */
@SuppressWarnings("serial")
public class Question implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5528509655466202445L;
	/**
	 *
	 */
	private String id;
	private String content;
	private UIElement uiElement;

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UIElement getUiElement() {
		return this.uiElement;
	}

	public void setUiElement(UIElement uiElement) {
		this.uiElement = uiElement;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.uiElement == null) ? 0 : this.uiElement.hashCode());
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
		Question other = (Question) obj;
		if (this.content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!this.content.equals(other.content)) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.uiElement == null) {
			if (other.uiElement != null) {
				return false;
			}
		} else if (!this.uiElement.equals(other.uiElement)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Question [id=" + this.id + ", content=" + this.content + ", uiElement=" + this.uiElement + "]";
	}
}
