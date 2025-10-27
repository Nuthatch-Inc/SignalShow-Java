package signals.functionterm;

public class Apertures {

	public static void setFilledAperture( double[][] array, int x_dimension, int y_dimension, boolean zeroCentered, 
			double xCenter, double yCenter, double radius, double amplitude) {

		int offsetX = zeroCentered ? x_dimension / 2 : 0; 
		int offsetY = zeroCentered ? y_dimension / 2 : 0; 

		int xStart = Math.max( 0, (int)(xCenter - radius - 1 + offsetX )); 
		int yStart = Math.max( 0, (int)(yCenter - radius - 1 + offsetY )); 

		int xEnd = Math.min( x_dimension - 1, (int)(xCenter + radius + 1 + offsetX )); 
		int yEnd = Math.min( y_dimension - 1, (int)(yCenter + radius + 1 + offsetY ));

		for( int y = yStart; y <= yEnd; y++ ) {

			for( int x = xStart; x <= xEnd; x++ ) {

				double x_shift = x - xCenter - offsetX; 
				double y_shift = y - yCenter - offsetY; 

				double r = Math.sqrt(x_shift*x_shift + y_shift*y_shift); 
				if( r <= radius ) array[y_dimension-1-y][x] = amplitude; 
			}

		}
		

	}

	public static void setCassegrainAperture( double[][] array, int x_dimension, int y_dimension, boolean zeroCentered, 
			double xCenter, double yCenter, double radius, double innerRadius, double amplitude) {

		int offsetX = zeroCentered ? x_dimension / 2 : 0; 
		int offsetY = zeroCentered ? y_dimension / 2 : 0; 

		int xStart = Math.max( 0, (int)(xCenter - radius - 1 + offsetX )); 
		int yStart = Math.max( 0, (int)(yCenter - radius - 1 + offsetY )); 

		int xEnd = Math.min( x_dimension - 1, (int)(xCenter + radius + 1 + offsetX )); 
		int yEnd = Math.min( y_dimension - 1, (int)(yCenter + radius + 1 + offsetY ));

		for( int y = yStart; y <= yEnd; y++ ) {

			for( int x = xStart; x <= xEnd; x++ ) {

				double x_shift = x - xCenter - offsetX; 
				double y_shift = y - yCenter - offsetY; 

				double r = Math.sqrt(x_shift*x_shift + y_shift*y_shift); 
				if( (r >= innerRadius) && (r <= radius) ) array[y_dimension-1-y][x] = amplitude;
			}

		}
	}
	
	public static void setRectangularAperture( double[][] array, int x_dimension, int y_dimension, boolean zeroCentered, 
			double xCenter, double yCenter, double width, double height, double amplitude) {

		int offsetX = zeroCentered ? x_dimension / 2 : 0; 
		int offsetY = zeroCentered ? y_dimension / 2 : 0; 

		int xStart = Math.max( 0, (int)(xCenter - width - 1 + offsetX )); 
		int yStart = Math.max( 0, (int)(yCenter - height - 1 + offsetY )); 

		int xEnd = Math.min( x_dimension - 1, (int)(xCenter + width + 1 + offsetX )); 
		int yEnd = Math.min( y_dimension - 1, (int)(yCenter + height + 1 + offsetY ));

		for( int y = yStart; y <= yEnd; y++ ) {

			for( int x = xStart; x <= xEnd; x++ ) {

				array[y_dimension-1-y][x] = amplitude; 
			}

		}
		

	}
	
	
	
//	public static double[] passFilledAperture( double[] array, int x_dimension, int y_dimension, boolean zeroCentered, 
//			double xCenter, double yCenter, double radius, double amplitude) {
//
//		int offsetX = zeroCentered ? x_dimension / 2 : 0; 
//		int offsetY = zeroCentered ? y_dimension / 2 : 0; 
//
//		int xStart = Math.max( 0, (int)(xCenter - radius - 1 + offsetX )); 
//		int yStart = Math.max( 0, (int)(yCenter - radius - 1 + offsetY )); 
//
//		int xEnd = Math.min( x_dimension - 1, (int)(xCenter + radius + 1 + offsetX )); 
//		int yEnd = Math.min( y_dimension - 1, (int)(yCenter + radius + 1 + offsetY ));
//
//		for( int y = yStart; y <= yEnd; y++ ) {
//
//			for( int x = xStart; x <= xEnd; x++ ) {
//
//				double x_shift = x - xCenter - offsetX; 
//				double y_shift = y - yCenter - offsetY; 
//
//				double r = Math.sqrt(x_shift*x_shift + y_shift*y_shift); 
//				if( r <= radius ) array[y_dimension-1-y][x] = amplitude; 
//			}
//
//		}
//	}
//
//	public static double[] passCassegrainAperture( double[] array, int x_dimension, int y_dimension, boolean zeroCentered, 
//			double xCenter, double yCenter, double radius, double innerRadius, double amplitude) {
//
//		int offsetX = zeroCentered ? x_dimension / 2 : 0; 
//		int offsetY = zeroCentered ? y_dimension / 2 : 0; 
//
//		int xStart = Math.max( 0, (int)(xCenter - radius - 1 + offsetX )); 
//		int yStart = Math.max( 0, (int)(yCenter - radius - 1 + offsetY )); 
//
//		int xEnd = Math.min( x_dimension - 1, (int)(xCenter + radius + 1 + offsetX )); 
//		int yEnd = Math.min( y_dimension - 1, (int)(yCenter + radius + 1 + offsetY ));
//
//		for( int y = yStart; y <= yEnd; y++ ) {
//
//			for( int x = xStart; x <= xEnd; x++ ) {
//
//				double x_shift = x - xCenter - offsetX; 
//				double y_shift = y - yCenter - offsetY; 
//
//				double r = Math.sqrt(x_shift*x_shift + y_shift*y_shift); 
//				if( (r >= innerRadius) && (r <= radius) ) array[y_dimension-1-y][x] = amplitude;
//			}
//
//		}
//	}

}
