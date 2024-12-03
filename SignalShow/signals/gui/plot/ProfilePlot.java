package signals.gui.plot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;

import signals.core.Constants;
import signals.core.Core;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.SourceData1D;
import signals.core.SourceData2D;
import signals.core.Constants.Part;
import signals.core.Constants.PlotStyle;
import signals.gui.ParameterEditor;
import signals.gui.ParameterUser;
import signals.gui.ResizablePane;

@SuppressWarnings("serial")
public class ProfilePlot extends JPanel implements ParameterUser, ActionListener, ResizablePane {

	ImageCursorPanel imagePanel;
	PlotCursorPanel plotPanel; 
	
	Function function2D; 
	
	Constants.Part selectedPart; 
	
	ImageProfileCutGraphic graphic; 
	
	JRadioButton xButton, yButton; 
	
	int firstIndexX, firstIndexY; 
	
	public static final int X_BUTTON = 0; 
	public static final int Y_BUTTON = 1;
	public static final int REAL_BUTTON = 2; 
	public static final int IMAG_BUTTON = 3;
	public static final int MAG_BUTTON = 4; 
	public static final int PHASE_BUTTON = 5;
	public static final int SAVE_BUTTON = 6;
	
	public static final int X0_PARAM = 0; 
	public static final int RUN_PARAM = 1; 
	public static final int Y0_PARAM = 2; 
	public static final int RISE_PARAM = 3; 
	
	ParameterEditor runEditor, x0Editor, riseEditor, y0Editor; 
	
	int x0, run, y0, rise; 
	
