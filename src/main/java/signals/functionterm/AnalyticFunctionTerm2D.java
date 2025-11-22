package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.text.NumberFormat;

import signals.core.Core;
import signals.core.FunctionTerm2D;

public abstract class AnalyticFunctionTerm2D extends FunctionTerm2D {

	int amplitude_idx, x_center_idx, y_center_idx, x_width_idx, y_width_idx; 
	
	public AnalyticFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
		amplitude_idx = 0;
		x_center_idx = 1; 
		y_center_idx = 2; 
		x_width_idx = 3; 
		y_width_idx = 4;
	}

	public AnalyticFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * if the function has an amplitude parameter that is not 1, returns "A x " 
	 */
	public String amplitudeMultiplierString() {
		if( getAmplitude() == 1) return ""; 
		NumberFormat formatter = Core.getDisplayOptions().getFormat();
		return formatter.format(getAmplitude())+" \u00D7 ";
	}
	
	/**
	 * @return the amplitude
	 */
	public double getAmplitude() {
		
		return paramBlock.getDoubleParameter(amplitude_idx);
	}

	/**
	 * @param amplitude the amplitude to set
	 */
	public void setAmplitude(double amplitude) {
		
		paramBlock.set( amplitude, amplitude_idx );

	}
	
	/**
	 * @return the amplitude
	 */
	public double getCenterX() {
		
		return paramBlock.getDoubleParameter(x_center_idx);
	}

	/**
	 * @param amplitude the amplitude to set
	 */
	public void setCenterX(double centerX) {
		
		paramBlock.set( centerX, x_center_idx );

	}
	
	/**
	 * @return the amplitude
	 */
	public double getCenterY() {
		
		return paramBlock.getDoubleParameter(y_center_idx);
	}

	/**
	 * @param amplitude the amplitude to set
	 */
	public void setCenterY(double centerY) {
		
		paramBlock.set( centerY, y_center_idx );

	}
	
	public double getWidth() {
		
		return paramBlock.getDoubleParameter(x_width_idx);
	}

	
	public void setWidth(double width) {
		
		paramBlock.set( width, x_width_idx );

	}
	
	public double getHeight() {
		
		return paramBlock.getDoubleParameter(y_width_idx);
	}

	
	public void setHeight(double height) {
		
		paramBlock.set( height, y_width_idx );

	}

	/**
	 * @return the amplitude_idx
	 */
	public int getAmplitudeIndex() {
		return amplitude_idx;
	}

	/**
	 * @return the x_center_idx
	 */
	public int getXCenterIndex() {
		return x_center_idx;
	}

	/**
	 * @return the y_center_idx
	 */
	public int getYCenterIndex() {
		return y_center_idx;
	}

	/**
	 * @return the width_idx
	 */
	public int getWidthIndex() {
		return x_width_idx;
	}

	/**
	 * @return the height_idx
	 */
	public int getHeightIndex() {
		return y_width_idx;
	}
	
	public abstract boolean hasWidth(); 
	public abstract boolean hasHeight(); 

}
