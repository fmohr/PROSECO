package de.upb.crc901.proseco.commons.html;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author kadirayk
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({ @JsonSubTypes.Type(value = Input.class, name = "Input"), @JsonSubTypes.Type(value = Select.class, name = "Select"), @JsonSubTypes.Type(value = Option.class, name = "Option"),
		@JsonSubTypes.Type(value = Script.class, name = "Script") })
public abstract class UIElement implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5890195807308722546L;
	private String tag;
	private String content;
	private Map<String, String> attributes;

	public String getTag() {
		return this.tag;
	}

	public void setTag(final String tag) {
		this.tag = tag;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(final Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public UIElement() {
	}

	/**
	 * Generate actual HTML element
	 *
	 * @return String consists of HTML element
	 */
	public String toHTML() {
		final StringBuilder html = new StringBuilder("<");
		html.append(this.tag);
		if (this.attributes != null) {
			boolean isFile = false;
			if ("file".equals(this.attributes.get("type"))) {
				isFile = true;
			}
			for (final Map.Entry<String, String> entry : this.attributes.entrySet()) {
				if (!isFile && entry.getKey().equals("name")) {
					entry.setValue("response");
				} else if (isFile && entry.getKey().equals("name")) {
					entry.setValue("file");
				}
				html.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
			}
		}
		html.append(">");
		if (this.content != null) {
			html.append(this.content);
		}
		html.append("</").append(this.tag).append(">");
		return html.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
		result = prime * result + ((this.content == null) ? 0 : this.content.hashCode());
		result = prime * result + ((this.tag == null) ? 0 : this.tag.hashCode());
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
		final UIElement other = (UIElement) obj;
		if (this.attributes == null) {
			if (other.attributes != null) {
				return false;
			}
		} else if (!this.attributes.equals(other.attributes)) {
			return false;
		}
		if (this.content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!this.content.equals(other.content)) {
			return false;
		}
		if (this.tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!this.tag.equals(other.tag)) {
			return false;
		}
		return true;
	}

}