package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.gui.IconCache;

public class SamplingModel1D extends SamplingModel {

	private SamplingSettings settings;

	public SamplingModel1D(Function input) {
		super(input, IconCache.getIcon("/guiIcons/SamplingDemo.png"));
		settings = new SamplingSettings(((Function1D) input).getDimension());
	}
	
	public SamplingModel1D clone() {
		
		SamplingModel1D copy = new SamplingModel1D(input);
		copy.settings = settings.copy();
		return copy;
	}

	@Override
	public void show() {
		
		Core.getGUI().removeContent();
		Sampling1D demo = createView();
		Core.getGUI().setContent(demo); 
		
	}
	
	protected Sampling1D createView() {
		return new Sampling1D((Function1D) input, settings);
	}

	public void showNew() {
		show(); 
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		
	}
}
