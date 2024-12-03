package signals.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class IconListRenderer extends JLabel implements ListCellRenderer {

//	a colored border is drawn around this cell when it is selected
	Border raisedBorder, selectedBorder, unselectedBorder; 
	Color selectedBorderColor, unselectedBorderColor; 

	public IconListRenderer(Dimension size) {
		setOpaque( true );
		raisedBorder = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.black), 
				BorderFactory.createRaisedBevelBorder() );
		resetColors();
		setPreferredSize( size );
	}

	protected void resetColors() {

		//TODO: change this to request colors from signals colors
		selectedBorderColor = Color.orange;
		unselectedBorderColor = Color.white; 
		setBackground( Color.white );

		selectedBorder = BorderFactory.createCompoundBorder(  
				BorderFactory.createMatteBorder(3, 3, 3, 3, selectedBorderColor), raisedBorder);

		unselectedBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(3, 3, 3, 3, unselectedBorderColor), raisedBorder );
	}




	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		boolean selected = list.getSelectedIndex() == index; 
		IconListItem item = (IconListItem)value; 
		setBorder( isSelected ? selectedBorder : unselectedBorder ); 
		setPreferredSize( selected ? item.getLargeIconSize() : item.getSmallIconSize() );
		setIcon( selected ? item.getLargeIcon() : item.getSmallIcon() ); 
		return this; 
	}
}
