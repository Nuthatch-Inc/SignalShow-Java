package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.IconCache;

public class FilteringModel2D extends FilteringModel {

	public FilteringModel2D(Function input) {
		super(input, IconCache.getIcon("/guiIcons/filteringDemo2D.png"));
	}
	
	public FilteringModel2D clone() {
		
		return new FilteringModel2D( input ); 
	}

	@Override
	public void show() {
		
		FilteringDemo2D demo = new FilteringDemo2D( input ); 
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
