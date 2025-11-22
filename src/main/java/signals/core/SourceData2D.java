package signals.core;

import signals.core.Constants.Part;

public class SourceData2D extends SourceData {
	
	public SourceData2D(Function function, Part part) {
		super(function, part);
	}
	
	public int getDimensionX() {
		
		return ((Function2D)function).getDimensionX();
	}
	
	public int getDimensionY() {
		
		return ((Function2D)function).getDimensionY();
	}

}
