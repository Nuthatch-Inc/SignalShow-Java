package signals.gui.plot;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import signals.operation.ArrayUtilities;

/**
 * Creates and stores indices
 * @author Juliet
 *
 */
public class Indices {
	
	@SuppressWarnings("unchecked")
	protected HashMap<String, SoftReference> indices1D; 
	@SuppressWarnings("unchecked")
	protected HashMap<String, SoftReference> angleIndices; 
	@SuppressWarnings("unchecked")
	protected HashMap<String, SoftReference> sliceRadius; 

	// Protected constructor is sufficient to suppress unauthorized calls to the constructor
	@SuppressWarnings("unchecked")
	protected Indices() {
		
		indices1D = new HashMap<String, SoftReference>(); 
		angleIndices = new HashMap<String, SoftReference>(); 
		sliceRadius = new HashMap<String, SoftReference>(); 
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.instance , not before.
	 */
	private static class SingletonHolder { 
		private final static Indices INSTANCE = new Indices();
	}

	public static Indices getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public static double[] indices1D( int dimension, boolean zeroCentered ) {
		
		return (double[]) getInstance().getIndices1D(dimension, zeroCentered).get();
	}
	
	public static double[] angleIndices( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		return (double[]) getInstance().getAngleIndices(x_dimension, y_dimension, zeroCentered).get();
	}
	
	public static double[] sliceRadius( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		return (double[]) getInstance().getSliceRadius(x_dimension, y_dimension, zeroCentered).get();
	}
	
	@SuppressWarnings("unchecked")
	private SoftReference getIndices1D( int dimension, boolean zeroCentered ) {
		
		String key = dimension + " " + zeroCentered; 
		SoftReference indices = null; 
		if( !indices1D.containsKey(key) || indices1D.get(key).get() == null ) {
			
			indices = new SoftReference( createIndices1D( dimension, zeroCentered ) );
			indices1D.put( key, indices );
			
		} else indices = indices1D.get(key); 
		
		return indices; 
	}
	
	@SuppressWarnings("unchecked")
	private SoftReference getAngleIndices( int x_dimension, int y_dimension, boolean zeroCentered  ) {
		
		String key = x_dimension + " " + y_dimension + " " + zeroCentered; 
		SoftReference indices = null; 
		if( !angleIndices.containsKey(key) || angleIndices.get(key).get() == null  ) {
			
			indices = new SoftReference( zeroCentered ? createAngleIndicesZeroCentered(x_dimension, y_dimension) : 
				createAngleIndicesZeroStart(x_dimension, y_dimension) ); 
			angleIndices.put( key, indices );
			
		} else indices = angleIndices.get(key); 
		
		return indices; 
	}
	
	@SuppressWarnings("unchecked")
	private SoftReference getSliceRadius( int x_dimension, int y_dimension, boolean zeroCentered  ) {
		
		String key = x_dimension + " " + y_dimension + " " + zeroCentered; 
		SoftReference indices = null; 
		if( !sliceRadius.containsKey(key) || sliceRadius.get(key).get() == null  ) {
			
			indices = new SoftReference( createSliceRadius( x_dimension, y_dimension, zeroCentered ) ); 
			sliceRadius.put( key, indices );
			
		} else indices = sliceRadius.get(key); 
		
		return indices; 
	}
	
	private double[] createIndices1D( int dimension, boolean zeroCentered ) {
		
		int firstIndex = zeroCentered ? (-dimension/2) : 0; 

		double[] indices = new double[dimension]; 

		for( int i = 0; i < dimension;  ++i ) {

			indices[i] = firstIndex++;

		}
		
		return indices; 
		
	}
	
	/**
	 * 
	 * @param x_dimension
	 * @param y_dimension
	 * @return
	 */
	private static double[] createAngleIndicesZeroStart( int x_dimension, int y_dimension ) {
	
		double[][] angle = new double[x_dimension][y_dimension]; 
	
		double scale = 180.0 / ( Math.PI );
	
		for( int y = 1; y < y_dimension; ++y ) {
	
			for( int x = 1; x < x_dimension; x++ ) {
	
				double theta_I = Math.atan( (double)y / (double)x );
				angle[x][y_dimension-y] = theta_I * scale;
	
			}
	
		}
	
		//vertical
		for( int y = 0; y < y_dimension; y++ ) {
			angle[0][y] = 90;
		}
	
	
		//horizontal
		for( int x = 0; x < x_dimension; x++ ) { 
			angle[x][0] = 0;
		}
	
		return ArrayUtilities.flattenTranspose(angle);
	}

	/**
	 * calculates the angle given x and y indices
	 * @param xIndices
	 * @param yIndices
	 * @return
	 */
	private static double[] createAngleIndicesZeroCentered( int x_dimension, int y_dimension ) {
	
		double[][] angle = new double[x_dimension][y_dimension]; 
	
		int center_x = x_dimension / 2; 
		int center_y = y_dimension / 2 - 1;
	
		int max_dimension = Math.max( x_dimension, y_dimension ) / 2;
		
		double scale = 180.0 / ( Math.PI );
		
		for( int y = 1; y <= max_dimension; y++ ) {
	
			for( int x = 1; x <= max_dimension; x++ ) {
	
				//first quadrant
				double theta_I = Math.atan( (double)y / (double)x );
				if( (center_x+x) < x_dimension && (center_y-y) >= 0)
				angle[center_x+x][center_y-y] = theta_I * scale;
	
				//second quadrant
				if( (center_x-y) > -1 && (center_y-x) > -1) {
					double theta_II = theta_I + Math.PI/2.;
					angle[center_x-y][center_y-x] = theta_II * scale;
				}
	
				//third quadrant
				if( (center_x+y) < x_dimension && (center_y+x) < y_dimension) {
					double theta_III = theta_I - Math.PI/2.;
					angle[center_x+y][center_y+x] = theta_III * scale;
				}
	
				//fourth quadrant
				if( (center_x-x) >=0 && (center_y+y) < y_dimension ) {
					double theta_IV = theta_I - Math.PI;
					angle[center_x-x][center_y+y] = theta_IV * scale;
				}
	
			}
		}
	
		//vertical 
		for( int y = 0; y < center_y; y++ ) {
	
			angle[center_x][center_y-y-1] = 90;
		}
		
		for( int y = 0; y <= center_y; y++ ) {
		
			angle[center_x][center_y+y+1] = -90;
		}
	
		//horizontal
		for( int x = 0; x < center_x; x++ ) {
			angle[center_x+x][center_y] = 0;
		}
		
		for( int x = 0; x < center_x; x++ ) {
		
			angle[center_x-x-1][center_y] = 180;
		}
		
		angle[center_x][center_y] = 90; //??? what to do with this center pixel???
	
		return ArrayUtilities.flattenTranspose(angle); 

	}
	
	private static double[] createSliceRadius( int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		int max_x = ( zeroCentered ? x_dimension/2 : x_dimension );
		int max_y = ( zeroCentered ? y_dimension/2 : y_dimension );
	
		int max_dimension = Math.max( max_x, max_y ); 
		int dimension = max_dimension * max_dimension / 2 + 2*max_dimension;
	
		double[] r = new double[dimension];
	
		int idx = 0; 
	
		for( int y = 0; y <= max_dimension; ++y ) {
	
			for( int x = 0; x <= y; ++x ) {
	
				r[idx++] = Math.sqrt( y*y + x*x );
			}
		}
	
		return r;
	}

}


