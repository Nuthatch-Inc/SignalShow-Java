package signals.gui.plot;

import java.awt.BasicStroke;
import java.awt.Dimension;


@SuppressWarnings("serial")
public class InteractiveImageGraphic extends ImageDisplayPanel {

	protected int dimension;
	
	//source of data
	protected double[] data;
	
	public BasicStroke lineStroke; 

	public InteractiveImageGraphic(Dimension displayDimension) {
		super(displayDimension);
		lineStroke = new BasicStroke( 4 ); 
	}

	public double mathToPlotX(double mathX) {
		
		return mathX / (double)dimension * displayDimension.width;
	}

	public double mathToPlotY(double mathY) {
		
		return displayDimension.height - 1 - mathY /(double)dimension * displayDimension.height;
	}

	public double plotToMathX(double plotX) {
	
		return plotX / (double)displayDimension.width * dimension;
	}

	public double plotToMathY(double plotY) {
	
		return ( displayDimension.height - 1 - plotY ) / (double)displayDimension.height * dimension;
	}

	@Override
	public void setIndices(int x_dimension, int y_dimension, boolean zeroCentered) {
		super.setIndices(x_dimension, y_dimension, zeroCentered);
		dimension = Math.max( x_dimension, y_dimension );
		setDirty();
		repaint();
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData( double[] data ) {
		this.data = data;
		refreshView(); 
		setDirty();
		repaint();
	}

	public void setImage( double[] data, int x_dimension, int y_dimension, boolean zeroCentered ) {
		
		this.data = data; 
		this.x_dimension = x_dimension; 
		this.y_dimension = y_dimension; 
		this.zeroCentered = zeroCentered;
		
		setIndices(x_dimension, y_dimension, zeroCentered);
		dimension = Math.max( x_dimension, y_dimension );
		refreshView(); 
		setDirty();
		repaint();
		
	}
	
	/**
	 * Call this method when the size of this plot should be updated
	 *
	 */
	public void sizeChanged() {

		setDirty();
	}

	public void setDirty() {}
	
	public void refreshView() {
		
		if( data != null ) {
		
			display( data );
		}
	}

}