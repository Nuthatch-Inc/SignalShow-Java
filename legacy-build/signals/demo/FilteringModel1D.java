package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.IconCache;

public class FilteringModel1D extends FilteringModel {

	public FilteringModel1D(Function input) {
		super(input, IconCache.getIcon("/guiIcons/filteringDemo.png"));
	}
	
	public FilteringModel1D clone() {
		
		return new FilteringModel1D( input ); 
	}

	@Override
	public void show() {
		
		FilteringDemo1D demo = new FilteringDemo1D( input ); 
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
