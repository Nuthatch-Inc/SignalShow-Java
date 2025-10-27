package signals.gui.plot;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * This class implements mathematical static methods useful
 * for making plots 
 * @author Juliet
 *
 */
public class PlottingMath {

	public static final int sizeThreshold = 30;
	public static final double windowNudgeScalar = 0.01; 

	boolean zeroCentered; 
	int width;
	int height; 
	int offset;
	double[] origin;

	double xWindowMin, yWindowMin; 
	double xWindowMax, yWindowMax;
	double xShiftFactor, yShiftFactor; 
	double xScaleFactor, yScaleFactor;

	ArrayList<String> ticksXLabels, ticksYLabels;
	ArrayList<Integer> ticksXLocations, ticksYLocations;

	NumberFormat formatter; 

	/**
	 * @param width
	 * @param height
	 * @param offset
	 */
	public PlottingMath(int width, int height, int offset) {
		super();
		this.width = width - 2*offset;
		this.height = height - 2*offset;
		this.offset = offset;
		formatter = NumberFormat.getNumberInstance(); 
		formatter.setMaximumFractionDigits(2);
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height - 2*offset;
		convertWindow();
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width - 2*offset;
		convertWindow();
	}

	public void increaseXMax() {

		xWindowMax += Math.abs( windowNudgeScalar * xWindowMax );  
		convertWindow(); 
	}

	public void decreaseXMin() {

		xWindowMax -= Math.abs( windowNudgeScalar * xWindowMax );  
		convertWindow(); 
	}

	public void increaseYMax() {

		yWindowMax += Math.abs( windowNudgeScalar * yWindowMax );  
		convertWindow(); 
	}

	public void decreaseYMin() {

		yWindowMax -= Math.abs( windowNudgeScalar * yWindowMax );  
		convertWindow(); 
	}

	public void setWindow( double xMin, double xMax, double yMin, double yMax ) {

		xWindowMin = xMin; 
		xWindowMax = xMax;
		yWindowMin = yMin; 
		yWindowMax = yMax;

		convertWindow(); 
	}

	public void setWindow( double[] xExtrema, double[] yExtrema ) {

		xWindowMin = xExtrema[0]; 
		xWindowMax = xExtrema[1];
		yWindowMin = yExtrema[0]; 
		yWindowMax = yExtrema[1];

		convertWindow();
	}

	public void convertWindow() {

		xScaleFactor = ( width - 1 ) / ( xWindowMax - xWindowMin );
		yScaleFactor = ( height - 1 ) / ( yWindowMax - yWindowMin );

		xShiftFactor = -xWindowMin;
		yShiftFactor = -yWindowMin;

		origin = new double[2]; 
		origin[0] = xShiftFactor*xScaleFactor + offset ;
		origin[1] = ( ( height - 1) - ( yShiftFactor*yScaleFactor ) ) + offset;

	}

	/**
	 * @return the origin
	 */
	public double[] getOrigin() {
		return origin;
	}

	/**
	 * create extrema for indices
	 * @return {first index, last index}
	 */
	public double[] indexExtrema( int dimension ) {

		double extrema[] = new double[2]; 

		extrema[0] = zeroCentered ? (-dimension/2) : 0; 
		extrema[1] = extrema[0] + dimension - 1;

		return extrema; 
	}

	/**
	 * finds the minimum and maximum values in the array
	 * @param array
	 * @return an array with the min at index 0 and the max at index 1
	 */
	public static double[] extrema( double[] array ) {

		double[] input_extrema = {Double.MAX_VALUE, Double.MIN_VALUE}; 
		return extrema( array, input_extrema );
	}

	/**
	 * finds the minimum and maximum values in the array, 
	 * essentially including minVal and maxVal as elements in the array 
	 * @param array
	 * @param extrma an array with the current min at index 0 and the current max at index 1
	 * @return
	 */
	public static double[] extrema ( double[] array, double[] input_extrema ) {

		int dimension = array.length;
		double minVal = input_extrema[0];
		double maxVal = input_extrema[1];

		for( int i = 0; i < dimension;  ++i ) {

			if ( array[i] > maxVal ) 
				maxVal = array[i]; 

			if ( array[i] < minVal ) 
				minVal = array[i]; 

		}

		double extrema[] = {minVal, maxVal};
		return extrema; 

	} 

