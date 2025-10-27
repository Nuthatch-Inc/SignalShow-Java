package signals.gui.datagenerator;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JTabbedPane;

import signals.core.Constants;
import signals.core.FunctionTerm1D;
import signals.functionterm.AnalyticFunctionTerm;
import signals.gui.plot.PlottingGraphics;
import signals.gui.plot.PlottingMath;

@SuppressWarnings("serial")
public class Noise1DFunctionTermEditor extends AnalyticFunctionTerm1DEditor {
	
	HistogramInteractivePlot histogramPlot; 
	DataPlot dataPlot; 
	
	public Noise1DFunctionTermEditor( GUIEventBroadcaster broadcaster,
			GUIEvent.Descriptor modifiedDescriptor, EditAnalyticFunctionTermPanel editor ) {
		
		super( broadcaster, modifiedDescriptor ); 
		
		histogramPlot = new HistogramInteractivePlot( broadcaster, modifiedDescriptor, editor );
		dataPlot = new DataPlot( broadcaster, modifiedDescriptor, editor );
		
		//layout
		JTabbedPane tabbedPane = new JTabbedPane(); 
		tabbedPane.setTabPlacement(JTabbedPane.LEFT);
		tabbedPane.addTab("Data" ,dataPlot ); 
		tabbedPane.addTab("Histogram", histogramPlot );
		add( tabbedPane );
	}
	
	@Override
	public void setFunctionTerm(AnalyticFunctionTerm term) {
		
		histogramPlot.setFunctionTerm( term ); 
		dataPlot.setFunctionTerm( term ); 
		
	}

	@Override
	public void setIndices(double[] indices) {
	
		histogramPlot.setRawDimension( indices.length ); 
		dataPlot.setIndices( indices );
	}
	
	public class DataPlot extends AnalyticInteractivePlot {

		public DataPlot(GUIEventBroadcaster broadcaster,
				GUIEvent.Descriptor modifiedDescriptor, EditAnalyticFunctionTermPanel editor) {
			super(broadcaster, modifiedDescriptor, editor);
			graphic = new DataPlotGraphic( null, null );
		}
		
		public void setBorder() {};
		
		public void dragAmplitude( MouseEvent e ) {

			PlottingMath plottingMath = graphic.getPlottingMath();
			double initAmplitude = plottingMath.plotToMathY( e.getY() );
			double center = currentFunction.getCenter();
			if ( initAmplitude > center ) {

				double newAmplitude = 0; 
				if ( initAmplitude >= plottingMath.getYWindowMax() ) {
					
					double oldAmplitude = currentFunction.getAmplitude(); 
					newAmplitude = oldAmplitude + PlottingMath.windowNudgeScalar*(Math.abs(oldAmplitude));
					plottingMath.setYWindowMax(center+newAmplitude);
					
				} else {
					newAmplitude = initAmplitude - center;
				}

				currentFunction.setAmplitude(newAmplitude); 
				repaint();
				editor.setAmplitude( newAmplitude );

			}
		}
		
		public void dragCenter( MouseEvent e ) {

			PlottingMath plottingMath = graphic.getPlottingMath();
			double newCenter = plottingMath.plotToMathY( e.getY() );

			if ( newCenter >= plottingMath.getYWindowMax() ) {

				double oldCenter = currentFunction.getCenter(); 
				newCenter = oldCenter + PlottingMath.windowNudgeScalar*(Math.abs(oldCenter));
				plottingMath.setYWindowMax(newCenter);

			} else if( newCenter <= plottingMath.getYWindowMin() ) {

				double oldCenter = currentFunction.getCenter(); 
				newCenter = oldCenter - PlottingMath.windowNudgeScalar*(Math.abs(oldCenter));
				plottingMath.setYWindowMin(newCenter);

			} 
			currentFunction.setCenter(newCenter); 
			repaint();
			editor.setCenter( newCenter );
		}

		public void updateWidth() {
			hasWidth = false; 
		}
		
		public class DataPlotGraphic extends AnalyticInteractivePlotGraphic {

			public DataPlotGraphic(FunctionTerm1D currentFunction, double[] indices) {
				super(currentFunction, indices);
				setPlotStyle( Constants.PlotStyle.SCATTER );
			}
			
			public void setLines() {

				int plotWidth = getLargeDimension().width;
				
				if( hasAmplitude ) {
					double plotAmplitude = largePlottingMath.mathToPlotY(center+amplitude);
					amplitudeLine = PlottingGraphics.xAxis( plotWidth, plotAmplitude, OFFSET ); 
					amplitudeBox = new Rectangle2D.Double( 0, plotAmplitude-3, plotWidth, 6 );
				}

				double plotCenter = 0; 
				
				if( hasCenter ) {
				
					plotCenter = largePlottingMath.mathToPlotY(center);
					centerLine = PlottingGraphics.xAxis( plotWidth, plotCenter, OFFSET );
					centerBox = new Rectangle2D.Double( 0, plotCenter-3, plotWidth, 6 );
				} 

			} //setlines
			
		} //end data plot subclass
		
	} //end data subclass
	
}