	public ProfilePlot( Function function2D )  {
		
		this.function2D = function2D; 
		
		selectedPart = Constants.Part.REAL_PART; 
		
		Dimension graphicSize = new Dimension( 200, 200); 
		graphic = new ImageProfileCutGraphic(graphicSize); 
		imagePanel = new ImageCursorPanel(graphicSize);
		imagePanel.setGraphic(graphic);
		imagePanel.setImage( new SourceData2D( function2D, selectedPart ) );
		
		plotPanel = new PlotCursorPanel( new Dimension( 300, 250 ));
		plotPanel.setDefaultPlotStyle(PlotStyle.SMOOTH);
		
		Function defaultData = ((Function2D)function2D).getYProfile( 0, 0 ); 
		plotPanel.addPlot( new SourceData1D( defaultData, selectedPart ) ); 
		
		int dimX = ((Function2D)function2D).getDimensionX();
		int dimY = ((Function2D)function2D).getDimensionY();
		
		firstIndexX = 0; 
		firstIndexY = 0; 
		boolean zeroCentered = function2D.isZeroCentered(); 
		if( zeroCentered ) { 
			firstIndexX = -dimX/2; 
			firstIndexY = -dimY/2; 
		}
		
		x0 = 0; 
		run = 0; 
		y0 = 0; 
		rise = 0; 
		
		SpinnerNumberModel x0_model = new SpinnerNumberModel(x0, -10000, 10000, 1); 
		x0Editor = new ParameterEditor( 3, x0_model, X0_PARAM, this ); //3 columns 
		x0Editor.setEnabled(false); 
		
		SpinnerNumberModel run_model = new SpinnerNumberModel(run, -10000, 10000, 1); 
		runEditor = new ParameterEditor( 3, run_model, RUN_PARAM, this ); //3 columns 
		runEditor.setEnabled(false);
		
		SpinnerNumberModel y0_model = new SpinnerNumberModel(y0, -10000, 10000, 1); 
		y0Editor = new ParameterEditor( 3, y0_model, Y0_PARAM, this ); //3 columns 
		
		SpinnerNumberModel rise_model = new SpinnerNumberModel(rise, -10000, 10000, 1); 
		riseEditor = new ParameterEditor( 3, rise_model, RISE_PARAM, this ); //3 columns 
		
		graphic.setYLine(0, - firstIndexY);
		
		//part selection radio buttons
		JRadioButton realButton = new JRadioButton( "Real Part"); 
		JRadioButton imagButton = new JRadioButton( "Imaginary Part"); 
		JRadioButton magButton = new JRadioButton( "Magnitude"); 
		JRadioButton phaseButton = new JRadioButton( "Phase"); 
		realButton.setSelected(true); 
		realButton.addActionListener(this);
		imagButton.addActionListener(this);
		magButton.addActionListener(this);
		phaseButton.addActionListener(this);
		
		realButton.setActionCommand(""+REAL_BUTTON); 
		imagButton.setActionCommand(""+IMAG_BUTTON); 
		magButton.setActionCommand(""+MAG_BUTTON); 
		phaseButton.setActionCommand(""+PHASE_BUTTON); 
		
		ButtonGroup partGroup = new ButtonGroup(); 
		partGroup.add( realButton ); 
		partGroup.add( imagButton ); 
		partGroup.add( magButton ); 
		partGroup.add( phaseButton ); 
		
		JPanel partPanel = new JPanel(); 
		partPanel.setLayout(new GridLayout(4, 1, 5, 5 )); 
		partPanel.add( realButton ); 
		partPanel.add( imagButton ); 
		partPanel.add( magButton ); 
		partPanel.add( phaseButton ); 
		
		
		//radio buttons
		xButton = new JRadioButton( " x = " ); 
		yButton = new JRadioButton( " y = " ); 
		xButton.setActionCommand(""+X_BUTTON); 
		yButton.setActionCommand(""+Y_BUTTON);
		xButton.addActionListener(this); 
		yButton.addActionListener(this);
		yButton.setSelected(true);
		
		ButtonGroup eqButtonGroup = new ButtonGroup(); 
		eqButtonGroup.add( xButton ); 
		eqButtonGroup.add( yButton );
		
		//layout
		JPanel upperPanel = new JPanel(); 
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(imagePanel, BorderLayout.CENTER); 
		
		JPanel xPanel = new JPanel(); 
		xPanel.setLayout( new BoxLayout( xPanel, BoxLayout.LINE_AXIS)); 
		xPanel.add( xButton );
		xPanel.add( runEditor ); 
		xPanel.add( new JLabel(" y + ") ); 
		xPanel.add( x0Editor );
		
		JPanel yPanel = new JPanel(); 
		yPanel.setLayout( new BoxLayout( yPanel, BoxLayout.LINE_AXIS)); 
		yPanel.add( yButton );
		yPanel.add( riseEditor ); 
		yPanel.add( new JLabel(" x + ") ); 
		yPanel.add( y0Editor );
		
		JButton saveButton = new JButton( "Export Profile as New 1D Function"); 
		saveButton.setActionCommand(""+SAVE_BUTTON); 
		saveButton.addActionListener(this); 

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.PAGE_AXIS ));
		buttonPanel.add( Box.createVerticalGlue() ); 
		buttonPanel.add( partPanel );
		buttonPanel.add( Box.createVerticalGlue() ); 
		buttonPanel.add( yPanel ); 
		buttonPanel.add( Box.createVerticalGlue() ); 
		buttonPanel.add( xPanel );
		buttonPanel.add( Box.createVerticalGlue() ); 
		buttonPanel.add(saveButton); 
		buttonPanel.add( Box.createVerticalGlue() );
		buttonPanel.setBorder(Core.getBorders().getEtchedBufferBorder());
		
		upperPanel.add( buttonPanel, BorderLayout.EAST); 
		
		setLayout( new BorderLayout() ); 
		add( upperPanel, BorderLayout.CENTER );
		
		
		add( plotPanel, BorderLayout.SOUTH );
	}
	
	public void setXLine() {
		
		plotPanel.removeAll(); 
		Function data = ((Function2D)function2D).getXProfile( run, x0 );
		plotPanel.addPlot( new SourceData1D( data, selectedPart ) );  
		graphic.setXLine(run, x0 - firstIndexX);
	}
	
	public void setYLine() {
		
		plotPanel.removeAll();  
		Function data = ((Function2D)function2D).getYProfile( rise, y0 );
		plotPanel.addPlot( new SourceData1D( data, selectedPart ) ); 
		graphic.setYLine(rise, y0 - firstIndexY);
	}

	public void setSelectedPart( Part part ) {
		
		selectedPart = part; 
		imagePanel.removeAll();
		imagePanel.setImage( new SourceData2D( function2D, selectedPart ) );
		if( xButton.isSelected() ) {
			
			setXLine(); 
			
		} else {
			
			setYLine(); 
		}
	}
	
	public void parameterChanged(int index, String newVal) {
	
		
		switch( index ) {
		
		case X0_PARAM: 
			
			x0 = Integer.parseInt(newVal); 
			setXLine(); 
			
			break; 
			
		case RUN_PARAM: 
			
			run = Integer.parseInt(newVal); 
			setXLine(); 
			
			break; 
			
		case Y0_PARAM: 
			
			y0 = Integer.parseInt(newVal);
			setYLine(); 
			
			break; 
			
		case RISE_PARAM: 
			
			rise = Integer.parseInt(newVal);
			setYLine(); 
			
			break; 
			
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		
		switch( Integer.parseInt(e.getActionCommand()) ) {
		
		case X_BUTTON: 
			
			riseEditor.setEnabled( false ); 
			y0Editor.setEnabled(false); 
			
			runEditor.setEnabled( true ); 
			x0Editor.setEnabled(true); 
			
			setXLine(); 
			
			break; 
			
		case Y_BUTTON: 
			
			riseEditor.setEnabled( true ); 
			y0Editor.setEnabled(true);
			
			runEditor.setEnabled( false ); 
			x0Editor.setEnabled(false); 
			
			setYLine(); 
			
			break; 
			
		case REAL_BUTTON: 
			
			setSelectedPart(Part.REAL_PART); 
			
			break; 
			
		case IMAG_BUTTON: 
			
			setSelectedPart(Part.IMAGINARY_PART);
			
			break; 
			
		case MAG_BUTTON: 
			
			setSelectedPart(Part.MAGNITUDE);
			
			break; 
			
		case PHASE_BUTTON: 
			
			setSelectedPart(Part.PHASE);
			
			break; 
			
		case SAVE_BUTTON: 
			
			Function data = null; 
			if( xButton.isSelected() ) {
				
				data = ((Function2D)function2D).getXProfile( run, x0 );
				
			} else {
				
				data = ((Function2D)function2D).getYProfile( rise, y0 );
			}
			
			Core.getFunctionList().addItem(data);
			
			break; 
		
		}
		
	}

	public void sizeChanged() {
	
		plotPanel.sizeChanged(); 
	}
	
}
