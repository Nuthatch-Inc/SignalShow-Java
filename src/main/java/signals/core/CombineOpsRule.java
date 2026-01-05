package signals.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Given an infix expression for a linear combination of functions,
 * Computes the order of operations to compute the linear combinations
 * (in postfix notation)
 * 
 * INPUT FORMAT: string of tokens
 * Token: 0 = end of list; Input Priority = -1. Stack Priority = -1
 * Token: 1 = next function term; Input Priority = 3. Stack Priority = 3
 * Token: 2 = +; Input Priority = 1. Stack Priority = 1
 * Token: 3 = *; Input Priority = 2. Stack Priority = 2
 * Token: 4 = (; Input Priority = 3. Stack Priority = 0
 * Token: 5 = ); Input Priority = 0. Stack Priority = 3
 * 
 * @author Juliet
 *
 */
public class CombineOpsRule {

	// binary ops: tier 1 - addition and subtraction
	// binary ops: tier 2 - multiplication and division
	// binary ops: tier 3 - any operations that have a higher priority than
	// multiplication and division

	// unary ops: tier 1 - e.g. exponentials
	// unary ops: tier 2 - e.g. transforms, cosine
	// unary ops: tier 3 - ?

	public static final int END_OF_LIST = 0; // input -1 stack -1
	public static final int FUNCTION = 1; // input 7 stack 7
	public static final int BINARY_OP_TIER_1 = 2; // input 1 stack 1
	public static final int BINARY_OP_TIER_2 = 3; // input 2 stack 2
	public static final int BINARY_OP_TIER_3 = 4; // input 3 stack 3
	public static final int UNARY_OP_TIER_1 = 5; // input 4 stack 4
	public static final int UNARY_OP_TIER_2 = 6; // input 5 stack 5
	public static final int UNARY_OP_TIER_3 = 7; // input 6 stack 6
	public static final int LEFT_PAREN = 8; // input 7 stack 0
	public static final int RIGHT_PAREN = 9; // input 0 stack 7

	// lookup tables for input and stack priorities
	public static final int[] input_priority = { -1, 7, 1, 2, 3, 4, 5, 6, 7, 0 };
	public static final int[] stack_priority = { -1, 7, 1, 2, 3, 4, 5, 6, 0, 7 };

	// public static final int END_OF_LIST = 0;
	// public static final int FUNCTION = 1;
	// public static final int BINARY_OP_TIER_1 = 2;
	// public static final int UNARY_OP_TIER_2 = 3;
	// public static final int LEFT_PAREN = 4;
	// public static final int RIGHT_PAREN = 5;
	//
	// //lookup tables for input and stack priorities
	// public static final int[] input_priority = { -1, 3, 1, 2, 3, 0 };
	// public static final int[] stack_priority = { -1, 3, 1, 2, 0, 3 };

	// rule in infix notation
	protected int[] infix_rule;

	// rule in postfix notation
	protected int[] postfix_rule;

	protected int postfix_rule_length;

	ArrayList<Operation> postfixOps;

