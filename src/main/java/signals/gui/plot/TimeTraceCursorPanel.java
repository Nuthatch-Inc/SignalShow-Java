package signals.gui.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;

import signals.core.Constants.PlotStyle;
import signals.operation.ArrayMath;

@SuppressWarnings("serial")
public class TimeTraceCursorPanel extends PlotCursorPanel {

	double[] data;
	double[] rotatedData; 
	int numPoints;  
	boolean rotated; 
	int stepIndex; 

	double[] quarterIndices, halfIndices, threeQuartersIndices; 

	public static final int MAX_NUM_PTS = 40; 

	public TimeTraceCursorPanel(Dimension plotSize, boolean rotated) {
		super(plotSize);
		this.rotated = rotated; 
		labels = new ArrayList<JLabel>();
	}

	public void setFunction( double[] data ) {

		((ArgandPlotGraphic)graphic).initialize(); 
		this.data = data; 
		if (rotated) data = ArrayMath.reverse(data); 
		cursorModeOn = true;
		cursorIndex = 0;
		numPoints = data.length; 
		rotatedData = new double[numPoints];
		createIndices(); 
		createRotatedData(); 
		graphic.setDefaultPlotStyle(PlotStyle.SMOOTH); 
		if( rotated )graphic.add(rotatedData, indices, "" ); 
		else graphic.add(indices, rotatedData, "" );
		graphic.getDataSequences().get(0).setColor(new Color( 150, 150, 150 )); 

		if( rotated ) {

			((ArgandPlotGraphic)graphic).setCursorLocation( rotatedData[0], 0 );

		} else {

			((ArgandPlotGraphic)graphic).setCursorLocation( 0, rotatedData[0] );

		}

		graphic.setCursorOn( true );
		stepIndex = 0; 
	}

	public void createIndices() {

		indices = new double[numPoints];
		quarterIndices = new double[numPoints];
		halfIndices = new double[numPoints];
		threeQuartersIndices = new double[numPoints];

		for( int i = 0; i < numPoints; i++ ) {

			indices[i] = rotated? -i : i; 
			quarterIndices[i] = rotated? -(i+.25) : (i+.25); 
			halfIndices[i] = rotated? -(i+.5) : (i+.5); 
			threeQuartersIndices[i] = rotated? -(i+.75) : (i+.75); 

		}

	}

	public void createGraphic( Dimension plotSize ) {

		graphic = new TimeTracePlotGraphic( plotSize );
	}

	public void createRotatedData() {

		for( int i = 0; i < numPoints; i++ ) { //when i = 0, correct

			int rotatedIndex = (cursorIndex + i) % (data.length); 
			rotatedData[i] = data[rotatedIndex]; 
		}

		rotatedData = ArrayMath.reverse(rotatedData); 

	}

	public void setCursorLabels() {

	}

	public void moveLeft() {

	}

	public void moveRight() {
		
		stepIndex = ( stepIndex + 1 ) % 4;

		switch( stepIndex ) {

		case 0: 

			int newIndex = cursorIndex == (data.length-1) ? 0 : cursorIndex + 1; //increase cursor index as moving right
			cursorIndex = newIndex; 
			createRotatedData(); 
			if( rotated ) {

				graphic.getDataSequences().get(0).setYData(indices, false);
				graphic.getDataSequences().get(0).setXData(rotatedData, false);
				((ArgandPlotGraphic)graphic).setCursorLocation( rotatedData[0], 0 );
	
			}
			else {

				graphic.getDataSequences().get(0).setXData(indices, false);
				graphic.getDataSequences().get(0).setYData(rotatedData, false); 
				((ArgandPlotGraphic)graphic).setCursorLocation( 0, rotatedData[0] );
			}

			break; 

		case 1: 

			if( rotated ) {

				graphic.getDataSequences().get(0).setYData(quarterIndices, false);
				

			} else {


				graphic.getDataSequences().get(0).setXData(quarterIndices, false);
			
			}

			break; 

		case 2: 

			if( rotated ) {

				graphic.getDataSequences().get(0).setYData(halfIndices, false);
			

			} else {


				graphic.getDataSequences().get(0).setXData(halfIndices, false);
				
			}

			break; 


		case 3: 

			if( rotated ) {

				graphic.getDataSequences().get(0).setYData(threeQuartersIndices, false);
		

			} else {


				graphic.getDataSequences().get(0).setXData(threeQuartersIndices, false);
				
			}


			break; 

		}
		
		((TimeTracePlotGraphic)graphic).setDirtyCursor();  

	}

}
