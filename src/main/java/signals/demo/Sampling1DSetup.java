package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.VerticalThumbnailList;

@SuppressWarnings("serial")
public class Sampling1DSetup extends DemoSetup {

	public Sampling1DSetup() {
		
		super( "New 1D Sampling/Aliasing Demo"); 
	}

	@Override
	public void setupDemo(Function input ) {
		SamplingModel1D model = new SamplingModel1D(input); 
		Core.getGUI().getFunctionList().addItem(model); 
	}

	@Override
	public VerticalThumbnailList getVariableList() {
		return Core.getFunction1DList(); 
	}

}
