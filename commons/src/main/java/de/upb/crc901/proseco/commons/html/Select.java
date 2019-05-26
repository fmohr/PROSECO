package de.upb.crc901.proseco.commons.html;

import java.util.List;
import java.util.Map;

/**
 * HTML Select element &lt;select\&gt;
 *
 * @author kadirayk
 *
 */
public class Select extends UIElement {
	/**
	 *
	 */
	private static final long serialVersionUID = 9213037056898530411L;
	private List<Option> options;
	private static final String TAG = "select";

	public Select() {
		this.setTag(TAG);
	}

	public Select(String content, Map<String, String> attributes, List<Option> options) {
		this.setTag(TAG);
		this.setContent(content);
		this.setAttributes(attributes);
		this.setOptions(options);
	}

	public List<Option> getOptions() {
		return this.options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	@Override
	public String toHTML() {
		StringBuilder html = new StringBuilder("<");
		html.append(this.getTag());
		if (this.getAttributes() != null) {
			for (Map.Entry<String, String> entry : this.getAttributes().entrySet()) {
				if (entry.getKey().equals("name")) {
					entry.setValue("response");
				}
				html.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
			}
		}
		html.append(">");
		if (this.options != null) {
			for (Option o : this.options) {
				html.append("\n\t").append(o.toHTML());
			}
		}
		html.append("\n</").append(this.getTag()).append(">");
		return html.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.options == null) ? 0 : this.options.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		Select mObj = (Select) obj;
		return this.options.equals(mObj.getOptions());
	}

}
