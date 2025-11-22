package signals.gui.datagenerator;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import signals.core.Constants;
import signals.core.FunctionTerm1D;
import signals.functionterm.NoiseFunctionTerm1D;
import signals.gui.datagenerator.EditAnalyticFunctionTermPanel;
import signals.gui.datagenerator.GUIEventBroadcaster;
import signals.gui.datagenerator.GUIEvent.Descriptor;
import signals.gui.plot.PlottingGraphics;
import signals.gui.plot.PlottingMath;
import signals.operation.Histogram;

@SuppressWarnings("serial")
public class HistogramInteractivePlot extends AnalyticInteractivePlot {

	@Override
	public void GUIEventOccurred(GUIEvent e) {
		//ignore GUI events
	}

	public HistogramInteractivePlot(GUIEventBroadcaster broadcaster,
			Descriptor modifiedDescriptor, EditAnalyticFunctionTermPanel editor) {
		super(broadcaster, modifiedDescriptor, editor);
		graphic = new HistogramPlotGraphic( null, null );
	}

	public void setRawDimension(int rawDimension) {
		
		((HistogramPlotGraphic)graphic).setRawDimension( rawDimension );
	}
	
	public void setBorder() {};
	
	public void dragCenter( MouseEvent e ) {

		PlottingMath plottingMath = graphic.getPlottingMath();
		double newCenter = plottingMath.plotToMathX( e.getX() );

		if ( newCenter >= plottingMath.getXWindowMax() ) {

			double oldCenter = currentFunction.getCenter(); 
			newCenter = oldCenter + PlottingMath.windowNudgeScalar*(Math.abs(oldCenter));
			plottingMath.setXWindowMax(newCenter);

		} else if( newCenter <= plottingMath.getXWindowMin() ) {

			double oldCenter = currentFunction.getCenter(); 
			newCenter = oldCenter - PlottingMath.windowNudgeScalar*(Math.abs(oldCenter));
			plottingMath.setXWindowMin(newCenter);

		} 
		currentFunction.setCenter(newCenter); 
		repaint();
		editor.setCenter( newCenter );
	}

	public void dragAmplitude( MouseEvent e ) {

		PlottingMath plottingMath = graphic.getPlottingMath();
		double initAmplitude = plottingMath.plotToMathX( e.getX() );
		double center = currentFunction.getCenter();
		if ( initAmplitude > center ) {

			double newAmplitude = 0; 
			if ( initAmplitude >= plottingMath.getXWindowMax() ) {
				
				double oldAmplitude = currentFunction.getAmplitude(); 
				newAmplitude = oldAmplitude + PlottingMath.windowNudgeScalar*(Math.abs(oldAmplitude));
				plottingMath.setXWindowMax(center+newAmplitude);
				
			} else {
				newAmplitude = initAmplitude - center;
			}

			currentFunction.setAmplitude(newAmplitude); 
			repaint();
			editor.setAmplitude( newAmplitude );

		}
	}
	
	public void updateWidth() {
		hasWidth = false; 
	}
	
	public class HistogramPlotGraphic extends AnalyticInteractivePlotGraphic {
		
		public static final int NUM_BINS = 32;
		
		int rawDimension;  

		public HistogramPlotGraphic(FunctionTerm1D currentFunction, double[] indices) {
			super(currentFunction, indices);
			setPlotStyle( Constants.PlotStyle.HISTOGRAM );
		}

		public void createTicksX() {
			
			largePlottingMath.createTicksX();
		}
		
		/**
		 * @param rawIndices the rawIndices to set
		 */
		public void setRawDimension(int rawDimension) {
			this.rawDimension = rawDimension;
			setDirty(); 
			repaint();
		}

		/* (non-Javadoc)
		 * @see signals.gui.FunctionTerm1DThumbnailGraphic#createData()
		 */
		@Override
		public void createData() {
			
			double[] rawData = ((NoiseFunctionTerm1D)function).create( rawDimension );
			double[] rawExtrema = PlottingMath.extrema(rawData);
			
			if( ((NoiseFunctionTerm1D)function).isIntegerDefined() ) {
				
				data = Histogram.integerHistogram(rawData, (int)rawExtrema[0], (int)rawExtrema[1] ); 
				indices = Histogram.integerBinIndices((int)rawExtrema[0], (int)rawExtrema[1]);
				
			} else {
				
				data = Histogram.histogram( rawData, NUM_BINS, rawExtrema[0], rawExtrema[1] ); 
				indices = Histogram.binIndices(NUM_BINS, rawExtrema[0], rawExtrema[1]);
			}
		}
		
		public void setLines() {

			int plotHeight = getLargeDimension().height;
			
			if( hasAmplitude ) {
				double plotAmplitude = largePlottingMath.mathToPlotX(center+amplitude);
				amplitudeLine = PlottingGraphics.yAxis( plotHeight, plotAmplitude, OFFSET ); 
				amplitudeBox = new Rectangle2D.Double( plotAmplitude-3, 0, 6, plotHeight );
			}

			double plotCenter = 0; 
			
			if( hasCenter ) {
			
				plotCenter = largePlottingMath.mathToPlotX(center);
				centerLine = PlottingGraphics.yAxis( plotHeight, plotCenter, OFFSET );
				centerBox = new Rectangle2D.Double( plotCenter-3, 0, 6, plotHeight );
			} 

		} //setLines
		
	}//end histogram plot graphic subclass

}
