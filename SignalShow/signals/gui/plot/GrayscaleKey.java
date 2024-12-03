package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

import signals.core.Core;
import signals.gui.IconCache;

@SuppressWarnings("serial")
public class GrayscaleKey extends JPanel {
	
	Image scaleImage; 
	int height; 
	PlottingMath plottingMath; 
	boolean piScaling; 
	
	boolean initialized; 
	
	double minValue, maxValue; 
	
	Line2D.Double axis; 
	GeneralPath tickMarks;
	ArrayList<String> ticksLabels;
	ArrayList<Integer> ticksLocations;
	
	NumberFormat formatter; 
	
	public static int AXIS_LOCATION = 20, OFFSET = 10;
	
	public GrayscaleKey( int height ) {
		
		this.height = height; 
		scaleImage = IconCache.getIcon("/guiIcons/scale256.png").getImage();
		Dimension preferredSize = new Dimension( 60, height + 2*OFFSET );
		setPreferredSize( preferredSize );
		plottingMath = new PlottingMath( 60, height, 0 );
		initialized = false; 
		
		formatter = Core.getDisplayOptions().getFormat();
		
		setOpaque( true );
	
	}
	
	public void setPiScaling( boolean piScaling ) {
		
		this.piScaling = piScaling; 
		plottingMath.createTicksY( piScaling );
		ticksLocations = plottingMath.getTicksYLocations(); 
		tickMarks = PlottingGraphics.createTicksY( ticksLocations, AXIS_LOCATION, OFFSET );	
		ticksLabels = plottingMath.getTicksYLabels(); 
		axis = new Line2D.Double( AXIS_LOCATION, OFFSET, AXIS_LOCATION, OFFSET+height );
		repaint(); 
	}
	
	public void setScale( double minValue, double maxValue ) {
		
		this.maxValue = minValue; 
		this.maxValue = maxValue;
		
		plottingMath.setWindow(0, 50, minValue, maxValue);
		initialized = true; 
	
		plottingMath.createTicksY( piScaling );
 
		ticksLocations = plottingMath.getTicksYLocations(); 
		tickMarks = PlottingGraphics.createTicksY( ticksLocations, AXIS_LOCATION, OFFSET );	
		ticksLabels = plottingMath.getTicksYLabels(); 
		axis = new Line2D.Double( AXIS_LOCATION, OFFSET, AXIS_LOCATION, OFFSET+height );
		repaint(); 
		
	}
	
	public void paintComponent( Graphics g ) {
		
		super.paintComponent(g); 
		
		//paint the scale
		g.drawImage( scaleImage, 0, OFFSET, 10, height, this );
		
		if( !initialized ) return; 
		
		Graphics2D g2 = (Graphics2D)g;

		g2.setColor( Color.black );
		g2.draw( axis ); 
		g2.draw( tickMarks );
		
		int offset = AXIS_LOCATION + 10; 
		
		g2.setFont( Core.getDisplayOptions().getAxisFont() ); 

		for( int i = 0; i < ticksLabels.size(); i++ ) { 

			g2.drawString(ticksLabels.get( i ), offset, ticksLocations.get( i )+OFFSET);

		}
	}

}
