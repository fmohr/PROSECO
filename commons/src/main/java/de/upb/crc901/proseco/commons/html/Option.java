package de.upb.crc901.proseco.commons.html;

import java.util.Map;

/**
 * HTML Option element &lt;option\&gt;
 *
 * @author kadirayk
 *
 */
public class Option extends UIElement {

	/**
	 *
	 */
	private static final long serialVersionUID = 4149880975608552766L;
	private static final String TAG = "option";

	/**
	 * empty constructor needed for YAML parser
	 */
	public Option() {
		this.setTag(TAG);
	}

	public Option(String content, Map<String, String> attributes) {
		this.setTag(TAG);
		this.setContent(content);
		this.setAttributes(attributes);
	}

}