	/**
	 * Given two sets of extrema, returns the most extreme
	 * @param extrema1 {min1,max1}
	 * @param extrema2 {min2,max2}
	 * @return {min(min1,min2), max(max1,max2)}
	 */
	public double[] compareExtrema( double[] extrema1, double[] extrema2  ) {

		double minVal = Math.min( extrema1[0], extrema2[0] ); 
		double maxVal = Math.max( extrema1[1], extrema2[1] );

		double extrema[] = {minVal, maxVal};
		return extrema; 
	}	 

	/**
	 * if there is no difference between minVal and maxval, 
	 * subtract some amount from minVal and add it to maxVal
	 * @param extrema an array with the current min at index 0 and the current max at index 1
	 * @return
	 */
	public static double[] contrastAdjustExtrema( double[] input_extrema, double adjustment ) {

		double minVal = input_extrema[0];
		double maxVal = input_extrema[1];

		double threshold = adjustment / 100;
		if ( (maxVal - minVal) < threshold ) {
			minVal -= adjustment;
			maxVal += adjustment; 
		}

		if( Math.abs( minVal ) < threshold ) { //if minimum value is zero, 

			minVal -= (adjustment / 10.0);
		}

		double extrema[] = {minVal, maxVal};
		return extrema;
	}

	/**
	 * Converts data to be plotted into coordinates that can be painted on a graph
	 * @param width the width of the graph in pixels
	 * @param height the height of the graph in pixels
	 * @param offset size of the empty border around the graph in pixels 
	 * @param xData data to be plotted along the horizontal axis
	 * @param yData data to be plotted along the vertical axis
	 * @param xExtrema minimum and maximum values to be shown on x axis
	 * @param yExtrema minimum and maximimum values to be shown on y axis
	 * @return the x and y coordiates to be painted on a graph, plus the origin, in plotting coordinates
	 */
	public double[][] mathToPlotCoordinates ( double[] xData, double[] yData ) {

		//the data adjusted to the width and height
		int dimension = xData.length;
		double[] xSequence = new double[dimension];
		double[] ySequence = new double[dimension];

		for( int i = 0; i < dimension; i++ ) {

			xSequence[i] = mathToPlotX( xData[i] );
			ySequence[i] = mathToPlotY( yData[i] );
		}	

		double[][] adjusted = { xSequence, ySequence };
		return adjusted; 
	}

	public double[] mathToPlotCoordinates( double xData, double yData ) {

		double[] adjusted = new double[2];
		adjusted[0] = mathToPlotX( xData );
		adjusted[1] = mathToPlotY( yData ); 
		return adjusted;
	}

	public double[] plotToMathCoordinates( double xPlot, double yPlot ) {

		double[] adjusted = new double[2];
		adjusted[0] =  plotToMathX( xPlot );
		adjusted[1] =  plotToMathY( yPlot );
		return adjusted;
	}

	public double mathToPlotY ( double yWindow ) {
		return ( (height - 1) - ( ( yWindow + yShiftFactor ) * yScaleFactor ) ) + offset; 
	}

	public double mathToPlotX( double xWindow ) {
		return ( xWindow + xShiftFactor ) * xScaleFactor + offset; 
	}

	public double plotToMathY( double yPlot ) {

		return (-yPlot + offset + (height-1)) / yScaleFactor - yShiftFactor;
	}

	public double plotToMathX( double xPlot ) {
		return ( xPlot - offset ) / xScaleFactor - xShiftFactor;
	}

	/**
	 * @return the xWindowMax
	 */
	public double getXWindowMax() {
		return xWindowMax;
	}

	/**
	 * @return the xWindowMin
	 */
	public double getXWindowMin() {
		return xWindowMin;
	}

