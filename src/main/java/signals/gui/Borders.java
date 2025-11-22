package signals.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import signals.core.Core;

/**
 * Stores several common widgets that are used throughout the GUI
 * @author Juliet
 *
 */
public class Borders {
	
	protected Border etchedBufferBorder; 
	protected Border emptyBorder;
	protected Border lineBorder; 
	protected Border lineBufferBorder;
	protected Border buffer; 
	protected Border hEmptyBorder; 
	protected Border selectedFrame, unselectedFrame, hoverFrame, unselectedFrameTransparent;
	protected Border plotBorder; 
	protected Border edgeBorder; 
	protected Border frame; 

	public Borders() {
		
		lineBorder = BorderFactory.createLineBorder(Color.lightGray);
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		buffer = BorderFactory.createEmptyBorder( 5, 5, 5, 5 ); 
		edgeBorder = BorderFactory.createCompoundBorder( buffer, loweredetched );
		etchedBufferBorder = BorderFactory.createCompoundBorder( edgeBorder, buffer );
		emptyBorder = BorderFactory.createEmptyBorder( 10, 10, 10, 10 ); 
		lineBufferBorder = BorderFactory.createCompoundBorder( buffer, lineBorder );
		hEmptyBorder = BorderFactory.createEmptyBorder( 0, 10, 0, 10 );
		frame = BorderFactory.createCompoundBorder( lineBorder, buffer ); 
		
//		raisedBorder = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.black), 
//				BorderFactory.createRaisedBevelBorder() );
		
		plotBorder = BorderFactory.createLineBorder(Color.black);
		
		selectedFrame = BorderFactory.createCompoundBorder(  
				BorderFactory.createMatteBorder(3, 3, 3, 3, Core.getColors().getFrameColor()), plotBorder);
		
		unselectedFrameTransparent = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(3, 3, 3, 3, new Color( 0, 0, 0, 0)), plotBorder );
		
		unselectedFrame = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(3, 3, 3, 3, Color.white), plotBorder );
		
		hoverFrame = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(3, 3, 3, 3, Core.getColors().getHoverColor()), plotBorder );
	}

	public Border getFrame() {
		return frame;
	}

	public Border getEdgeBorder() {
		return edgeBorder;
	}

	/**
	 * @return the hEmptyBorder
	 */
	public Border getHEmptyBorder() {
		return hEmptyBorder;
	}

	/**
	 * @return the etchedBufferBorder
	 */
	public Border getEtchedBufferBorder() {
		return etchedBufferBorder;
	}

	/**
	 * @return the emptyBorder
	 */
	public Border getEmptyBorder() {
		return emptyBorder;
	}

	public Border getLineBorder() {
		return lineBorder;
	}

	public Border getLineBufferBorder() {
		return lineBufferBorder;
	}

	public Border getBuffer() {
		return buffer;
	}

	/**
	 * @return the raisedBorder
	 */
	public Border getRaisedBorder() {
		return plotBorder;
	}

	/**
	 * @return the selectedFrame
	 */
	public Border getSelectedFrame() {
		return selectedFrame;
	}

	/**
	 * @return the unselectedFrame
	 */
	public Border getUnselectedFrame() {
		return unselectedFrame;
	}
	
	public Border getUnselectedFrameTransparent() {
		return unselectedFrameTransparent;
	}

	public Border getHoverFrame() {
		return hoverFrame;
	}

}
