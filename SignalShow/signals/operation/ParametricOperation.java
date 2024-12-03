package signals.operation;

public interface ParametricOperation {
	
	public void setValue( int index, double value ); 
	
	public double getValue( int index ); 
	
	public int getNumParams(); 
	
	public String getParamName( int index );
	
	public String getParamDescriptor();

}
