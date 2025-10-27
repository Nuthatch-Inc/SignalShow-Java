package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGenerator;
import signals.core.DataGeneratorTypeModel;

public class HighPassFunctionTerm1D extends AnalyticFunctionTerm1D {


	public HighPassFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	AnalyticFunctionTerm1D base; 
	
	public HighPassFunctionTerm1D(ParameterBlock paramBlock) {
		super(paramBlock);
	}
	
	public HighPassFunctionTerm1D( AnalyticFunctionTerm1D base ) {
		
		super(base, base.getParamBlock()); //spoof
		this.base = base; 
	}

	@Override
	public double[] create(double[] indices) {
		
		double[] basedata = base.create(indices); 
		double amplitude = base.getAmplitude(); 
		
		for( int i = 0; i < indices.length; i++ ) {
			
			basedata[i] = amplitude - basedata[i]; 
		}
		
		return basedata;
	}

	@Override
	public double[] create(int dimension, boolean zeroCentered) {
		
		double[] basedata = base.create(dimension, zeroCentered); 
		double amplitude = base.getAmplitude(); 
		
		for( int i = 0; i < dimension; i++ ) {
			
			basedata[i] = amplitude - basedata[i]; 
		}
		
		return basedata;
	}

	@Override
	public void setWidthScale(double scale) {
		base.setWidthScale(scale); 
	}

	@Override
	public String getEquation(String[] variables) {
		
		return getAmplitude() + " - " + base.getEquation(variables);
	}

	@Override
	public String amplitudeMultiplierString() {
		
		return base.amplitudeMultiplierString();
	}

	@Override
	public String formattedParamString(String param) {
	
		return base.formattedParamString(param);
	}

	@Override
	public String formattedParamStringNeg(String param) {
		
		return base.formattedParamStringNeg(param);
	}

	@Override
	public double getAmplitude() {
		
		return base.getAmplitude();
	}

	@Override
	public int getAmplitudeIndex() {
		
		return base.getAmplitudeIndex();
	}

	@Override
	public double getCenter() {
	
		return base.getCenter();
	}

	@Override
	public int getCenterIndex() {
	
		return base.getCenterIndex();
	}

	@Override
	public double getWidth() {
	
		return base.getWidth();
	}

	@Override
	public int getWidthIndex() {
	
		return base.getWidthIndex();
	}

	@Override
	public boolean hasAmplitude() {
	
		return base.hasAmplitude();
	}

	@Override
	public boolean hasCenter() {
		
		return base.hasCenter();
	}

	@Override
	public boolean hasWidth() {
		
		return base.hasWidth();
	}

	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {
	
		base.initTypeModel(model);
	}

	@Override
	public boolean isHalfWidthDefined() {
	
		return base.isHalfWidthDefined();
	}

	@Override
	public void setAmplitude(double amplitude) {
	
		base.setAmplitude(amplitude);
	}

	@Override
	public void setCenter(double center) {
	
		base.setCenter(center);
	}

	@Override
	public void setWidth(double width) {

		base.setWidth(width);
	}

	@Override
	public DataGenerator getDefaultInstance() {
	
		return base.getDefaultInstance();
	}

	@Override
	public ParameterBlock getDefaultParamBlock() {
	
		return base.getDefaultParamBlock();
	}

	@Override
	public DataGenerator getInstance(ParameterBlock paramBlock) {
	
		return base.getInstance(paramBlock);
	}

	@Override
	public ParameterBlock getParamBlock() {
	
		return base.getParamBlock();
	}

	@Override
	public ParameterBlock getParamBlockClone() {
		
		return base.getParamBlockClone();
	}

	@Override
	public Object getParameter(int index) {
		
		return base.getParameter(index);
	}

	@Override
	public Vector<Object> getParameters() {
	
		return base.getParameters();
	}

	@Override
	public SpinnerNumberModel[] getSpinnerModels() {
	
		return base.getSpinnerModels();
	}

	@Override
	public DataGeneratorTypeModel getTypeModel() {
		
		return base.getTypeModel();
	}

	@Override
	public void setParamBlock(ParameterBlock paramBlock) {

		base.setParamBlock(paramBlock);
	}

	@Override
	public void setParameter(Object value, int index) {

		base.setParameter(value, index);
	}

}
