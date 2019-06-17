package de.upb.crc901.proseco.view.core.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.upb.crc901.proseco.view.core.ExpressionEvaluator;

public class ExpressionTest {

	@Test
	public void test1() {
		String exp = "a=a & (b=c|(c=c))";
		ExpressionEvaluator ev = new ExpressionEvaluator(exp);
		assertTrue(ev.evaluateExpression());
	}

	@Test
	public void test2() {
		String exp = "a=b | (b=c | (c=c & d=d)))";
		ExpressionEvaluator ev = new ExpressionEvaluator(exp);
		assertTrue(ev.evaluateExpression());
	}

	@Test
	public void testFalse() {
		String exp = "a=a & (b=c | (c=c & d=d)) & f=f & g=h )";
		ExpressionEvaluator ev = new ExpressionEvaluator(exp);
		assertFalse(ev.evaluateExpression());
	}

	@Test
	public void testTrue3() {
		String exp = "a=a & (b=c | (c=c & d=d)) & f=f & g=g )";
		ExpressionEvaluator ev = new ExpressionEvaluator(exp);
		assertTrue(ev.evaluateExpression());
	}

	@Test
	public void testNumericOperationTrue() {
		String exp = "3>1 & (!(4>=4) | 2=2 | !(3<=1))";
		ExpressionEvaluator ev = new ExpressionEvaluator(exp);
		assertTrue(ev.evaluateExpression());
	}

	@Test
	public void testNumericOperationFalse() {
		String exp = "3>1 & (!(4>=4) | 2!=2 | !(3>=1))";
		ExpressionEvaluator ev = new ExpressionEvaluator(exp);
		assertFalse(ev.evaluateExpression());
	}

	@Test
	public void testStringEquals() {
		String exp = "a=b";
		ExpressionEvaluator ev = new ExpressionEvaluator(exp);
		assertFalse(ev.evaluateExpression());

		String exp2 = "a!=b";
		ev = new ExpressionEvaluator(exp2);
		assertTrue(ev.evaluateExpression());
	}

}
