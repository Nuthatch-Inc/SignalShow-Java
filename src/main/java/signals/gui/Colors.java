package signals.gui;

import java.awt.Color;
import java.util.ArrayList;

public class Colors {

	Color realPlotColor, imagPlotColor, frameColor, axesColor,
		  gridlinesColor, backgroundColor, hoverColor; 
	
	ArrayList<Color> defaultPlotColors; 
	
	public Colors() {
		
		realPlotColor = new Color( 0, 0, 125 ); 
		imagPlotColor = new Color( 150, 0 , 0 ); 
		gridlinesColor = Color.lightGray;
		frameColor = new Color( 91, 160, 255 );
		hoverColor = new Color( 103, 214, 255 );
		axesColor = Color.black;
		backgroundColor = Color.white;
		
		defaultPlotColors = new ArrayList<Color>();
		defaultPlotColors.add( realPlotColor );
		defaultPlotColors.add( new Color( 150, 0, 0, 200 ) );  //red
		defaultPlotColors.add( new Color( 0, 74, 101, 200 ) );  //teal
		defaultPlotColors.add( new Color( 150, 194, 24, 150 ) ); //lime
		defaultPlotColors.add( new Color( 174, 0, 68, 150 )); //maroon 
		defaultPlotColors.add( new Color( 244, 134, 11, 150 )); //orange
		defaultPlotColors.add( new Color( 241, 33, 2, 150 )); //red
		defaultPlotColors.add( new Color( 71, 0, 113, 150 )); //purple
	}
	
	public Color getDefaultColor( int idx ) {
		
		return defaultPlotColors.get( idx % defaultPlotColors.size() );
	}

	/**
	 * @return the gridlinesColor
	 */
	public Color getGridlinesColor() {
		return gridlinesColor;
	}

	/**
	 * @param gridlinesColor the gridlinesColor to set
	 */
	public void setGridlinesColor(Color gridlinesColor) {
		this.gridlinesColor = gridlinesColor;
	}

	/**
	 * @return the frameColor
	 */
	public Color getFrameColor() {
		return frameColor;
	}

	/**
	 * @param frameColor the frameColor to set
	 */
	public void setFrameColor(Color frameColor) {
		this.frameColor = frameColor;
	}

	/**
	 * @return the imagPlotColor
	 */
	public Color getImagPlotColor() {
		return imagPlotColor;
	}

	/**
	 * @param imagPlotColor the imagPlotColor to set
	 */
	public void setImagPlotColor(Color imagPlotColor) {
		this.imagPlotColor = imagPlotColor;
	}

	/**
	 * @return the realPlotColor
	 */
	public Color getRealPlotColor() {
		return realPlotColor;
	}

	/**
	 * @param realPlotColor the realPlotColor to set
	 */
	public void setRealPlotColor(Color realPlotColor) {
		this.realPlotColor = realPlotColor;
	}

	/**
	 * @return the axesColor
	 */
	public Color getAxesColor() {
		return axesColor;
	}

	/**
	 * @param axesColor the axesColor to set
	 */
	public void setAxesColor(Color axesColor) {
		this.axesColor = axesColor;
	}

	/**
	 * @return the defaultPlotColors
	 */
	public ArrayList<Color> getDefaultPlotColors() {
		return defaultPlotColors;
	}

	/**
	 * @param defaultPlotColors the defaultPlotColors to set
	 */
	public void setDefaultPlotColors(ArrayList<Color> defaultPlotColors) {
		this.defaultPlotColors = defaultPlotColors;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getHoverColor() {
		return hoverColor;
	}
	
}
