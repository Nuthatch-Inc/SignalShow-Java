package signals.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;

import signals.core.Core;
import signals.gui.SaveImageDialog;
import signals.io.ResourceLoader;

@SuppressWarnings("serial")
public class ScreenShotAction extends AbstractAction {
	
	static final String description = "Save Screen Shot"; 
	static int numshots; 
	
	public ScreenShotAction() {
		
		 super( description, ResourceLoader.createImageIcon("/guiIcons/screenshot.png") );
	}

	public void actionPerformed(ActionEvent arg0) {

		Component c = Core.getGUI().getContent();
        if( c== null ) return; 
        Dimension d = c.getSize(); 
        BufferedImage screenshot = new BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = screenshot.getGraphics(); 
        c.paint(g); 
        
        numshots++; 
        
        SaveImageDialog dialog = new SaveImageDialog( Core.getFrame(), description, screenshot, 
        		"screenshot" + numshots ); 
        dialog.display();
        
	}

}
