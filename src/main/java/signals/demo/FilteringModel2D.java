package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.IconCache;

public class FilteringModel2D extends FilteringModel {

	public FilteringModel2D(Function input) {
		super(input, IconCache.getIcon("/guiIcons/filteringDemo2D.png"));
	}
	
	public FilteringModel2D clone() {
		
		FilteringModel2D copy = new FilteringModel2D(input);
		copy.settings = settings.copy();
		return copy;
	}

	@Override
	public void show() {
		
		Core.getGUI().removeContent();
		FilteringDemo2D demo = createView();
		Core.getGUI().setContent(demo); 
		
	}
	
	protected FilteringDemo2D createView() {
		return new FilteringDemo2D(input, settings);
	}

	public void showNew() {
		show(); 
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		
	}
}
