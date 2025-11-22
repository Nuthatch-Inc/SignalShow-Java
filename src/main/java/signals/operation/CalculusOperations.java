package signals.operation;

public class CalculusOperations {

	//computes area (or volume) of a function
	public static double area( double[] input ) {
		
		int dimension = input.length;
		
		double area = 0;
		for( int i = 0; i < dimension; i++ ) {
			
			area += input[i]; 
		}
		
		return area;
		
	}
	
	public static double[] derivative( double[] input ) {
		
		int dimension = input.length; 
		double[] output = new double[dimension]; 
		
		for( int i = dimension - 1; i > 0; i-- ) {
			
			output[i] = input[i] - input[i-1];  
		}
	
		return output;	
		
	} 
	
	public static double[] integral( double[] input ) {
		
		int dimension = input.length; 
		double[] output = ArrayUtilities.clone( input );
		
		for( int i = 1; i < dimension; i++ ) {
			
			output[i] += output[i-1];  
		}
	
		return output;	
		
	} 
	
	/**
	 * Performs an integral in one direction only
	 * @param input
	 * @param xDimension
	 * @param yDimension
	 * @param x : true if the derivative should be done in the x-direction
	 * @return
	 */
	public static double[] integral2D( double[] input, int x_dimension, int y_dimension, boolean x ) {
		
		double[][] expanded = ArrayUtilities.expand(input, x_dimension, y_dimension);
		
		if( x ) {
			
			for( int j = 0; j < y_dimension; ++ j ) { //for each row
				
				for( int i = 1; i < x_dimension; i++ ) {
					
					expanded[j][i] += expanded[j][i-1];   
				}
			}
			
		} else {
			
			for( int i = 0; i < x_dimension; ++ i ) { //for each column
				
				for( int j = y_dimension - 2; j >= 0; j-- ) { //take derivative in y-direction
					
					expanded[j][i] += expanded[j+1][i]; 
				}
			}
			
		}
		
		return ArrayUtilities.flatten( expanded ); 
		
	}
	
	/**
	 * Performs a derivative in one direction only
	 * @param input
	 * @param xDimension
	 * @param yDimension
	 * @param x : true if the derivative should be done in the x-direction
	 * @return
	 */
	public static double[] derivative2D( double[] input, int x_dimension, int y_dimension, boolean x ) {
		
		double[][] expanded = ArrayUtilities.expand(input, x_dimension, y_dimension);
		double[][] output = new double[y_dimension][x_dimension]; 
		
		if( x ) {
			
			for( int j = 0; j < y_dimension; ++ j ) { //for each row
				
				for( int i = x_dimension - 1; i > 0; i-- ) { //take derivative in x-direction
					
					output[j][i] = expanded[j][i] - expanded[j][i-1];  
				}
			}
			
		} else {
			
			for( int i = 0; i < x_dimension; ++ i ) { //for each column
				
				for( int j = 0; j < (y_dimension-1); j++ ) { //take derivative in y-direction
					
					output[j][i] = expanded[j][i] - expanded[j+1][i]; 
				}
			}
			
		}
		
		return ArrayUtilities.flatten( output ); 
		
	}
}
