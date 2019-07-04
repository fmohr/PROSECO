package de.upb.crc901.proseco.view.core;

/**
 * Node is an abstract class of Operands or Operators in an expression
 *
 * @author kadirayk
 *
 */
public abstract class Node {
	private String value;

	public Node(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
