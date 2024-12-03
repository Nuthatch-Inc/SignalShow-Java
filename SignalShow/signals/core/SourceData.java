package signals.core;

import signals.core.Constants.Part;

public class SourceData {
	
	protected Function function; 
	protected Constants.Part part;
	String label; 
	boolean hasLabel; 
	
	/**
	 * @param function
	 * @param part
	 */
	public SourceData(Function function, Part part) {
		this.function = function;
		this.part = part;
		hasLabel = false; 
	}
	
	public SourceData(Function function, Part part, String label) {
		this.function = function;
		this.part = part;
		this.label = label; 
		hasLabel = true; 
	}
	
	public double[] getData() {
		
		return function.getPart( part );
	}
	
	public String getCompactLabel() {
		
		if( hasLabel ) return label; 
		return Constants.partLabel( part );
	}
	
	public String getLongLabel() {
		
		return Constants.partLabel( part ) + "{" + function.getLongDescriptor() + "}";
	}
	
	/**
	 * @return the function
	 */
	public Function getFunction() {
		return function;
	}
	/**
	 * @param function the function to set
	 */
	public void setFunction(Function function) {
		this.function = function;
	}
	/**
	 * @return the part
	 */
	public Constants.Part getPart() {
		return part;
	}
	/**
	 * @param part the part to set
	 */
	public void setPart(Constants.Part part) {
		this.part = part;
	} 
	
	public boolean isZeroCentered() {
		
		return function.isZeroCentered();
	}

}
