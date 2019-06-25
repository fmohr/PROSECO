package de.upb.crc901.proseco.commons.html;

import java.util.Map;

/**
 * HTML Input element &lt;input\&gt;
 *
 * @author kadirayk
 *
 */
public class Input extends UIElement {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String TAG = "input";

	public Input() {
		this.setTag(TAG);
	}

	public Input(String content, Map<String, String> attributes) {
		this.setTag(TAG);
		this.setContent(content);
		this.setAttributes(attributes);
	}

}
