package signals.functionterm;

public class Polynomial {

	/**
	* CalcPoly
	* Calculates the y value of a polynomial
	* example: if coefVector = [ 0.5, 3, 1 ], calcPoly returns y = x^2 + 3x + 0.5 for a given x. 
	* @param xIn - value of x
	* @param coefVector - coefficients in the polynomial
	* @param order - order of the polynomial to calculate
	*/
	public static double calcPoly( double xIn, double[] coefVector, int order ) {
		
		double yOut = coefVector[order];
	
		for( int i = order - 1; i >= 0; i-- ) {
			
			yOut = yOut * xIn + coefVector[i]; 
			
		}
		
		return yOut;
	}
	
}
