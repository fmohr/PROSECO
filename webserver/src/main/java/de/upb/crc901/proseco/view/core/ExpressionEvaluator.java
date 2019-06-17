package de.upb.crc901.proseco.view.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

/**
 * ExpressionEvaluator, parses infix expression and converts to postfix
 * expression using Shunting-yard algorithm then operates over postfix
 * expression to evaluate given expression
 * 
 * @author kadirayk
 *
 */
public class ExpressionEvaluator {
	private Deque<Node> operatorStack;
	private Queue<Node> postfixQueue;
	private Deque<Node> evaluationStack;

	public ExpressionEvaluator(String expression) {
		operatorStack = new ArrayDeque<>();
		postfixQueue = new LinkedList<>();
		convertToPostfix(expression);
	}

	public void postFixToString() {
		for (Node n : postfixQueue) {
			System.out.print(n.getValue() + " ");
		}
		System.out.println("");
	}

	/**
	 * Evaluates the postfix expression
	 * 
	 * @return
	 */
	public boolean evaluateExpression() {
		evaluationStack = new ArrayDeque<>();
		for (Node n : postfixQueue) {
			if (n instanceof Operand) {
				evaluationStack.push(n);
			} else if (n instanceof Operator && n.getValue().equals(OperatorEnum.NOT.value())) {
				Node node = evaluationStack.pop();
				boolean evaluation = !Boolean.valueOf(node.getValue());
				Node result = new Operand(String.valueOf(evaluation));
				evaluationStack.push(result);
			} else if (n instanceof Operator) {
				Node nodeLast = evaluationStack.pop();
				Node nodeFirst = evaluationStack.pop();
				Node result = operate(nodeFirst, nodeLast, n);
				evaluationStack.push(result);
			}
		}
		Node node = evaluationStack.pop();
		return Boolean.valueOf(node.getValue());
	}

	/**
	 * Executes the actual operation and returns the result
	 * 
	 * @param nodeFirst
	 *            first operand
	 * @param nodeLast
	 *            last operand
	 * @param operation
	 *            operation to be applied to the operands
	 * @return the result of the operation
	 * @see Node
	 */
	private Node operate(Node nodeFirst, Node nodeLast, Node operation) {
		boolean evaluation = false;
		if (operation.getValue().equals(OperatorEnum.AND.value())) {
			evaluation = Boolean.valueOf(nodeFirst.getValue()) && Boolean.valueOf(nodeLast.getValue());
		} else if (operation.getValue().equals(OperatorEnum.EQUAL.value())) {
			evaluation = nodeFirst.getValue().equals(nodeLast.getValue());
		} else if (operation.getValue().equals(OperatorEnum.OR.value())) {
			evaluation = Boolean.valueOf(nodeFirst.getValue()) || Boolean.valueOf(nodeLast.getValue());
		} else {
			evaluation = operateNumeric(nodeFirst, nodeLast, operation);
		}
		return new Operand(String.valueOf(evaluation));
	}

	private boolean operateNumeric(Node nodeFirst, Node nodeLast, Node operation) {
		boolean evaluation = false;
		NumericOperand op1 = getNumericOperand(nodeFirst);
		NumericOperand op2 = getNumericOperand(nodeLast);
		if (operation.getValue().equals(OperatorEnum.GREATER.value())) {
			evaluation = op1.getNumericValue() > op2.getNumericValue();
		} else if (operation.getValue().equals(OperatorEnum.GREATER_EQUAL.value())) {
			evaluation = op1.getNumericValue() >= op2.getNumericValue();
		} else if (operation.getValue().equals(OperatorEnum.LESS.value())) {
			evaluation = op1.getNumericValue() < op2.getNumericValue();
		} else if (operation.getValue().equals(OperatorEnum.LESS_EQUAL.value())) {
			evaluation = op1.getNumericValue() <= op2.getNumericValue();
		}
		return evaluation;
	}

	private NumericOperand getNumericOperand(Node operand) {
		if (operand instanceof NumericOperand) {
			return (NumericOperand) operand;
		} else {
			throw new IllegalArgumentException("Operands must be numeric for this operation");
		}
	}

	/**
	 * Parses String infix expression and converts to postfix expression using
	 * Shunting-yard algorithm
	 * 
	 * @param expression
	 */
	private void convertToPostfix(String expression) {
		// TODO: reduce Cognitive Complexity
		int cursor = 0;
		while (cursor < expression.length()) {
			StringBuilder str = new StringBuilder();
			boolean isOperand = false;
			while (cursor < expression.length()
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.AND.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.EQUAL.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.OR.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.NOT.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.LEFT_P.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.RIGHT_P.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.GREATER.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.LESS.value())) {
				str.append(expression.substring(cursor, cursor + 1));
				cursor++;
				isOperand = true;
			}
			if (isOperand) {
				String value = str.toString().trim();
				if (!value.isEmpty()) {
					Node operand = null;
					if (isNumericOperand(value)) {
						operand = new NumericOperand(value);
					} else {
						operand = new Operand(value);
					}
					postfixQueue.add(operand);
				}
			} else {
				String opString = expression.substring(cursor, cursor + 1);
				if ((opString.equals(OperatorEnum.GREATER.value()) || opString.equals(OperatorEnum.LESS.value()))
						&& cursor < expression.length()) {
					String operator = expression.substring(cursor, cursor + 2);
					if (operator.equals(OperatorEnum.GREATER_EQUAL.value())
							|| operator.equals(OperatorEnum.LESS_EQUAL.value())) {
						opString = operator;
						cursor++;
					}
				}
				if (!opString.equals(OperatorEnum.LEFT_P.value()) && !opString.equals(OperatorEnum.RIGHT_P.value())) {
					while (!operatorStack.isEmpty()
							&& !operatorStack.peek().getValue().equals(OperatorEnum.LEFT_P.value())
							&& !operatorStack.peek().getValue().equals(OperatorEnum.RIGHT_P.value())
							&& isHigerPrec(opString, operatorStack.peek().getValue())) {
						postfixQueue.add(operatorStack.pop());
					}
					Node operator = new Operator(opString);
					operatorStack.push(operator);
				} else if (opString.equals(OperatorEnum.LEFT_P.value())) {
					Node operator = new Operator(opString);
					operatorStack.push(operator);
				} else if (opString.equals(OperatorEnum.RIGHT_P.value())) {
					while (!operatorStack.isEmpty()
							&& !operatorStack.peek().getValue().equals(OperatorEnum.LEFT_P.value())) {
						postfixQueue.add(operatorStack.pop());
					}
					if (!operatorStack.isEmpty()) {
						operatorStack.pop();
					}

				}

				cursor++;
			}
		}

		while (!operatorStack.isEmpty())
			postfixQueue.add(operatorStack.pop());
	}

	private boolean isNumericOperand(String value) {
		return StringUtils.isNumeric(value);
	}

	private boolean isHigerPrec(String op, String sub) {
		return OperatorEnum.findByValue(sub).precedence() >= OperatorEnum.findByValue(op).precedence();

	}

}
