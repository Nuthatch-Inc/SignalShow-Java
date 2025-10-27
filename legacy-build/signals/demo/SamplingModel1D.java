package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.gui.IconCache;

public class SamplingModel1D extends SamplingModel {

	public SamplingModel1D(Function input) {
		super(input, IconCache.getIcon("/guiIcons/SamplingDemo.png"));
	}
	
	public SamplingModel1D clone() {
		
		return new SamplingModel1D( input ); 
	}

	@Override
	public void show() {
		
		Sampling1D demo = new Sampling1D( (Function1D)input ); 
		Core.getGUI().removeContent(); 
		Core.getGUI().setContent(demo); 
		
	}
	
	public void showNew() {
		show(); 
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		
	}
}