	/**
	 * @return the yWindowMax
	 */
	public double getYWindowMax() {
		return yWindowMax;
	}

	/**
	 * @return the yWindowMin
	 */
	public double getYWindowMin() {
		return yWindowMin;
	}

	/**
	 * @return the zeroCentered
	 */
	public boolean isZeroCentered() {
		return zeroCentered;
	}

	/**
	 * Finds the locations for the tick marks along the X-axis. Initializes ticksXLocations and ticksXLabels
	 * @param indexExtrema ticks are placed at natural locations given the indices
	 */
	public void createTicksX( double[] indexExtrema ) {

		ticksXLocations = new ArrayList<Integer>();
		ticksXLabels = new ArrayList<String>();

		double max = Math.max( Math.abs( indexExtrema[0] ), Math.abs( indexExtrema[1] ) ); 
		double size = Math.ceil(max/4.0);

		double gap = size * xScaleFactor;
		int maxX = width + 2 * offset;
		double label = size; 

		double markX = origin[0] + gap; 

		//draw tick mark at origin
		ticksXLocations.add( (int)origin[0] ); 
		ticksXLabels.add( "0" ); 

		//draw positive x tick marks
		while( markX <= maxX ) {

			ticksXLocations.add( (int)markX );
			ticksXLabels.add( formatter.format( label ) ); 

			markX += gap;
			label+=size;

		}

		//draw negative x tick marks
		markX = origin[0] - gap; 
		label = -size;
		while( markX >= offset ) {

			ticksXLocations.add( (int)markX ); 
			ticksXLabels.add( formatter.format( label ) ); 

			markX -= gap;
			label -= size;
		}

	}


	/**
	 * Finds the locations for the tick marks along the X-axis. Initializes ticksXLocations and ticksXLabels
	 * @param indexExtrema ticks are placed at natural locations given the indices
	 */
	public void createTicksY( double[] indexExtrema ) {

		ticksYLocations = new ArrayList<Integer>();
		ticksYLabels = new ArrayList<String>();

		double max = Math.max( Math.abs( indexExtrema[0] ), Math.abs( indexExtrema[1] ) ); 
		double size = Math.round(max/4);

		double gap = size * yScaleFactor;
		int maxY = height + 2 * offset; 

		//draw tick mark at origin
		ticksYLocations.add( (int)origin[1] ); 
		ticksYLabels.add( "0" ); 

		//draw negative y tick marks
		double label = -size; 
		double markY = origin[1] + gap;
		while( markY <= maxY ) {

			ticksYLocations.add( (int)markY );
			ticksYLabels.add( formatter.format( label ) ); 

			markY += gap;
			label -=size;

		}

		//draw positive y tick marks
		markY = origin[1] - gap; 
		label = size;
		while( markY >= offset ) {

			ticksYLocations.add( (int)markY ); 
			ticksYLabels.add( formatter.format( label ) ); 

			markY -= gap;
			label += size;
		}

	}

	/**
	 * Finds the locations for the tick marks along the X-axis. Initializes ticksXLocations and ticksXLabels
	 */
	public void createTicksX() {

		ticksXLocations = new ArrayList<Integer>();
		ticksXLabels = new ArrayList<String>();


		//of xWindowMax and yWindowMax, find the one with the larger magnitude
		double max = Math.max( Math.abs( xWindowMax ), 
				Math.abs( xWindowMin ) ); 

		//find the initial tick mark size. This is essentially the order of magnitude
		//of the larger max value.  Only calculate this if one is not specified.
		double power = Math.floor( Math.log10( max ) );
		double size = Math.pow( 10, power );

		//for max of one, go down to half increments
		int increment = (int)Math.round( size / max );
		if( increment == 1 ) { 

			power -= 1;
			size /= 2.0; //divide by 2 to get increments of 5
		}

		//for max greater than seven, go down to 5
		if( increment > 7 ) {

			size = size * 5.0 / increment;
		}


		formatter.setMaximumFractionDigits( power >= 0  ? 0 : (int)Math.abs( power ) );

		double gap = size * xScaleFactor;
		int maxX = width + 2 * offset;
		double label = size; 

		//draw positive x tick marks: may need to go while less than something relating to windowmax.
		double markX = origin[0] + gap; 
		while( markX < maxX ) {

			ticksXLocations.add( (int)markX );
			ticksXLabels.add( formatter.format( label ) ); 

			markX += gap;
			label+=size;

		}

		//draw negative x tick marks
		markX = origin[0] - gap; 
		label = -size;
		while( markX >= offset ) {

			ticksXLocations.add( (int)markX ); 
			ticksXLabels.add( formatter.format( label ) ); 

			markX -= gap;
			label -= size;
		}

	}

