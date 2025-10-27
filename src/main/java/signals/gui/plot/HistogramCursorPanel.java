package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import signals.core.Constants;
import signals.core.Core;
import signals.core.Function;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;
import signals.gui.ResizablePane;
import signals.operation.ArrayMath;
import signals.operation.Histogram;

@SuppressWarnings("serial")
public class HistogramCursorPanel extends PlotCursorPanel implements ParameterUser, ResizablePane {

	int numBins;
	double increment; 
	ParameterEditor numBinsEditor; 
	double[] rawDataR, rawDataI;  
	double[] rawExtrema; 
	String descriptor; 

	//means and stdevs
	double meanR, meanI, stdevR, stdevI; 

	boolean hasImaginary; 

	public static final int NUM_BINS_INDEX = 0; 

	public HistogramCursorPanel(Dimension plotSize) {
		super(plotSize);
		numBins = 30; 
		graphic.setDefaultPlotStyle( Constants.PlotStyle.HISTOGRAM );
	}

	public void setPlot( Function function ) {

		rawDataR = function.getReal();
		rawDataI = function.getImaginary();
		descriptor = function.getCompactDescriptor();

		double[] rawExtremaI = PlottingMath.extrema(rawDataI); 
		hasImaginary = rawExtremaI[0] != 0 && rawExtremaI[1] != 0; //only use imaginary part if nonzero extrema 
		if( hasImaginary ) rawExtrema = PlottingMath.extrema(rawDataR, rawExtremaI);
		else rawExtrema = PlottingMath.extrema(rawDataR);

		refreshHistogram(); 

		if( hasToolBar ) { 
			
			NumberFormat format = Core.getDisplayOptions().getFormat(); 

			Color realColor = graphic.getDataSequences().get(0).getColor(); 
			meanR = ArrayMath.mean(rawDataR);
			stdevR = ArrayMath.stdev(rawDataR);
			JLabel rmlabel = new JLabel( "    \u03BC\u1D63: " + format.format(meanR) );
			rmlabel.setForeground(realColor); 
			toolBar.add( rmlabel ); 
			JLabel rsdlabel = new JLabel( "    \u03C3\u1D63: " + format.format(stdevR) ); 
			rsdlabel.setForeground(realColor); 
			toolBar.add( rsdlabel ); 		
			toolBar.add( Box.createHorizontalGlue()); 

			if( hasImaginary ) {

				Color imagColor = graphic.getDataSequences().get(1).getColor(); 
				meanI = ArrayMath.mean(rawDataI);
				stdevI = ArrayMath.stdev(rawDataI); 
				JLabel imlabel = new JLabel( "    \u03BC\u1D62: " + format.format(meanI) ); 
				imlabel.setForeground(imagColor);
				toolBar.add( imlabel ); 
				JLabel isdlabel = new JLabel( "    \u03C3\u1D62: " + format.format(stdevI) ); 
				isdlabel.setForeground(imagColor);
				toolBar.add( isdlabel );
				toolBar.add( Box.createHorizontalGlue()); 

			} 
		}
		
		refreshTitleBar(); 
	}

	public void refreshHistogram() {

		indices = Histogram.binIndices(numBins, rawExtrema[0], rawExtrema[1]);		
		increment = (rawExtrema[1]-rawExtrema[0])/(double)numBins;

		double[] histDataR = Histogram.histogram( rawDataR, numBins, rawExtrema[0], rawExtrema[1] ); 
		graphic.add( indices, histDataR, "Real Histogram{ " + descriptor + "}" );
		
		if( hasImaginary ) {

			double[] histDataI = Histogram.histogram( rawDataI, numBins, rawExtrema[0], rawExtrema[1] );
			graphic.add( indices, histDataI, "Imaginary Histogram{ " + descriptor + "}" );
		}

		refreshTitleBar();

		if( hasToolBar ) {

			leftAction.setEnabled( false ); 
			rightAction.setEnabled( false ); 
		}

	}

	public void setCursorLabels() {

		NumberFormat formatter = Core.getDisplayOptions().getFormat();

		ArrayList<PlotGraphic.DataSequence> dataSequences = graphic.getDataSequences();
		Iterator<JLabel> labelsIter = labels.iterator();

		for( PlotGraphic.DataSequence sequence : dataSequences ) {

			labelsIter.next().setText( sequence.getName() +": (bin[" + 
					formatter.format(sequence.xData[cursorIndex]) + 
					" : " + formatter.format(sequence.xData[cursorIndex]+increment)  +  "], " +
					formatter.format(sequence.yData[cursorIndex]) + ")" );
		}


	}

	public void setCursorLocation( double location ) {

		cursorIndex = (int)((location - indices[0])/increment);
		graphic.setCursorLocation( indices[cursorIndex] + increment/2.0 );
		setCursorLabels();
	}

	public void cursorActionCode() {

		graphic.addMouseListeners();
		cursorIndex = indices.length / 2 - 1; //center index
		graphic.setCursorLocation( indices[cursorIndex] + increment/2.0 );
		graphic.setCursorOn( true );
		cursorModeOn = true;

		leftAction.setEnabled( true ); 
		rightAction.setEnabled( true ); 

		setCursorLabels();

	}

	public void moveLeft() {

		int newCursorIndex = cursorIndex - 1; 
		if( newCursorIndex >= 0 ) {

			cursorIndex = newCursorIndex; 
			graphic.setCursorLocation( indices[cursorIndex] + increment/2.0 );
			setCursorLabels(); 
		} 
	}

	public void moveRight() {

		int newCursorIndex = cursorIndex + 1; 
		if( newCursorIndex < indices.length ) {

			cursorIndex = newCursorIndex; 
			graphic.setCursorLocation( indices[cursorIndex] + increment/2.0 );
			setCursorLabels(); 
		}

	}

	public void showToolBar() {

		super.showToolBar(); 

		//create spinner for number of bins
		SpinnerNumberModel model = new SpinnerNumberModel( numBins, 2, 10000, 1); 
		numBinsEditor = new ParameterEditor(3, model, NUM_BINS_INDEX, this); 
		JLabel numBinsLabel = new JLabel( "Number of Bins: "); 
		numBinsLabel.setLabelFor(numBinsEditor); 
		toolBar.add(numBinsLabel);
		toolBar.add(numBinsEditor);
		toolBar.add( Box.createHorizontalGlue() ); 

	}

	public void parameterChanged(int index, String newVal) {

		int newNumBins = Integer.parseInt(newVal); 

		if( newNumBins > 1 ) {

			numBins = newNumBins; 
			graphic.removeAll(); 
			refreshHistogram();
		}

	}

}
