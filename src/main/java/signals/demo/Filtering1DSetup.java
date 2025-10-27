package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.VerticalThumbnailList;

@SuppressWarnings("serial")
public class Filtering1DSetup extends DemoSetup {

	public Filtering1DSetup() {
		
		super( "New 1D Frequency-Domain Filtering Demo"); 
	}

	@Override
	public void setupDemo(Function input ) {
		FilteringModel1D model = new FilteringModel1D(input); 
		Core.getGUI().getFunctionList().addItem(model); 
	}

	@Override
	public VerticalThumbnailList getVariableList() {
		return Core.getFunction1DList(); 
	}

}
