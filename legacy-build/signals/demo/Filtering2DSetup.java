package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.VerticalThumbnailList;

@SuppressWarnings("serial")
public class Filtering2DSetup extends DemoSetup {

	public Filtering2DSetup() {
		
		super( "New 2D Frequency-Domain Filtering Demo"); 
	}

	@Override
	public void setupDemo(Function input ) {
		FilteringModel2D model = new FilteringModel2D(input); 
		Core.getGUI().getFunctionList().addItem(model); 
	}

	@Override
	public VerticalThumbnailList getVariableList() {
		return Core.getFunction2DList(); 
	}

}
