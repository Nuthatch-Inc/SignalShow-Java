package signals.gui.plot;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import signals.core.Constants;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.SourceData1D;
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
public class Function1DTabPanel extends JTabbedPane implements ResizablePane {

	//four plots
	PlotCursorPanel[] plots;
	public static final Part[] parts = { Part.REAL_PART, Part.IMAGINARY_PART, Part.MAGNITUDE, Part.PHASE };
	public static final int REAL=0, IMAG=1, MAG=2, PHASE=3;


	/**
	 * 
	 * @param size dimension of a single plot
	 */
	public Function1DTabPanel( Dimension size ) {

		plots = new PlotCursorPanel[4]; 

		ImageIcon[] icons = new ImageIcon[4]; 
		icons[REAL] = IconCache.getIcon("/demoIcons/realTab.png"); 
		icons[IMAG] = IconCache.getIcon("/demoIcons/imagTab.png"); 
		icons[MAG] = IconCache.getIcon("/demoIcons/magTab.png"); 
		icons[PHASE] = IconCache.getIcon("/demoIcons/phaseTab.png");

		//add plots
		for( int i=0; i < parts.length; i++ ) {

			plots[i] = new PlotCursorPanel( size ); 
			addTab( "", icons[i], plots[i] ); 
		}

	}

	public void setCompactLabelMode(boolean compactLabelMode) {
		for( PlotCursorPanel plot : plots ) {

			plot.setCompactLabelMode( compactLabelMode ); 
		}
	}

	public void setDefaultPlotStyle( Constants.PlotStyle plotStyle ) {

		for( PlotCursorPanel plot : plots ) {

			plot.setDefaultPlotStyle( plotStyle ); 
		}

	}	


	public void setPlotStyle( int graphicIndex, Constants.PlotStyle plotStyle ) {

		for( PlotCursorPanel plot : plots ) {

			plot.setPlotStyle( graphicIndex, plotStyle ); 
		}

	}
	
	public int getNumberPlots() {
		return plots[0].getNumberPlots(); 
	}

	public void lockScale( Function function ) {

		for( int i=0; i < parts.length; i++ ) {

			plots[i].lockScale( new SourceData1D( function, parts[i] ) );
		}
	}

	public void sizeChanged() {

		for( PlotCursorPanel plot : plots ) {

			plot.sizeChanged();
		}
	}

	public void addSourceData( SourceData1D function ) {

		for( int i=0; i < parts.length; i++ ) {

			plots[i].addPlot( function );
		}
	}

	public void addFunction( Function1D function, String label ) {

		for( int i=0; i < parts.length; i++ ) {

			plots[i].addPlot( new SourceData1D( (Function)function, parts[i], label ) );
		}
	}

	public void addFunction( Function1D function ) {

		for( int i=0; i < parts.length; i++ ) {

			plots[i].addPlot( new SourceData1D( (Function)function, parts[i] ) );
		}
	}

	public void removePlots() {

		for( int i=0; i < parts.length; i++ ) {

			plots[i].removeAll();
		}
	}

}
