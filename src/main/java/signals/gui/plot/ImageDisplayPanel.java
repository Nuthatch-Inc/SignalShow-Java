package signals.gui.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.RenderedImage;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import signals.core.Core;
import signals.core.Deconstructable;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;

import com.sun.media.jai.widget.DisplayJAI;

@SuppressWarnings("serial")
public class ImageDisplayPanel extends JPanel implements ParameterUser, Deconstructable {

	//Panel to display the image
	protected DisplayJAI imagePanel;

	protected int x_dimension, y_dimension; 
	protected boolean zeroCentered;

	protected PlottingMath plottingMath;
	protected ImageDisplayMath imageMath;

	protected Dimension displayDimension;

	protected GrayscaleKey key;

	protected NumberFormat formatter; 

	protected ParameterEditor maxEditor, minEditor; 

	protected Axis yAxis, xAxis;

	public static int AXIS_BAR_WIDTH = 60;
	public static int AXIS_BAR_HEIGHT = 30;
	public static int X_AXIS_LOCATION = 10;
	public static int Y_AXIS_LOCATION = 10;

	public static final int MAX_SPINNER_INDEX = 0; 
	public static final int MIN_SPINNER_INDEX = 1; 

	protected JPanel imageAxesPanel; 

	boolean hasAxes; 
	
	RenderedImage image; 

	public ImageDisplayPanel( Dimension displayDimension ) {

		imagePanel = new DisplayJAI(); 
		this.displayDimension = displayDimension;
		formatter = Core.getDisplayOptions().getFormat();
		imageMath = new ImageDisplayMath();
		plottingMath = new PlottingMath( displayDimension.width, displayDimension.height, 0 );
		hasAxes = displayDimension.height > 128;  
		key = new GrayscaleKey( displayDimension.height - 60 );

		createImageAxesPanel();

		yAxis = new Axis( true );
		xAxis = new Axis( false );

		//max and min parameter editors
		SpinnerNumberModel maxSpinner = new SpinnerNumberModel( 1, -10000, 10000, 0.1 ); 
		SpinnerNumberModel minSpinner = new SpinnerNumberModel( 0, -10000, 10000, 0.1 ); 

		maxEditor = new ParameterEditor( 3, maxSpinner, MAX_SPINNER_INDEX, this ); 
		minEditor = new ParameterEditor( 3, minSpinner, MIN_SPINNER_INDEX, this ); 

		add( imageAxesPanel );
		
		if( hasAxes ) {
		
			imageAxesPanel.setPreferredSize( new Dimension( displayDimension.width+AXIS_BAR_WIDTH, 
					displayDimension.height+AXIS_BAR_HEIGHT));
			
			JPanel keyPanel = new JPanel(new BorderLayout() );
			keyPanel.add( key, BorderLayout.CENTER); 
			keyPanel.add( maxEditor, BorderLayout.NORTH ); 
			keyPanel.add( minEditor, BorderLayout.SOUTH ); 
			keyPanel.setBorder(Core.getBorders().getFrame());  
			add( keyPanel );
		
		} else {
			
			imageAxesPanel.setPreferredSize( new Dimension( displayDimension.width, displayDimension.height));
		}

		setOpaque( true );
	}
	
	public void deconstruct() {
		
//		maxEditor.deconstruct(); 
//		minEditor.deconstruct(); 
	}
	
	public void setAdjustExtrema(boolean adjustExtrema) {
		
		imageMath.setAdjustExtrema(adjustExtrema); 
	}

	public void createImageAxesPanel() {

		imageAxesPanel = new ImageAxesPanel(); 
	}

	public void refreshView() {}; 

	/**
	 * display grayscale data
	 * @param data
	 */
	public void display( double[] data ) {

		//if shrinking, use bilinear interpolation. otherwise, use nearest neighbor 
		Interpolation interpolation  = (x_dimension > displayDimension.width ) ? 
				new InterpolationBilinear() : new InterpolationNearest(); 

		image = imageMath.getScaledImage(
						data, x_dimension, y_dimension, displayDimension, zeroCentered, interpolation);
		display(); 
	}

	public void display( double[] band1, double[] band2, double[] band3 ) {

		//if shrinking, use bilinear interpolation. otherwise, use nearest neighbor 
		Interpolation interpolation  = (x_dimension > displayDimension.width ) ? 
				new InterpolationBilinear() : new InterpolationNearest(); 
		image = imageMath.getScaledImage( band1, band2, band3,
						x_dimension, y_dimension, displayDimension, zeroCentered, interpolation, zeroCentered);
		display(); 

	}
	
	public void display() {
		
		imagePanel.set(image); 
		imageAxesPanel.repaint();
		key.setScale(imageMath.getMinValue(), imageMath.getMaxValue());
		maxEditor.setValue(imageMath.getMaxValue()); 
		minEditor.setValue(imageMath.getMinValue()); 
	}

	public void setPiScaling( boolean piScaling ) {

		key.setPiScaling(piScaling);
	}

