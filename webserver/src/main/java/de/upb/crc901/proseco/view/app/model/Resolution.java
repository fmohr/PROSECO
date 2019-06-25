package de.upb.crc901.proseco.view.app.model;

import java.io.Serializable;

public class Resolution implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2509719989340606054L;
	private String height;
	private String width;

	public Resolution() {

	}

	public Resolution(String height, String width) {
		this.height = height;
		this.width = width;
	}

	public String getHeight() {
		return this.height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return this.width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
