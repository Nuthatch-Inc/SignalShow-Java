package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.IconCache;

public class FilteringModel1D extends FilteringModel {

	public FilteringModel1D(Function input) {
		super(input, IconCache.getIcon("/guiIcons/filteringDemo.png"));
	}
	
	public FilteringModel1D clone() {
		
		FilteringModel1D copy = new FilteringModel1D(input);
		copy.settings = settings.copy();
		return copy;
	}

	@Override
	public void show() {
		
		Core.getGUI().removeContent();
		FilteringDemo1D demo = createView();
		Core.getGUI().setContent(demo); 
		
	}
	
	protected FilteringDemo1D createView() {
		return new FilteringDemo1D(input, settings);
	}

	public void showNew() {
		show(); 
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		
	}
}
