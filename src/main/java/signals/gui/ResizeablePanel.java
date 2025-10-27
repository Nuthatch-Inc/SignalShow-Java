package signals.gui;

import javax.swing.JPanel; 

/*
 * Anything displayable in the large tabbed pane (e.g. demos, 
 * plots) should extend this class. These items will be 
 * automatically resized when the window is resized. 
 */
@SuppressWarnings("serial")
public abstract class ResizeablePanel extends JPanel {

	/*
	 * Resize Components
	 * This method should be implemented so that components
	 * within the panel are resized and rearranged in an 
	 * attractive way. 
	 */
	public abstract void resizeComponents( int width, int height ); 
	
	
	/*
	 * This method is called when the window is resized
	 */
	public void resetSize( int width, int height ){
		
		//set the size here
		resizeComponents( width, height ); 
		
	}//resetSize
	
}//ResizeablePanel
