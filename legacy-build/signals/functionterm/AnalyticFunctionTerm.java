package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

public interface AnalyticFunctionTerm {
	
	public boolean hasAmplitude(); 
	public boolean hasCenter(); 
	public boolean hasWidth(); 
	
	//String that represents the amplitude
	public String amplitudeMultiplierString();
	
	//String that represents the shifted and scaled parameter
	public String formattedParamString( String param ); 
	
	public double getAmplitude(); 
	public double getCenter(); 
	public double getWidth(); 
	
	public void setAmplitude( double amplitude ); 
	public void setCenter( double center ); 
	public void setWidth( double width );
	public boolean isHalfWidthDefined(); 
	public DataGeneratorTypeModel getTypeModel();
	
	public int getAmplitudeIndex(); 
	public int getWidthIndex(); 
	public int getCenterIndex();
	public Vector<Object> getParameters();
	public ParameterBlock getParamBlock();
	public SpinnerNumberModel[] getSpinnerModels();

}
