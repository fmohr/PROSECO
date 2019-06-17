package de.upb.crc901.proseco.view.core;

/**
 * NumericOperand is an operand in the expression with numeric value
 *
 * @author kadirayk
 *
 */
public class NumericOperand extends Operand {

	private double numericValue;

	public NumericOperand(String value) {
		super(value);
		this.numericValue = Double.valueOf(value);
	}

	public double getNumericValue() {
		return this.numericValue;
	}

	public void setNumericValue(double numericValue) {
		this.numericValue = numericValue;
	}

}
