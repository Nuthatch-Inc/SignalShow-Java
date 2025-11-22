package signals.core;

public interface Showable {
	
	public void show(); 
	
	public void setShowState( String state ); 
	
	public String getShowState(); 
	
	public void freeMemory(); 
	
	public Object clone(); 
}