	public void parameterChanged(int index, String newValue) {

		double newExtremum = Double.parseDouble(newValue); 
		boolean valid = false; 

		switch( index ) { 

		case MAX_SPINNER_INDEX:

			if( newExtremum > imageMath.getMinValue() )  {
				imageMath.setMaxValue(newExtremum); 
				valid = true; 
			}

			break; 

		case MIN_SPINNER_INDEX: 

			if( newExtremum < imageMath.getMaxValue() ) {
				imageMath.setMinValue(newExtremum);
				valid = true; 
			}

			break; 

		}

		if( valid ) { 
			imagePanel.set( imageMath.getScaledImage()); 
			imageAxesPanel.repaint();
			key.setScale(imageMath.getMinValue(), imageMath.getMaxValue());
		}

	}

	/**
	 * 
	 * @param x_dimension
	 * @param y_dimension
	 * @param zeroCentered
	 */
	public void setIndices( int x_dimension, int y_dimension, boolean zeroCentered ) {

		boolean refreshView = x_dimension != this.x_dimension || y_dimension != this.y_dimension || zeroCentered != this.zeroCentered; 

		this.x_dimension = x_dimension; 
		this.y_dimension = y_dimension; 
		this.zeroCentered = zeroCentered;

		if( refreshView ) {

			refreshView();
		}

		setupAxes(); 

		imageAxesPanel.repaint(); 
		key.repaint();
	}

	public void setupAxes() { 

		if( hasAxes ) {

			//both axes have the same scale: the scale of the larger of the dimensions
			int dimension = Math.max( x_dimension, y_dimension );

			double x1 = zeroCentered ? -dimension / 2 : 0; 
			double x2 = zeroCentered ? dimension / 2 - 1 : dimension -1;

			double[] extrema = { x1, x2 }; 

			plottingMath.setWindow(extrema, extrema);

			plottingMath.createTicksX( extrema );
			plottingMath.createTicksY( extrema );

			ArrayList<Integer> ticksXLocations = plottingMath.getTicksXLocations(); 
			ArrayList<Integer> ticksYLocations = plottingMath.getTicksYLocations(); 

			xAxis.setTicksLocations(ticksXLocations);
			yAxis.setTicksLocations(ticksYLocations);

			xAxis.setTickMarks(PlottingGraphics.createTicksX( ticksXLocations, displayDimension.height+X_AXIS_LOCATION ) );
			yAxis.setTickMarks(PlottingGraphics.createTicksY( ticksYLocations, displayDimension.width+Y_AXIS_LOCATION ) );	

			xAxis.setTicksLabels(plottingMath.getTicksXLabels()); 
			yAxis.setTicksLabels(plottingMath.getTicksYLabels()); 

			xAxis.setAxis( PlottingGraphics.xAxis(displayDimension.width, displayDimension.height+X_AXIS_LOCATION, 0));
			yAxis.setAxis( PlottingGraphics.yAxis(displayDimension.height, displayDimension.width+Y_AXIS_LOCATION, 0));

		}
	}


	public class ImageAxesPanel extends JPanel {

		public void paintComponent( Graphics g ) {

			super.paintComponent(g);

			//paint the image
			imagePanel.paintComponent(g);

			if( hasAxes ) {

				//paint the x axis
				xAxis.paint( g ); 

				//paint the y axis
				yAxis.paint( g );
			}

		}
	}

	public class Axis {

		Line2D.Double axis; 
		GeneralPath tickMarks;
		ArrayList<String> ticksLabels;
		ArrayList<Integer> ticksLocations;
		boolean vertical; 

		public Axis( boolean vertical ) {

			this.vertical = vertical;
		}

		public void paint( Graphics g ) {

			if( x_dimension == 0 ) return; //do nothing if dimensions have not yet been initialized

			Graphics2D g2 = (Graphics2D)g;

			g2.setFont( Core.getDisplayOptions().getAxisFont() );
			g2.setColor( Color.black );
			g2.draw( axis ); 
			g2.draw( tickMarks );

			if( vertical ) {

				int offset = displayDimension.width + Y_AXIS_LOCATION + 10; 

				for( int i = 0; i < ticksLabels.size(); i++ ) { 

					g2.drawString(ticksLabels.get( i ), offset, ticksLocations.get( i ));

				}

			} else {

				for( int i = 0; i < ticksLabels.size(); i++ ) { 

					int offset = displayDimension.height + X_AXIS_LOCATION + 20; 

					g2.drawString( ticksLabels.get( i ), ticksLocations.get( i ), offset );

				}
			}
		}

		/**
		 * @param axis the axis to set
		 */
		public void setAxis(Line2D.Double axis) {
			this.axis = axis;
		}

		/**
		 * @param tickMarks the tickMarks to set
		 */
		public void setTickMarks(GeneralPath tickMarks) {
			this.tickMarks = tickMarks;
		}

		/**
		 * @param ticksLabels the ticksLabels to set
		 */
		public void setTicksLabels(ArrayList<String> ticksLabels) {
			this.ticksLabels = ticksLabels;
		}

		/**
		 * @param ticksLocations the ticksLocations to set
		 */
		public void setTicksLocations(ArrayList<Integer> ticksLocations) {
			this.ticksLocations = ticksLocations;
		}

	}
}
