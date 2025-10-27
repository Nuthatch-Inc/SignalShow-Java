package signals.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import signals.core.Core;
import signals.core.Function1D;

/**
 * This class renders a cell in a list or a combobox. It displays a small thumbnail
 * of a thumbnail producer 
 */

@SuppressWarnings("serial")
public class ThumbnailListRenderer extends JPanel implements ListCellRenderer, TableCellRenderer {

	//a colored border is drawn around this cell when it is selected
	Border selectedBorder, unselectedBorder, hoverBorder; 

	//the function that this represents
	ThumbnailProducer thumbnailProducer;  

	/*
	 * Default Constructor
	 */
	public ThumbnailListRenderer() {

		setOpaque( true );
		setBackground( Core.getColors().getBackgroundColor() );
		selectedBorder = Core.getBorders().getSelectedFrame(); 
		unselectedBorder = Core.getBorders().getUnselectedFrame();
		hoverBorder = Core.getBorders().getHoverFrame(); 
	}


	/*
	 * Draws a thumbnail plot and the text of the name 
	 */ 
	public void paintComponent( Graphics g ) {

		super.paintComponent( g );
	//	thumbnailProducer.paintSmallGraphic((Graphics2D)g);
		Dimension d = getPreferredSize(); 
	
		thumbnailProducer.paintGraphic((Graphics2D)g, d.width, d.height );

	} 
	
	public Component getTableCellRendererComponent(JTable table, 
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		thumbnailProducer = (ThumbnailProducer)value;

		//code modified from http://www.digitalredemption.net/2006/01/adding-rollovers-to-jlist-comp.html
		if (column == ((HorizontalThumbnailList)table).getMouseOver() && !isSelected)

			setBorder( hoverBorder ); 
		else
			if(isSelected)
				setBorder( selectedBorder ); 
			else
				setBorder( unselectedBorder ); 

		repaint();

		//set the tool tip text
		String toolText = thumbnailProducer.getLongDescriptor(); 
		if( toolText != null && !toolText.equals("") ) {

			setToolTipText(toolText);
		}

		return this;

	} 


	/**
	 * this method is necessary for this class to be a cell renderer. 
	 * when the cell is selected or deselected, this method is called. 
	 */
	public Component getListCellRendererComponent(
			JList list,
			Object value,   //Data Generator
			int index,      // cell index
			boolean isSelected,    // is the cell selected
			boolean cellHasFocus ) {   // the list and the cell have the focus
		

		int preferredWidth = list.getWidth(); 
		thumbnailProducer = (ThumbnailProducer)value; 
		
		if( value instanceof Function1D ) {
		
			thumbnailProducer.setDirty();
			setPreferredSize(new Dimension( preferredWidth, (int) ((100.0/128)*preferredWidth)  ));
			
			
		} else {

			thumbnailProducer.setDirty();
			setPreferredSize(new Dimension( preferredWidth, preferredWidth)); 
		}

		//code modified from http://www.digitalredemption.net/2006/01/adding-rollovers-to-jlist-comp.html
		if (index == ((VerticalThumbnailList)list).getMouseOver() && !isSelected)

			setBorder( hoverBorder ); 
		else
			if(isSelected)
				setBorder( selectedBorder ); 
			else
				setBorder( unselectedBorder ); 



		repaint();

		//set the tool tip text
		String toolText = thumbnailProducer.getLongDescriptor(); 
		if( toolText != null && !toolText.equals("") ) {

			setToolTipText(toolText);
		}

		return this;

	} 
} 