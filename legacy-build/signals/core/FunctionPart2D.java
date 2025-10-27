/**
 * 
 */
package signals.core;

import java.util.ArrayList;

/**
 * A sum of 1D function terms that represents a real part, imaginary part, 
 * magnitude, or phase component of a 1D Function
 * @author Juliet
 * 
 */
public class FunctionPart2D {

	//	generating functions for each term
	ArrayList<FunctionTerm2D> termsList; 
	
	//rule that combines terms in a linear combination
	CombineTermsRule combineTermsRule; 

	public FunctionPart2D( ArrayList<FunctionTerm2D> termsList, CombineTermsRule combineTermsRule ) {
		this.termsList = termsList; 
		this.combineTermsRule = combineTermsRule;
	}
	
	
	/**
	 * @return the termsList
	 */
	public ArrayList<FunctionTerm2D> getTermsList() {
		return termsList;
	}

	/**
	 * @return the combineTermsRule
	 */
	public CombineTermsRule getCombineTermsRule() {
		return combineTermsRule;
	}
	
	public double[] create( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		//create each term and store in a 2D array
		int numTerms = termsList.size();
		double[][] termData = new double[numTerms][];
		int idx = 0; 
		for( FunctionTerm2D term : termsList ) {
			
			termData[idx++] = term.create( x_dimension, y_dimension, zeroCentered );
		}
		
		//create linear combination of terms
		return combineTermsRule.combine(termData, x_dimension*y_dimension);
	}
	
	public String getEquation() {
		
		String[] variables = { "x", "y" }; //independent variable list
		
		//create strings for all terms with the variables 
		//and store in an array. 
		String[] termStrings = new String[termsList.size()]; 
		
		for( int i = 0; i< termStrings.length; i++ ) {
			
			termStrings[i] = termsList.get(i).getEquation(variables);
		}
		
		//use the combine terms rule to combine
		return combineTermsRule.getEquation(termStrings);
		
	}
	
}

