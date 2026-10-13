package signals.demo;

import signals.core.Core;
import signals.core.DemoModel;
import signals.core.Function;
import signals.gui.IconCache;

public class FrequencyLocation2DModel extends DemoModel {
	
	Function input;
	FrequencyLocationSettings settings = new FrequencyLocationSettings();
	
	public FrequencyLocation2DModel(Function input) { 
		super(IconCache.getIcon("/guiIcons/FrequencyLocationDemo.png"));
		this.input = input;
	}
	
	public FrequencyLocation2DModel clone() {
		
		FrequencyLocation2DModel copy = new FrequencyLocation2DModel(input);
		copy.settings = settings.copy();
		return copy;
	}

	public void show() {
		
		Core.getGUI().removeContent();
		FrequencyLocation2D demo = createView();
		Core.getGUI().setContent(demo); 
		
	}

	public String getCompactDescriptor() {
		return "Frequency Locations Demo";
	}

	public String getLongDescriptor() {
		return "Frequency Locations Demo"; 
	}
	
	protected FrequencyLocation2D createView() {
		return new FrequencyLocation2D(input, settings);
	}

	public void showNew() {
		show(); 
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		
	}
	
	public String getShowState() {
		// TODO Auto-generated method stub
		return null;
	}



	public void setShowState(String state) {
		// TODO Auto-generated method stub
		
	}

}
