package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import signals.core.Core;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.FunctionPart1D;
import signals.gui.plot.ArgandDiagram;
import signals.gui.plot.Function1DOverviewPanel;
import signals.gui.plot.Indices;
import signals.io.ResourceLoader;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public abstract class Function1DToolBar extends FunctionToolBar {

	JComboBox dimensionSelector;
	CreateFunctionPart1DPanel editor; 
	JCheckBox zeroCenteredCheckBox;
	JTextField nameTextField; 
	public static final String ARGAND = "ARGAND";
	JToggleButton argandButton; 

	public Function1DToolBar( Function function ) {

		super( function ); 
		createGlobalOptions();
	}

	public Function1DToolBar() {

		super();
		createGlobalOptions();

	}
	
	public void selectButton(String buttonCode) {
		
		if( buttonCode.equals(PLOT) ) {
			
			plotButton.setSelected(true); 
			plot(); 
			
		} else if( buttonCode.equals(EDIT_A)) {
			
			editPartAButton.setSelected(true); 
			editPartA();
			
		} else if( buttonCode.equals(EDIT_B)) {
			
			editPartBButton.setSelected(true); 
			editPartB();
			
		} else if( buttonCode.equals(FFT)) {
			
			fftButton.setSelected(true); 
			plotFFT(); 
			
		} else if( buttonCode.equals(ARGAND)) {
			
			argandButton.setSelected(true); 
			plotArgand();
		}
		
	}

	public void createGlobalOptions() {

		dimensionSelector = new JComboBox( Core.getFunctionCreationOptions().getAvailableDimensions() ); 
		zeroCenteredCheckBox = new JCheckBox( "Zero-Centered"); 
	}

	public void editPartA() {

		openTab = EDIT_A; 
		showEditor( newStateA, ((Function1D)function).getPartA() ); 
		newStateA = false; 
		setShowableState( openTab ); 
	}

	public void editPartB() {

		openTab = EDIT_B; 
		showEditor( newStateB, ((Function1D)function).getPartB() ); 
		newStateB = false; 
		setShowableState( openTab ); 
	}

	public void showEditor( boolean newState, FunctionPart1D part ) {

		int dimension = ((Function1D)function).getDimension(); 
		double[] indices = Indices.indices1D( dimension, function.isZeroCentered() ); 
		GUIEventBroadcaster broadcaster = new GUIEventBroadcaster(); 
		editor = new CreateFunctionPart1DPanel(broadcaster, newState, isEditable()); 
		editor.setIndices(indices);

		if( !newState ) {

			editor.setFunctionPart1D(part);
		}

		broadcaster.addGUIEventListener(this);

		//name box
		nameTextField = new JTextField(function.getDescriptor(), 20 );

		nameTextField.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				textChanged(); 
			}

			public void insertUpdate(DocumentEvent e) {
				textChanged(); 	
			}

			public void removeUpdate(DocumentEvent e) {
				textChanged(); 
			}


		});

		//dimension selector
		dimensionSelector = new JComboBox( Core.getFunctionCreationOptions().getAvailableDimensions() ); 
		dimensionSelector.setSelectedIndex( Core.getFunctionCreationOptions().dimensionToIndex(dimension) );

		zeroCenteredCheckBox = new JCheckBox( "Zero-Centered"); 

		zeroCenteredCheckBox.setSelected( function.isZeroCentered() ); 

		ActionListener listener = new ActionListener() { 

			public void actionPerformed(ActionEvent e) {

				int index = dimensionSelector.getSelectedIndex();
				Integer[] availableDimensions = Core.getFunctionCreationOptions().getAvailableDimensions();
				Core.getFunctionCreationOptions().setDimensionIndex1D(index); 
				int dimension = availableDimensions[index];
				boolean zeroCentered = zeroCenteredCheckBox.isSelected(); 

				double[] indices = Indices.indices1D( dimension, zeroCentered );
				Core.getFunctionCreationOptions().setZeroCentered1D(zeroCentered); 
				editor.setIndices(indices);

				//update function
				function.setZeroCentered(zeroCentered); 
				((Function1D)function).setDimension(dimension);
				Core.getGUI().getFunctionList().refresh();

			}
		};

		JPanel globalOptionsPanel = new JPanel();
		globalOptionsPanel.setLayout( new BoxLayout( globalOptionsPanel, BoxLayout.LINE_AXIS));
		globalOptionsPanel.add( Box.createHorizontalStrut( 5 ) );
		globalOptionsPanel.add( new JLabel( " Function Name: " ) );
		globalOptionsPanel.add( nameTextField );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		globalOptionsPanel.add( new JLabel( " Dimension: " ) );
		globalOptionsPanel.add( dimensionSelector );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		globalOptionsPanel.add( zeroCenteredCheckBox );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		globalOptionsPanel.setBorder( Core.getBorders().getEmptyBorder() );

		zeroCenteredCheckBox.addActionListener( listener ); 
		dimensionSelector.addActionListener( listener );

		JPanel main = new JPanel( new BorderLayout() ); 
		main.add( editor, BorderLayout.CENTER ); 
		main.add( globalOptionsPanel, BorderLayout.NORTH );
		Core.getGUI().setContent(main); 
		content = main; 
	}

	public void addButtons() {

		super.addButtons(); 
		argandButton = new JToggleButton(new PlotArgandAction() );
		buttons.add(argandButton); 
	}

	public class PlotArgandAction extends AbstractAction {

		public PlotArgandAction() {

			super("Argand Diagram", ResourceLoader.createImageIcon("/guiIcons/plotArgand.png"));
			putValue( ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke( KeyEvent.VK_A, ActionEvent.CTRL_MASK ) );
		}

		public void actionPerformed(ActionEvent e) {

			plotArgand(); 

		}

	}

	public void plotArgand() {

		ArgandDiagram plot = new ArgandDiagram();
		plot.setFunction( function );
		setContent( plot, ARGAND );
	}


	public void plot() {

		Function1DOverviewPanel plot = new Function1DOverviewPanel(function); 
		setContent( plot, PLOT ); 
	}

	public void plotFFT() {

		Function transform = Transforms.fft1D(function,	false, Transforms.NORMALIZE_ROOT_N );
		Function1DOverviewPanel plot = new Function1DOverviewPanel(transform);
		setContent( plot, FFT );
	}

	public void textChanged() {

		String name = nameTextField.getText().trim(); 
		function.setDescriptor(name); 
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getDescriptor().equals(GUIEvent.Descriptor.INDICES_MODIFIED_1D)) {

			double[] indices = (double[])(e.getValue(0));
			((Function1D)function).setDimension(indices.length); 
			function.setZeroCentered(indices[0] != 0); 

		} else  { 
			
			if( openTab.equals(EDIT_A ) ){

				((Function1D)function).setPartA(((CreateFunctionPart1DPanel)editor).getFunctionPart1D());  
				Core.getFunctionList().refresh(); 

			} else if( openTab.equals(EDIT_B ) ){

				((Function1D)function).setPartB(((CreateFunctionPart1DPanel)editor).getFunctionPart1D());
				Core.getFunctionList().refresh();

			}

		}

	}

}
