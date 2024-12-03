package signals.core;

import signals.operation.ArrayUtilities;

public class PolarFunctionMath {

	public static double[] reflect( double[] eigth, int x_dimension, int y_dimension, boolean zeroCentered ) {
	
		return( zeroCentered ? PolarFunctionMath.refelectZeroCentered( eigth, x_dimension, y_dimension ) : 
			PolarFunctionMath.reflectZeroStart( eigth, x_dimension, y_dimension ) );
	}

	public static double[] refelectZeroCentered( double[] eigth, int x_dimension, int y_dimension ) {
	
		int center_x = x_dimension/2; 
		int center_y = y_dimension/2;
	
		int max_dimension = Math.max( center_x, center_y ); 
		
		center_y -= 1;
	
		double[][] data = new double[x_dimension][y_dimension];
	
		int idx = 0; 
		for( int y = 0; y <= max_dimension; ++y ) {
	
			for( int x = 0; x <= y; ++x ) {
	
				double value = eigth[idx++];
	
				//lower portion of first quadrant
				if( (center_x+x) < x_dimension && (center_y-y) >= 0 )
					data[center_x+x][center_y-y] = value;
	
				//upper portion of first quadrant
				if( (center_x+y) < x_dimension && (center_y-x) >= 0 )
					data[center_x+y][center_y-x] = value; 
	
	
				//upper portion of second quadrant
				if( (center_x-y) >= 0 && (center_y-x) >= 0 )
					data[center_x-y][center_y-x] = value; 
	
				//lower portion of second quadrant
				if( (center_x-x) >= 0 && (center_y-y) >= 0 )
					data[center_x-x][center_y-y] = value;	
	
	
				//lower portion of third quadrant
				if( (center_x-y) >= 0 && (center_y+x) < y_dimension )
					data[center_x-y][center_y+x] = value; 
	
				//upper portion of third quadrant
				if( (center_x-x) >= 0 && (center_y+y) < y_dimension )
					data[center_x-x][center_y+y] = value;			
	
	
				//lower portion of fourth quadrant
				if( (center_x+y) < x_dimension && (center_y+x) < y_dimension )
					data[center_x+y][center_y+x] = value; 
	
				//upper portion of fourth quadrant
				if( (center_x+x) < x_dimension && (center_y+y) < y_dimension )
					data[center_x+x][center_y+y] = value;
	
			}
		}
	
		return ArrayUtilities.flattenTranspose(data); 
	}

	public static double[] reflectZeroStart( double[] half, int x_dimension, int y_dimension ) {
	
		int dimension = Math.max( x_dimension, y_dimension ); 
	
		double[][] data = new double[x_dimension][y_dimension];
	
		int idx = 0; 
	
		for( int y = 0; y < dimension; ++y ) {
	
			for( int x = 0; x <= y; ++x ) {
	
				double value = half[idx++];
	
				//upper portion of first quadrant
				if( x < x_dimension && (y_dimension-1-y) >= 0 )
					data[x][y_dimension-1-y] = value;
	
				//upper portion of first quadrant
				if( y < x_dimension && (y_dimension-1-x) >= 0 )
					data[y][y_dimension-1-x] = value; 
			}
		}
	
		return ArrayUtilities.flattenTranspose(data);
	}

	/**
	 * Given x and y indices, calculates the radius
	 * @param xIndices
	 * @param yIndices
	 * @return
	 */
	public static double[] radius( double[] xIndices, double[] yIndices ) {
	
		int dimension = xIndices.length; 
		double[] radius = new double[dimension]; 
	
		for( int i = 0; i < dimension; ++i ) {
	
			radius[i] = Math.sqrt( xIndices[i]*xIndices[i] + yIndices[i]*yIndices[i] );
		}
	
		return radius; 
	}

}
