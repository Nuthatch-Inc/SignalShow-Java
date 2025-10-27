package signals.demo;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;

import signals.core.Function;
import signals.core.Function2D;
import signals.functionterm.Apertures;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;
import signals.gui.plot.ImageDisplayPanel;
import signals.gui.plot.ImageRadiusDisplayPanel;
import signals.operation.ArrayMath;
import signals.operation.ArrayUtilities;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public class FrequencyLocation2D extends JPanel implements ParameterUser {
	
	ImageRadiusDisplayPanel frequencyDisplayPanel; 
	ImageDisplayPanel spatialDisplayPanel; 
	double innerRadius, outerRadius; 
	ParameterEditor innerRadiusEditor, outerRadiusEditor; 
	int dimensionX, dimensionY; 
	JCheckBox dcCheckBox, lfCheckBox, mfCheckBox, hfCheckBox, absvalCheckBox; 
	
	double[] fdreal, fdimag; 
	double[][] lpfiltered, bandfiltered, hpfiltered; 
	double[] zero; 
	
	JRadioButton realButton, imagButton; 
	
	public static final int INNER = 0; 
	public static final int OUTER = 1; 
	
	public FrequencyLocation2D( Function function ) {
		
		JPanel originalPanel = new JPanel(); 
		ImageDisplayPanel realOriginal = new ImageDisplayPanel(new Dimension( 128, 128 )); 
		ImageDisplayPanel imagOriginal = new ImageDisplayPanel(new Dimension( 128, 128 ));
		originalPanel.add( realOriginal ); 
		originalPanel.add( imagOriginal );
		originalPanel.setBorder( 
				BorderFactory.createTitledBorder( "Original Function: Real and Imaginary")); ;
		
		JPanel frequencyPanel = new JPanel(); 
		
		frequencyDisplayPanel = new ImageRadiusDisplayPanel(new Dimension( 256, 256 )); 
		frequencyPanel.setBorder( 
				BorderFactory.createTitledBorder( "Frequency Domain Filtering"));
		frequencyPanel.add( frequencyDisplayPanel); 
		
		JPanel spatialPanel = new JPanel( ); 
		
		spatialDisplayPanel = new ImageDisplayPanel(new Dimension(256, 256)); 
		
		spatialPanel.setBorder( 
				BorderFactory.createTitledBorder( "Spatial Domain Reconstruction"));
		spatialPanel.add( spatialDisplayPanel); 
		
		absvalCheckBox = new JCheckBox( "display absolute value" ); 
		spatialPanel.add(absvalCheckBox); 
		absvalCheckBox.setSelected(true); 
		
		double[] real = function.getReal(); 
		double[] imag = function.getImaginary();
				
		Function2D function2D = (Function2D) function; 
		
		dimensionX = function2D.getDimensionX(); 
		dimensionY = function2D.getDimensionY();
		
		realOriginal.setIndices(dimensionX, dimensionY, true); 
		imagOriginal.setIndices(dimensionX, dimensionY, true); 
		realOriginal.display(real); 
		imagOriginal.display(imag); 
		
		innerRadius = 8; 
		outerRadius = 24; 
		
		zero = new double[dimensionX*dimensionY];
		
		//display FFT in frequency display panel 
		double[][] fd = Transforms.computeFFT2D(real, imag, true, false, 
				dimensionX, dimensionY, Transforms.NORMALIZE_NONE); 
		
		fdreal = fd[0]; 
		fdimag = fd[1]; 
	
		double[] logMagnitude = ArrayMath.log10(ArrayMath.magnitude(fdreal, fdimag));
		
		frequencyDisplayPanel.setIndices(dimensionX, dimensionY, true);
		spatialDisplayPanel.setIndices(dimensionX, dimensionY, true); 
		
		frequencyDisplayPanel.display(logMagnitude);
		
		frequencyDisplayPanel.setInnerRadius(innerRadius); 
		frequencyDisplayPanel.setOuterRadius(outerRadius);
		
		SpinnerNumberModel innerSpinner = new SpinnerNumberModel( innerRadius, 0, 10000, 1 ); 
		SpinnerNumberModel outerSpinner = new SpinnerNumberModel( outerRadius, 0, 10000, 1 ); 

		innerRadiusEditor = new ParameterEditor( 3, innerSpinner, INNER, this ); 
		outerRadiusEditor = new ParameterEditor( 3, outerSpinner, OUTER, this ); 
		
		dcCheckBox = new JCheckBox( "DC Component"); 
		lfCheckBox = new JCheckBox( "Low Frequencies (Not Including DC Component)"); 
		mfCheckBox = new JCheckBox( "Mid Frequencies (Band Pass)"); 
		hfCheckBox = new JCheckBox( "High Frequencies"); 
		
		JPanel cbPanel = new JPanel();
		
		cbPanel.setLayout( new GridLayout( 4, 1, 5, 5 ));
		
		cbPanel.add(dcCheckBox); 
		cbPanel.add(lfCheckBox); 
		cbPanel.add(mfCheckBox); 
		cbPanel.add(hfCheckBox); 
		
		cbPanel.setBorder( 
				BorderFactory.createTitledBorder( "Frequencies to Display in Reconstruction"));
	
	
		lfCheckBox.setSelected(true); 
		mfCheckBox.setSelected(true); 
		hfCheckBox.setSelected(true); 
		
		realButton = new JRadioButton("Show Real"); 
		imagButton = new JRadioButton("Show Imaginary"); 
		ButtonGroup partGroup = new ButtonGroup(); 
		partGroup.add(realButton); 
		partGroup.add(imagButton); 
		realButton.setSelected(true); 
		JPanel partPanel = new JPanel(); 
		partPanel.add( realButton ); 
		partPanel.add( imagButton ); 
		
		spatialPanel.add( partPanel ); 
		spatialPanel.add( originalPanel );
		
		ActionListener buttonListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				displayReconstruction(); 
			}
		}; 
		
		realButton.addActionListener(buttonListener); 
		imagButton.addActionListener(buttonListener); 
		
		ActionListener cblistener = new ActionListener() {

			public void actionPerformed( ActionEvent e) {
				calculate(); 
			}
		
		}; 
	
		dcCheckBox.addActionListener( cblistener );
		lfCheckBox.addActionListener( cblistener );
		mfCheckBox.addActionListener( buttonListener );
		hfCheckBox.addActionListener( buttonListener );
		absvalCheckBox.addActionListener(buttonListener); 
		
		JPanel ftoolbar = new JPanel(); 
		ftoolbar.setLayout( new GridLayout( 2, 2, 5, 5 )); 
		ftoolbar.add( new JLabel( "Low-Frequency Cutoff: "));
		ftoolbar.add( innerRadiusEditor );
		ftoolbar.add( new JLabel( "Mid-Frequency Cutoff: "));
		ftoolbar.add( outerRadiusEditor ); 
		
		
		frequencyPanel.add( ftoolbar); 
		frequencyPanel.add( cbPanel ); 

		Dimension panelSize = new Dimension( 450, 550 ); 
		frequencyPanel.setPreferredSize( panelSize );
		spatialPanel.setPreferredSize( panelSize ); 
		add( frequencyPanel); 
		add( spatialPanel ); 
		
		calculate(); 

	}
	
	public void calculate() { //todo: subtract dc component checkbox
		
		double[] lpfilter = getlpfilter(false); 
		double[] midfilter = getmidfilter(); 
		double[] highfilter = ArrayUtilities.constant(lpfilter.length, 1); 
		
		double[] bandfilter = ArrayMath.subtract(midfilter, lpfilter); 
		double[] hpfilter = ArrayMath.subtract(highfilter, midfilter); 
		
		lpfilter = getlpfilter(!dcCheckBox.isSelected()); 

		double[] lpfilteredfd_real = ArrayMath.modulate(lpfilter, fdreal); 
		double[] bandfilteredfd_real = ArrayMath.modulate(bandfilter, fdreal); 
		double[] hpfilteredfd_real = ArrayMath.modulate(hpfilter, fdreal); 
 		
		double[] lpfilteredfd_imag = ArrayMath.modulate(lpfilter, fdimag); 
		double[] bandfilteredfd_imag = ArrayMath.modulate(bandfilter, fdimag); 
		double[] hpfilteredfd_imag = ArrayMath.modulate(hpfilter, fdimag); 
		
		lpfiltered = Transforms.computeFFT2D(lpfilteredfd_real, lpfilteredfd_imag, true, true, 
				dimensionX, dimensionY, Transforms.NORMALIZE_N); 
		bandfiltered = Transforms.computeFFT2D(bandfilteredfd_real, bandfilteredfd_imag, true, true, 
				dimensionX, dimensionY, Transforms.NORMALIZE_N); 
		hpfiltered = Transforms.computeFFT2D(hpfilteredfd_real, hpfilteredfd_imag, true, true, 
				dimensionX, dimensionY, Transforms.NORMALIZE_N);  
		
		displayReconstruction(); 
	
	}
	
	public void displayReconstruction() {
		
		int idx = realButton.isSelected() ? 0 : 1; 
		double[] hfdisplay = hfCheckBox.isSelected() ? hpfiltered[idx] : zero; 
		double[] mfdisplay = mfCheckBox.isSelected() ? bandfiltered[idx] : zero; 
		double[] lfdisplay = lfCheckBox.isSelected() ? lpfiltered[idx] : zero; 
		
		if( absvalCheckBox.isSelected() ) {
			
			hfdisplay = ArrayMath.abs(hfdisplay); 
			mfdisplay = ArrayMath.abs(mfdisplay); 
			lfdisplay = ArrayMath.abs(lfdisplay); 
			
		}
		
		spatialDisplayPanel.display( hfdisplay, mfdisplay, lfdisplay);
	}
	
	public double[] getlpfilter( boolean subtractDC ) {
		
		//low frequencies 
		double[][] array = new double[dimensionY][dimensionX]; 
		Apertures.setFilledAperture(array, dimensionX, dimensionY, true, 0, 0, innerRadius, 1); 
		
		if( subtractDC ) {
			
			array[dimensionY/2-1][dimensionX/2] = 0; 
		}
		
		return ArrayUtilities.flatten(array);
	}
	
	public double[] getmidfilter() {
		
		//low frequencies 
		double[][] array = new double[dimensionY][dimensionX]; 
		Apertures.setFilledAperture(array, dimensionX, dimensionY, true, 0, 0, outerRadius, 1); 
		return ArrayUtilities.flatten(array);
	}

	public void parameterChanged(int index, String newValue) {

		double newRadius = Double.parseDouble(newValue); 

		switch( index ) { 

		case INNER:

			if( newRadius > 0 && newRadius < outerRadius )  {
				innerRadius = newRadius; 
				frequencyDisplayPanel.setInnerRadius(innerRadius); 
				calculate(); 
			}

			break; 

		case OUTER: 

			if( newRadius > innerRadius ) {
				outerRadius = newRadius; 
				frequencyDisplayPanel.setOuterRadius(outerRadius); 
				calculate();  
			}

			break; 

		}

	}

}
