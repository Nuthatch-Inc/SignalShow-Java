package signals.demo;

import signals.core.Core;
import signals.core.DemoModel;
import signals.core.Function;
import signals.gui.IconCache;

public class FrequencyLocation2DModel extends DemoModel {
	
	Function input; 
	
	public FrequencyLocation2DModel(Function input) { 
		super(IconCache.getIcon("/guiIcons/FrequencyLocationDemo.png"));
		this.input = input;
	}
	
	public FrequencyLocation2DModel clone() {
		
		return new FrequencyLocation2DModel( input ); 
	}

	public void show() {
		
		FrequencyLocation2D demo = new FrequencyLocation2D( input ); 
		Core.getGUI().removeContent(); 
		Core.getGUI().setContent(demo); 
		
	}

	public String getCompactDescriptor() {
		return "Frequency Locations Demo";
	}

	public String getLongDescriptor() {
		return "Frequency Locations Demo"; 
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
