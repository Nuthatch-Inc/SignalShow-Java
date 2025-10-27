package signals.gui.plot;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import signals.core.Function;
import signals.core.Function1D;
import signals.core.SourceData2D;
import signals.core.Constants.Part;
import signals.gui.IconCache;
import signals.gui.ResizablePane;

/**
 * This panel shows four plots - real, imaginary, magnitude, 
 * and phase. When a plot is clicked, A larger version is shown
 * with a full plot toolbar. 
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class Function2DTabPanel extends JTabbedPane implements ResizablePane {

	//four plots
	ImageCursorPanel[] plots;
	public static final Part[] parts = { Part.REAL_PART, Part.IMAGINARY_PART, Part.MAGNITUDE, Part.PHASE };
	public static final int REAL=0, IMAG=1, MAG=2, PHASE=3;
	
	/**
	 * 
	 * @param size dimension of a single plot
	 */
	public Function2DTabPanel( Dimension size ) {
		
		setUI( new BasicTabbedPaneUI() ); 
		
		plots = new ImageCursorPanel[4]; 
		
		ImageIcon[] icons = new ImageIcon[4]; 
		icons[REAL] = IconCache.getIcon("/demoIcons/realTab.png"); 
		icons[IMAG] = IconCache.getIcon("/demoIcons/imagTab.png"); 
		icons[MAG] = IconCache.getIcon("/demoIcons/magTab.png"); 
		icons[PHASE] = IconCache.getIcon("/demoIcons/phaseTab.png");
		
		//add plots
		for( int i=0; i < parts.length; i++ ) {
			
			plots[i] = new ImageCursorPanel( size ); 
			addTab( "", icons[i], plots[i] ); 
		}
		
	}
	
	public void sizeChanged() {
		
//		for( ImageCursorPanel plot : plots ) {
//			
//			//plot.sizeChanged();
//		}
	}
	
	public void addFunction( Function1D function ) {
		
		for( int i=0; i < parts.length; i++ ) {
		
			plots[i].setImage( new SourceData2D( (Function)function, parts[i] ) );
		}
	}
	
	public void removePlots() {
		
		for( int i=0; i < parts.length; i++ ) {
			
			plots[i].removeAll();
		}
	}
	
}
