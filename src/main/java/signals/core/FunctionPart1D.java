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
public class FunctionPart1D {

	//generating functions for each term
	ArrayList<FunctionTerm1D> termsList; 

	//rule that combines terms in a linear combination
	CombineTermsRule combineTermsRule; 

	public FunctionPart1D( ArrayList<FunctionTerm1D> termsList, CombineTermsRule combineTermsRule ) {
		this.termsList = termsList; 
		this.combineTermsRule = combineTermsRule;
	}

	public double[] create( double[] indices ) {

		int numTerms = termsList.size();

		if( numTerms == 0 ) {

			return Zeros.zeros(indices.length); 
		}

		double[][] termData = new double[numTerms][];
		int idx = 0; 
		for( FunctionTerm1D term : termsList ) {

			termData[idx++] = term.create( indices );
		}

		//create linear combination of terms
		return combineTermsRule.combine(termData, indices.length);
	}

	public double[] create( int dimension, boolean zeroCentered ) {

		//		create each term and store in a 2D array
		int numTerms = termsList.size();

		if( numTerms == 0 ) {

			return Zeros.zeros(dimension); 
		}

		double[][] termData = new double[numTerms][];
		int idx = 0; 
		for( FunctionTerm1D term : termsList ) {

			termData[idx++] = term.create( dimension, zeroCentered );
		}

		//create linear combination of terms
		return combineTermsRule.combine(termData, dimension );
	}

	/**
	 * @return the termsList
	 */
	public ArrayList<FunctionTerm1D> getTermsList() {
		return termsList;
	}

	/**
	 * @return the combineTermsRule
	 */
	public CombineTermsRule getCombineTermsRule() {
		return combineTermsRule;
	}

	public void setWidthScale( double scale ) {

		for( FunctionTerm1D term : termsList ) {
			term.setWidthScale( scale );
		}
	}

	public String getEquation() {

		String[] variables = { "x" }; //independent variable list

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
