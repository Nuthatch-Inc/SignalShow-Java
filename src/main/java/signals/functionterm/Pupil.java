package signals.functionterm;

public class Pupil {
	
	/**
	 * Adds a filled circle of given amplitude and radius to the function at position x_location, y_location
	 * @param function
	 * @param x_dimension
	 * @param y_dimension
	 * @param x_location
	 * @param y_location
	 * @param radius
	 * @param amplitude
	 * @return
	 */
	public static double[] addFilledPupil( double[] function,
					int x_dimension, int y_dimension, 
					double x_location, double y_location, 
					double radius, double amplitude ) {
		
		int y_start = Math.min( y_dimension-1, Math.max( 0, (int)Math.ceil( y_location+radius ) ) );
		int y_end = Math.min( y_dimension-1, Math.max( 0, (int)Math.ceil( y_location-radius ) ) );
		
		int x_start = Math.min( x_dimension-1, Math.max( 0, (int)Math.ceil( x_location-radius ) ) ) ;
		int x_end = Math.min( x_dimension-1, Math.max( 0, (int)Math.ceil( x_location+radius ) ) );
		
		double r_squared = radius*radius;
		
		//search in a radius x radius sized box
		for( int y = y_start; y >= y_end; --y ) {
			
			for( int x = x_start; x <= x_end; ++x ) {
			
				if( (y*y + x*x) <= r_squared ) {
					
					int k = y * x_dimension * x;
					function[k] += amplitude;
				}
			}
		}
		return function; 
	}

	
//	function cassegrain,x,y,f1,f2
//	cyl1=(x^2.0 + y^2.0) le f1^2.0
//	cyl2=(x^2.0 + y^2.0) le f2^2.0
//	pupil=cyl1-cyl2
//	return,pupil
//	end
	
	public static double[] addCassegrainPupil( double[] function,
			int x_dimension, int y_dimension,
			double x_location, double y_location, 
			double inner_radius, double outer_radius, double amplitude ) {

			return function; 
	}
}