	public CombineOpsRule(int[] infix, ArrayList<Operation> infixOps) {

		infix_rule = infix;

		// use Dijkstra's double priority algorithm to convert from
		// infix notation to postfix notation
		// algorithm from
		// http://www.faqts.com/knowledge_base/view.phtml/aid/26070/fid/585

		// length of infix notation expression
		int infix_length = infix.length;

		// array of signals.operation codes (either + or *) that has length less than or
		// equal to length of infix expression
		postfix_rule = new int[infix_length];

		// stack is used in conversion process
		Stack<Integer> stack = new Stack<Integer>();
		Stack<Operation> opStack = new Stack<Operation>();

		// switch operation order from infix to postfix
		postfixOps = new ArrayList<Operation>();
		Iterator<Operation> opsIter = infixOps.iterator();

		// index of the postfix expression
		int postfixIndex = 0;

		// push a token signifying the end of the list on the stack
		stack.push(END_OF_LIST);

		// for each token in the infix expression
		for (int infixIndex = 0; infixIndex < infix_length; infixIndex++) {

			// read next token from the infix expression
			int token = infix[infixIndex];

			// remove all the items on the stack that have a stack priority greater than or
			// equal to the
			// input priority of this token. If these removed tokens are + or *, add them to
			// the postfix rule.
			while (input_priority[token] <= stack_priority[stack.peek()]) {

				int removed_token = stack.pop();
				if ((removed_token != LEFT_PAREN) && (removed_token != RIGHT_PAREN)) {

					postfix_rule[postfixIndex] = removed_token;
					postfixIndex++;

					// if not function, add operation to postfix operations
					if (removed_token != FUNCTION) {

						postfixOps.add(opStack.pop());
					}

				}

			} // endwhile

			stack.push(token);
			if (((token != LEFT_PAREN) && (token != RIGHT_PAREN)) && (token != FUNCTION)) {

				opStack.push(opsIter.next());
			}

		} // endfor

		// read the rest of the items off of the stack. If these removed tokens are not
		// parens, add them to the postfix rule.
		int removed_token = stack.pop();

		while (removed_token != 0) { // while there is not more in the stack

			if ((removed_token != LEFT_PAREN) && (removed_token != RIGHT_PAREN)) {

				postfix_rule[postfixIndex] = removed_token;
				postfixIndex++;

				// if not function, add operation to postfix operations
				if (removed_token != FUNCTION) {

					postfixOps.add(opStack.pop());
				}

			}
			removed_token = stack.pop();
		}

		// the number of elements in the postfix rule = length of infix rule - number of
		// parentheses
		postfix_rule_length = postfixIndex;

	}

	/**
	 * Combines images or signals according to this combination rule
	 * Modifies the first in the list
	 */
	public Function combine(ArrayList<FunctionProducer> terms) {

		if (postfix_rule_length == 0)
			return null;

		// algorithm from wikipedia

		// * While there are input tokens left
		// o Read the next token from input.
		// o If the token is a value
		// + Push it onto the stack.
		// o Otherwise, the token is a function. (Operators, like +, are simply
		// functions taking two arguments.)
		// + It is known that the function takes n arguments.
		// + If there are fewer than n values on the stack
		// # (Error) The user has not input sufficient values in the expression.
		// + Else, Pop the top n values from the stack.
		// + Evaluate the function, with the values as arguments.
		// + Push the returned results, if any, back onto the stack.
		// * If there is only one value in the stack
		// o That value is the result of the calculation.
		// * If there are more values in the stack
		// o (Error) The user input too many values.

		// stack is used in conversion process
		Stack<Function> stack = new Stack<Function>();

		Function operand1 = null;
		Function operand2 = null;

		Iterator<FunctionProducer> termsIter = terms.iterator();
		Iterator<Operation> opsIter = postfixOps.iterator();

		// for each token in the postfix rule
		for (int idx = 0; idx < postfix_rule_length; idx++) {

			// read the next token
			int token = postfix_rule[idx];

			switch (token) {

				case FUNCTION: // function (operand): get the function from the list and push it on the stack

					stack.push(termsIter.next().getFunction());

					break;

				case BINARY_OP_TIER_1:
				case BINARY_OP_TIER_2:
				case BINARY_OP_TIER_3:

					// binary operation: get the next operation and the two operands.
					// Perform the operation and push the result on the stack
					// Note: Stack is LIFO, so we pop in reverse order:
					// - operand2 was pushed second (is on top) → pop first
					// - operand1 was pushed first → pop second
					// Then call create(operand1, operand2) to maintain left-to-right order

					if (!stack.empty())
						operand2 = stack.pop();
					if (stack.empty()) {

						return null;

					} else {

						BinaryOperation operator = (BinaryOperation) opsIter.next();
						operand1 = stack.pop();
						operand1 = operator.create(operand1, operand2);
						stack.push(operand1);
					}
					break;

				case UNARY_OP_TIER_1:
				case UNARY_OP_TIER_2:
				case UNARY_OP_TIER_3:

					if (stack.empty()) {

						return null;

					} else {

						operand1 = stack.pop();
						UnaryOperation operator = (UnaryOperation) opsIter.next();
						operand1 = operator.create(operand1);
						stack.push(operand1);
					}

					break;

			}// end of switch

		} // endfor

		if (stack.empty())
			return null; // empty stack
		Function result = stack.pop();
		if (!stack.empty())
			return null; // too many symbols
		return result;

	}

	/**
	 * @return the infix_rule
	 */
	public int[] getInfix() {
		return infix_rule;
	}

	// TODO: method to get equation

}
