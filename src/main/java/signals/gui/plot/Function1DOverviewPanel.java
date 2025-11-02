package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import signals.core.Constants;
import signals.core.Core;
import signals.core.Function;
import signals.core.SourceData1D;
import signals.core.Constants.Part;
import signals.core.Constants.PlotStyle;
import signals.gui.IconCache;

/**
 * This panel shows four plots - real, imaginary, magnitude, 
 * and phase. When a plot is clicked, A larger version is shown
 * with a full plot toolbar. 
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class Function1DOverviewPanel extends FunctionOverviewPanel {

	Color plotColor; 
	PlotStyle plotStyle; 
	JComboBox styleChooser;

	public static final PlotStyle[] availableStyles = { PlotStyle.SMOOTH, PlotStyle.FILLED, PlotStyle.SCATTER, 
			PlotStyle.STEM, PlotStyle.HISTOGRAM}; 

	public Function1DOverviewPanel(Function function) {
		super(function);
	}
	
	public Function1DOverviewPanel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public JComponent createEquationArea() {

		JPanel equationPanel = new JPanel(); 
		BoxLayout layout = new BoxLayout(equationPanel,BoxLayout.LINE_AXIS); 
		equationPanel.setLayout( layout );  
		JButton colorPicker = new JButton( new ColorPickerAction() ); 
		colorPicker.setMaximumSize(colorPicker.getPreferredSize());
		equationPanel.add( colorPicker ); 
		equationPanel.add( Box.createHorizontalStrut(5) );

		styleChooser = new JComboBox(availableStyles);
		styleChooser.setRenderer(new PlotStyleListRenderer()); 
		styleChooser.setMaximumSize(styleChooser.getPreferredSize());
		plotStyle = ((PlotCursorPanel)plots[0]).getDefaultPlotStyle(); 
		styleChooser.setSelectedItem( plotStyle );
		styleChooser.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				plotStyle = (PlotStyle)(styleChooser.getSelectedItem()); 

				for( int i=0; i < parts.length; i++ ) { 

					((PlotCursorPanel)plots[i]).setPlotStyle(0,plotStyle); 

				}

				((PlotCursorPanel)largePlotPanel).setPlotStyle(0,plotStyle);

			}


		}); 
		equationPanel.add( styleChooser );
		equationPanel.add( Box.createHorizontalStrut(5) );
		plotColor = Core.getColors().getDefaultColor( 0 );
		equationPanel.add( Box.createHorizontalGlue() );
		JComponent equationArea = super.createEquationArea();
		equationArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		equationPanel.add( equationArea ); 
		equationPanel.add( Box.createHorizontalGlue() );

		return equationPanel; 
	}

	public void setDefaultPlotStyle( Constants.PlotStyle plotStyle ) {
		
		this.plotStyle = plotStyle; 
		styleChooser.setSelectedItem(plotStyle);

		for( DisplayCursorPanel plot : plots ) {

			((PlotCursorPanel)plot).setDefaultPlotStyle( plotStyle ); 
		}

		((PlotCursorPanel)largePlotPanel).setDefaultPlotStyle( plotStyle ); 
	}	

	public class PopinPlot extends PlotCursorPanel {

		public PopinPlot(Dimension plotSize) {
			super(plotSize);
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

	} //end inner class popinPlot



	@Override
	public void setupPlots() {

		plots = new PlotCursorPanel[4]; 

		//add plots
		for( int i=0; i < parts.length; i++ ) {

			plots[i] = new PlotCursorPanel( thumbSize ); 
		}

		largePlotPanel = new PopinPlot( new Dimension( 600, 400 ) ); 

	}

	@Override
	public void setLargePlot(Part part) {

		((PlotCursorPanel)largePlotPanel).addPlot( new SourceData1D( function, part ) );
		((PlotCursorPanel)largePlotPanel).setColor(0,plotColor);
		((PlotCursorPanel)largePlotPanel).setPlotStyle(0,plotStyle);
		largePlotPanel.setBorder( BorderFactory.createCompoundBorder(Core.getBorders().getBuffer(), 
				Core.getBorders().getUnselectedFrameTransparent()));

	}

	@Override
	public void refreshSmallPlots() {

		for( int i=0; i < parts.length; i++ ) {

			plots[i].removeAll();
			((PlotCursorPanel)plots[i]).addPlot( new SourceData1D( function, parts[i] ) );
			((PlotCursorPanel)plots[i]).setPlotStyle(0,plotStyle);
			((PlotCursorPanel)plots[i]).setColor(0,plotColor);
		}

	}

	@Override
	public Dimension getThumbSize() {

		return new Dimension( 300, 300 ); 
	}


	public class ColorPickerAction extends AbstractAction {

		public ColorPickerAction() {

			super( "", IconCache.getIcon("/plotIcons/colorchooser.png") ); 
			putValue( SHORT_DESCRIPTION, "Change Plot Color" );
		}

		public void actionPerformed(ActionEvent e) {

			Color newColor = JColorChooser.showDialog(
					null,
					"Select Plot Color",
					((PlotCursorPanel)plots[0]).getColor(0)); 
			
			if( newColor != null ) plotColor = newColor; 

			for( int i=0; i < parts.length; i++ ) { 

				((PlotCursorPanel)plots[i]).setColor(0,plotColor); 

			}


			((PlotCursorPanel)largePlotPanel).setColor(0,plotColor);

		} 

	}

}
