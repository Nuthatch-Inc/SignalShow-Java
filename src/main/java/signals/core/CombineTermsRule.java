package signals.core;

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
 * Token: 3 = -; Input Priority = 1. Stack Priority = 1
 * Token: 4 = *; Input Priority = 2. Stack Priority = 2
 * Token: 5 = (; Input Priority = 3. Stack Priority = 0
 * Token: 6 = ); Input Priority = 0. Stack Priority = 3
 * 
 * @author Juliet
 *
 */
public class CombineTermsRule {

	public static final int END_OF_LIST = 0;
	public static final int FUNCTION_TERM = 1; 
	public static final int ADD = 2; 
	public static final int SUBTRACT = 3; 
	public static final int MULTIPLY = 4; 
	public static final int LEFT_PAREN = 5; 
	public static final int RIGHT_PAREN = 6;

	//lookup tables for input and stack priorities
	public static final int[] input_priority = { -1, 3, 1, 1, 2, 3, 0 };
	public static final int[] stack_priority = { -1, 3, 1, 1, 2, 0, 3 };

	//rule in infix notation
	protected int[] infix_rule; 

	//rule in postfix notation 
	protected int[] postfix_rule;

	protected int postfix_rule_length;

	public static CombineTermsRule getDefaultRule() {

		int[] infix = {FUNCTION_TERM}; 
		return new CombineTermsRule( infix ); 
	}

	public CombineTermsRule( int[] infix ) {

		infix_rule = infix;

		//use Dijkstra's double priority algorithm to convert from
		//infix notation to postfix notation
		//algorithm from http://www.faqts.com/knowledge_base/view.phtml/aid/26070/fid/585

		//length of infix notation expression
		int infix_length = infix.length; 

		//array of signals.operation codes (either + or *) that has length less than or equal to length of infix expression
		postfix_rule = new int[infix_length];

		//stack is used in conversion process
		Stack<Integer> stack = new Stack<Integer>();

		//index of the postfix expression
		int postfixIndex = 0;

		//push a token signifying the end of the list on the stack
		stack.push(END_OF_LIST);

		//for each token in the infix expression
		for( int infixIndex = 0; infixIndex < infix_length; infixIndex++ ) {

			//read next token from the infix expression
			int token = infix[infixIndex];

			//remove all the items on the stack that have a stack priority greater than or equal to the 
			//input priority of this token. If these removed tokens are + or *, add them to the postfix rule.
			while( input_priority[token] <= stack_priority[stack.peek()] ) { 

				int removed_token = stack.pop();
				if( (removed_token != LEFT_PAREN) && (removed_token != RIGHT_PAREN) ) {

					postfix_rule[postfixIndex] = removed_token; 
					postfixIndex++;

				}

			} //endwhile

			stack.push(token);

		}//endfor

		//read the rest of the items off of the stack.  If these removed tokens are not parens, add them to the postfix rule.
		int removed_token = stack.pop();

		while( removed_token != 0 ) { //while there is not more in the stack 

			if( (removed_token != LEFT_PAREN) && (removed_token != RIGHT_PAREN) ) { 

				postfix_rule[postfixIndex] = removed_token; 
				postfixIndex++;
			}
			removed_token = stack.pop();
		}

		//the number of elements in the postfix rule = length of infix rule - number of parentheses 
		postfix_rule_length = postfixIndex;

		
	}

	public String getEquation( String[] termEquations ) {

		StringBuilder equation = new StringBuilder(); 
		int termIndex = 0; 

		//combine all the term equations with the operator symbols
		for( int i = 0; i < infix_rule.length; i++ ) {

			switch( infix_rule[i] ) {

			case FUNCTION_TERM: 
				equation.append(termEquations[termIndex]);
				termIndex++; 
				break; 
			case ADD: 
				equation.append(" + "); 
				break; 
			case MULTIPLY: 
				equation.append( " \u00D71 " ); 
				break; 
			case LEFT_PAREN: 
				equation.append("("); 
				break; 
			case RIGHT_PAREN: 
				equation.append(")"); 
				break; 

			}

		}

		return equation.toString(); 
	}


	/**
	 * Combines images or signals according to this combination rule
	 * Modifies the first in the list
	 */
	public double[] combine( double[][] terms, int dimension ) {

		//special case: nothing to add
		if( postfix_rule_length == 0 ) {

			return Zeros.zeros(dimension);
		}

		//index of the latest term to be used
		int termIndex = 0; 

		//algorithm from Wikipedia

		//	    * While there are input tokens left
		//        o Read the next token from input.
		//        o If the token is a value
		//              + Push it onto the stack.
		//        o Otherwise, the token is a function. (Operators, like +, are simply functions taking two arguments.)
		//              + It is known that the function takes n arguments.
		//              + If there are fewer than n values on the stack
		//                    # (Error) The user has not input sufficient values in the expression.
		//              + Else, Pop the top n values from the stack.
		//              + Evaluate the function, with the values as arguments.
		//              + Push the returned results, if any, back onto the stack.
		//  * If there is only one value in the stack
		//        o That value is the result of the calculation.
		//  * If there are more values in the stack
		//        o (Error) The user input too many values.

		//		stack is used in conversion process
		Stack<double[]> stack = new Stack<double[]>();

		double[] operand1 = null; 
		double[] operand2 = null;

		//for each token in the postfix rule
		for( int idx = 0; idx < postfix_rule_length; idx++ ) {

			//read the next token
			int token = postfix_rule[idx];

			switch( token ) {

			case FUNCTION_TERM:

				stack.push(terms[termIndex]);
				termIndex++;

				break;

			case ADD:

				if( !stack.empty() ) {

					operand1 = stack.pop();
				}

				if( stack.empty() ) { 

					return Zeros.zeros(dimension);

				} else { 

					if( Zeros.isZero(operand1) ) {

						operand1 = new double[dimension]; 

					} 

					operand2 = stack.pop();

					//result = operand + result
					for( int i=0; i<dimension; ++i ) {

						operand1[i] += operand2[i]; 
					}

					stack.push(operand1);


				}

				break; 
				
			case SUBTRACT: //not sure if the order is correct here - needs to be tested
				
				if( !stack.empty() ) {

					operand1 = stack.pop();
				}

				if( stack.empty() ) { 

					return Zeros.zeros(dimension);

				} else { 

					operand2 = stack.pop();
					
					if( Zeros.isZero(operand2) ) {

						operand2 = new double[dimension]; 

					} 

					//result = operand - result
					for( int i=0; i<dimension; ++i ) {

						operand2[i] -= operand1[i]; 
					}

					stack.push(operand2);

				}
				
				break; 

			case MULTIPLY: 

				if( !stack.empty() ) operand1 = stack.pop();
				if( stack.empty() ) { 

					return Zeros.zeros(dimension);

				} else { 

					if( Zeros.isZero(operand1) ) {

						stack.push(operand1); //zero x operand 1 = zero

					} else {

						operand2 = stack.pop();

						//result = operand * result
						for( int i=0; i<dimension; ++i ) {

							operand1[i] *= operand2[i]; 
						}

						stack.push(operand1);
					}

				}

				break; 

			}//end of switch

		}//endfor

		if( stack.empty() ) return Zeros.zeros(dimension);  //empty stack
		double[] result = stack.pop(); 
		if( !stack.empty() ) return Zeros.zeros(dimension); //too many symbols
		return result;

	}

	/**
	 * @return the infix_rule
	 */
	public int[] getInfix() {
		return infix_rule;
	}

}
