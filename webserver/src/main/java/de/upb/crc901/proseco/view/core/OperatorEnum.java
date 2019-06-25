package de.upb.crc901.proseco.view.core;

/**
 * Enum of supported Operator types
 *
 * @author kadirayk
 *
 */
public enum OperatorEnum {

	EQUAL("="), GREATER(">"), LESS("<"), GREATER_EQUAL(">="), LESS_EQUAL("<="), AND("&"), OR("|"), NOT("!"), LEFT_P("("), RIGHT_P(")");

	private String value;

	OperatorEnum(String value) {
		this.value = value;
	}

	public static OperatorEnum findByValue(String value) {
		for (OperatorEnum e : values()) {
			if (e.value.equals(value)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Arithmetic operators has a high precedence over logic operators
	 *
	 * @return
	 */
	public int precedence() {
		switch (this) {
		case EQUAL:
		case GREATER:
		case GREATER_EQUAL:
		case LESS:
		case LESS_EQUAL:
			return 3;
		case NOT:
			return 2;
		default:
			return 1;
		}
	}

	public String value() {
		return this.value;
	}

}