	/**
	 * Finds the locations for the tick marks along the Y-axis. Initializes ticksYLocations and ticksYLabels
	 * @param piScaling
	 */
	public void createTicksY( boolean piScaling ) {

		ticksYLocations = new ArrayList<Integer>();
		ticksYLabels = new ArrayList<String>();

		double max = Math.max( Math.abs( yWindowMax ), 
				Math.abs( yWindowMin ) ); 

		if( piScaling ) max /= Math.PI;

		//find the initial tick mark size. This is essentially the order of magnitude
		//of the larger max value. 
		double power = Math.floor( Math.log10( max ) );
		double size = Math.pow( 10, power );
		int increment = (int)Math.round( size / max );
		if( increment == 1 ) { 

			power -= 1;
			size /= 2.0; //divide by 2 to get increments of 5
			
		}

		formatter.setMaximumFractionDigits( power >= 0 ? 0 : (int)Math.abs( power ) );

		if( size < sizeThreshold ) size *= 2;
		double gap = size * yScaleFactor;
		if( piScaling ) gap *= Math.PI;
		int maxY = height + 3 * offset; //may need to change this

		//draw tick mark at origin
		ticksYLocations.add( (int)origin[1] ); 
		ticksYLabels.add( "0" ); 

		//draw negative y tick marks
		double markY = origin[1] + gap; 
		double label = -size;
		
		while( markY <= maxY ) {

			ticksYLocations.add( (int)markY );  

			if( piScaling ) {

				ticksYLabels.add( formatter.format( label ) + '\u03C0' );

			} else {

				ticksYLabels.add( formatter.format( label ) );
			}


			markY += gap;
			label -= size;
		}

		//draw positive y tick marks
		markY = origin[1] - gap; 
		label = size;

		while( markY >= 0 ) {

			ticksYLocations.add( (int)markY ); 
			if( piScaling ) {

				ticksYLabels.add( formatter.format( label ) + '\u03C0' );

			} else {

				ticksYLabels.add( formatter.format( label ) );
			}


			markY -= gap;
			label += size;
		}

	}

	/**
	 * @return the ticksXLabels
	 */
	public ArrayList<String> getTicksXLabels() {
		return ticksXLabels;
	}

	/**
	 * @return the ticksXLocations
	 */
	public ArrayList<Integer> getTicksXLocations() {
		return ticksXLocations;
	}

	/**
	 * @return the ticksYLabels
	 */
	public ArrayList<String> getTicksYLabels() {
		return ticksYLabels;
	}

	/**
	 * @return the ticksYLocations
	 */
	public ArrayList<Integer> getTicksYLocations() {
		return ticksYLocations;
	}

	/**
	 * @param windowMax the xWindowMax to set
	 */
	public void setXWindowMax(double windowMax) {
		xWindowMax = windowMax;
		convertWindow(); 
	}

	/**
	 * @param windowMin the xWindowMin to set
	 */
	public void setXWindowMin(double windowMin) {
		xWindowMin = windowMin;
		convertWindow();
	}

	/**
	 * @param windowMax the yWindowMax to set
	 */
	public void setYWindowMax(double windowMax) {
		yWindowMax = windowMax;
		convertWindow();
	}

	/**
	 * @param windowMin the yWindowMin to set
	 */
	public void setYWindowMin(double windowMin) {
		yWindowMin = windowMin;
		convertWindow();
	}

}
