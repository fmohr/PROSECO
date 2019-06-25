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
	Deque<Node> operatorStack;
	Queue<Node> postfixQueue;
	Deque<Node> evaluationStack;

	public ExpressionEvaluator(String expression) {
		this.operatorStack = new ArrayDeque<>();
		this.postfixQueue = new LinkedList<>();
		this.convertToPostfix(expression);
	}

	/**
	 * Evaluates the postfix expression
	 *
	 * @return
	 */
	public boolean evaluateExpression() {
		this.evaluationStack = new ArrayDeque<>();
		for (Node n : this.postfixQueue) {
			if (n instanceof Operand) {
				this.evaluationStack.push(n);
			} else if (n instanceof Operator && n.getValue().equals(OperatorEnum.NOT.value())) {
				Node node = this.evaluationStack.pop();
				boolean evaluation = !Boolean.valueOf(node.getValue());
				Node result = new Operand(String.valueOf(evaluation));
				this.evaluationStack.push(result);
			} else if (n instanceof Operator) {
				Node nodeLast = this.evaluationStack.pop();
				Node nodeFirst = this.evaluationStack.pop();
				Node result = this.operate(nodeFirst, nodeLast, n);
				this.evaluationStack.push(result);
			}
		}
		Node node = this.evaluationStack.pop();
		return Boolean.valueOf(node.getValue());
	}

	/**
	 * Executes the actual operation and returns the result
	 *
	 * @param nodeFirst first operand
	 * @param nodeLast last operand
	 * @param operation operation to be applied to the operands
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
			evaluation = this.operateNumeric(nodeFirst, nodeLast, operation);
		}
		return new Operand(String.valueOf(evaluation));
	}

	private boolean operateNumeric(Node nodeFirst, Node nodeLast, Node operation) {
		boolean evaluation = false;
		NumericOperand op1 = this.getNumericOperand(nodeFirst);
		NumericOperand op2 = this.getNumericOperand(nodeLast);
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
		int cursor = 0;
		while (cursor < expression.length()) {
			StringBuilder str = new StringBuilder();
			boolean isOperand = false;
			while (cursor < expression.length() && !expression.substring(cursor, cursor + 1).equals(OperatorEnum.AND.value()) && !expression.substring(cursor, cursor + 1).equals(OperatorEnum.EQUAL.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.OR.value()) && !expression.substring(cursor, cursor + 1).equals(OperatorEnum.NOT.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.LEFT_P.value()) && !expression.substring(cursor, cursor + 1).equals(OperatorEnum.RIGHT_P.value())
					&& !expression.substring(cursor, cursor + 1).equals(OperatorEnum.GREATER.value()) && !expression.substring(cursor, cursor + 1).equals(OperatorEnum.LESS.value())) {
				str.append(expression.substring(cursor, cursor + 1));
				cursor++;
				isOperand = true;
			}
			if (isOperand) {
				this.handleOperand(str);
			} else {
				cursor = this.handleOperator(expression, cursor);
			}
		}

		while (!this.operatorStack.isEmpty()) {
			this.postfixQueue.add(this.operatorStack.pop());
		}
	}

	private int handleOperator(String expression, int cursor) {
		String opString = expression.substring(cursor, cursor + 1);
		if ((opString.equals(OperatorEnum.GREATER.value()) || opString.equals(OperatorEnum.LESS.value())) && cursor < expression.length()) {
			String operator = expression.substring(cursor, cursor + 2);
			if (operator.equals(OperatorEnum.GREATER_EQUAL.value()) || operator.equals(OperatorEnum.LESS_EQUAL.value())) {
				opString = operator;
				cursor++;
			}
		}
		this.handleParanthesis(opString);

		cursor++;
		return cursor;
	}

	private void handleParanthesis(String opString) {
		if (!opString.equals(OperatorEnum.LEFT_P.value()) && !opString.equals(OperatorEnum.RIGHT_P.value())) {
			while (!this.operatorStack.isEmpty() && !this.operatorStack.peek().getValue().equals(OperatorEnum.LEFT_P.value()) && !this.operatorStack.peek().getValue().equals(OperatorEnum.RIGHT_P.value())
					&& this.isHigerPrec(opString, this.operatorStack.peek().getValue())) {
				this.postfixQueue.add(this.operatorStack.pop());
			}
			Node operator = new Operator(opString);
			this.operatorStack.push(operator);
		} else if (opString.equals(OperatorEnum.LEFT_P.value())) {
			Node operator = new Operator(opString);
			this.operatorStack.push(operator);
		} else if (opString.equals(OperatorEnum.RIGHT_P.value())) {
			while (!this.operatorStack.isEmpty() && !this.operatorStack.peek().getValue().equals(OperatorEnum.LEFT_P.value())) {
				this.postfixQueue.add(this.operatorStack.pop());
			}
			if (!this.operatorStack.isEmpty()) {
				this.operatorStack.pop();
			}

		}
	}

	private void handleOperand(StringBuilder str) {
		String value = str.toString().trim();
		if (!value.isEmpty()) {
			Node operand = null;
			if (this.isNumericOperand(value)) {
				operand = new NumericOperand(value);
			} else {
				operand = new Operand(value);
			}
			this.postfixQueue.add(operand);
		}
	}

	private boolean isNumericOperand(String value) {
		return StringUtils.isNumeric(value);
	}

	private boolean isHigerPrec(String op, String sub) {
		return OperatorEnum.findByValue(sub).precedence() >= OperatorEnum.findByValue(op).precedence();

	}

}
