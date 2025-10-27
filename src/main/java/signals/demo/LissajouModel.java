package signals.demo;

import signals.core.Core;
import signals.core.DemoModel;
import signals.gui.IconCache;

public class LissajouModel extends DemoModel {
	
	
	public LissajouModel(){
		super(IconCache.getIcon("/guiIcons/LissajouDemo.png"));
	}

	public LissajouModel clone() {
		
		return new LissajouModel(); 
	}
	
	public void show() {
		
		Lissajou demo = new Lissajou(); 
		Core.getGUI().removeContent(); 
		Core.getGUI().setContent(demo); 
		
	}

	public String getCompactDescriptor() {
		return "Lissajou Demo";
	}

	public String getLongDescriptor() {
		return "Lissajou Demo"; 
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
