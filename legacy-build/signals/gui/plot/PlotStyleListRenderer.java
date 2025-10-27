package signals.gui.plot;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import signals.core.Constants.PlotStyle;
import signals.gui.IconCache;

@SuppressWarnings("serial")
public class PlotStyleListRenderer extends JLabel implements ListCellRenderer {

	public PlotStyleListRenderer() {
		setOpaque( true );
		setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

	}
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
		
		switch( (PlotStyle)value) {
		
		case FILLED:
			
			setIcon( IconCache.getIcon("/plotIcons/filledPlot.png") ); 
			setText( "Area Plot"); 
			break; 
			
		case SCATTER: 
			
			setIcon( IconCache.getIcon("/plotIcons/dotPlot.png") ); 
			setText( "Scatter Plot");
			break; 
			
		case SMOOTH: 
			
			setIcon( IconCache.getIcon("/plotIcons/linePlot.png") ); 
			setText( "Line Plot");
			break; 
			
		case STEM: 
			
			setIcon( IconCache.getIcon("/plotIcons/stemPlot.png") ); 
			setText( "Stem Plot");
			break; 
		
		case HISTOGRAM: 
			
			setIcon( IconCache.getIcon("/plotIcons/barPlot.png") ); 
			setText( "Bar Plot");
			break; 
		}
		 
		return this; 
	}
}
