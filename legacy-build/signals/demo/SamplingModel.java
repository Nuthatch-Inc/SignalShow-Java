package signals.demo;

import javax.swing.ImageIcon;

import signals.core.DemoModel;
import signals.core.Function;

public abstract class SamplingModel extends DemoModel {
	
	Function input; 
	boolean displayed; 
	
	public SamplingModel(Function input, ImageIcon icon) { 
		super(icon);
		this.input = input;
	}

	public abstract void show(); 

	public String getCompactDescriptor() {
		return "Sampling Demo";
	}

	public String getLongDescriptor() {
		return "Sampling Demo"; 
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
	
	public String getShowState() {
		// TODO Auto-generated method stub
		return null;
	}



	public void setShowState(String state) {
		// TODO Auto-generated method stub
		
	}
	

}
