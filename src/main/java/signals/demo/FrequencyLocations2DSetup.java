package signals.demo;

import signals.core.Core;
import signals.core.Function;
import signals.gui.VerticalThumbnailList;

@SuppressWarnings("serial")
public class FrequencyLocations2DSetup extends DemoSetup {

	public FrequencyLocations2DSetup() {
		
		super( "New 2D Frequency Locations Demo"); 
	}

	@Override
	public void setupDemo(Function input ) {
		FrequencyLocation2DModel model = new FrequencyLocation2DModel(input); 
		Core.getGUI().getFunctionList().addItem(model); 
	}

	@Override
	public VerticalThumbnailList getVariableList() {
		return Core.getFunction2DList(); 
	}

}
