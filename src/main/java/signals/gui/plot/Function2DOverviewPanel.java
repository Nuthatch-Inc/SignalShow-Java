package signals.gui.plot;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import signals.core.Core;
import signals.core.Function;
import signals.core.SourceData2D;
import signals.core.Constants.Part;
import signals.gui.IconCache;

/**
 * This panel shows four plots - real, imaginary, magnitude, 
 * and phase. When a plot is clicked, A larger version is shown
 * with a full plot toolbar. 
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class Function2DOverviewPanel extends FunctionOverviewPanel {

	public Function2DOverviewPanel() {
		super();
	}

	public Function2DOverviewPanel(Function function) {
		super(function);
	}

	@Override
	public void deconstruct() {

		super.deconstruct();  

		if( plots != null ) {

			for( int i=0; i < parts.length; i++ ) {

				if( plots[i] != null )
					((ImageCursorPanel)plots[i]).deconstruct(); 

			}

			if( largePlotPanel != null )
				((ImageCursorPanel)largePlotPanel).deconstruct(); 

		}
	}

	public class PopinImage extends ImageCursorPanel {

		public PopinImage(Dimension size) {
			super(size);
			showToolBar();
		}

		public void showToolBar() {

			super.showToolBar();
			toolBar.add( new JButton( new AbstractAction( "", IconCache.getIcon("/plotIcons/popin.png") ) {

				public void actionPerformed(ActionEvent e) {

					updatePlots(); 
				} 

			}));
		}

	} //end inner class popinImage

	@Override
	public void setupPlots() {

		plots = new ImageCursorPanel[4]; 

		//add plots
		for( int i=0; i < parts.length; i++ ) {

			plots[i] = new ImageCursorPanel( thumbSize ); 

		}

		largePlotPanel = new PopinImage( new Dimension( 400, 400 ) ); 

	}

	@Override
	public void setLargePlot(Part part) {

		((ImageCursorPanel)largePlotPanel).setImage( new SourceData2D( function, part ) );
		largePlotPanel.setBorder( BorderFactory.createCompoundBorder(Core.getBorders().getBuffer(), 
				Core.getBorders().getUnselectedFrameTransparent()));
	}

	@Override
	public void refreshSmallPlots() {

		for( int i=0; i < parts.length; i++ ) {

			plots[i].removeAll();
			((ImageCursorPanel)plots[i]).setImage( new SourceData2D( function, parts[i] ) );
		}

	}

	@Override
	public Dimension getThumbSize() {

		return new Dimension( 175, 175 ); 
	}


}
