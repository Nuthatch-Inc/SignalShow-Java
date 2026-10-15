package signals.gui.datagenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

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
import signals.core.Function2D;
import signals.core.FunctionPart2D;
import signals.gui.IconCache;
import signals.gui.plot.Function2DOverviewPanel;
import signals.gui.plot.ProfilePlot;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public abstract class Function2DToolBar extends FunctionToolBar {

	JComboBox widthSelector, heightSelector; 
	JCheckBox zeroCenteredCheckBox;
	JTextField nameTextField; 
	CreateFunctionPart2DPanel editor; 
	public static final String PROFILE = "PROFILE";
	JToggleButton profileButton; 

	@Override
	public void deconstruct() {
		editor = null;
		widthSelector = null;
		heightSelector = null;
		zeroCenteredCheckBox = null;
		nameTextField = null;
		super.deconstruct();
	}

	public Function2DToolBar( Function function ) {

		super( function ); 
	}

	public Function2DToolBar( Function function, boolean newState ) {

		super( function ); 
		this.newStateA = newState; 
		this.newStateB = newState; 
	}
	
	public Function2DToolBar() {

		super();
	}

	public void editPartA() {
		
		openTab = EDIT_A; 
		showEditor( newStateA, ((Function2D)function).getPartA() ); 
		newStateA = false; 
	}

	public void editPartB() {

		openTab = EDIT_B; 
		showEditor( newStateB, ((Function2D)function).getPartB() ); 
		newStateB = false; 
	}
	
	public void selectButton(String buttonCode) {
        if (buttonCode.equals(PROFILE)) {
            profileButton.setSelected(true);
            plotProfile();
        } else {
            super.selectButton(buttonCode);
        }
	}

	public void showEditor( boolean newState, FunctionPart2D part ) {

		int width = ((Function2D)function).getDimensionX(); 
		int height = ((Function2D)function).getDimensionY();
		GUIEventBroadcaster broadcaster = new GUIEventBroadcaster(); 
		editor = new CreateFunctionPart2DPanel(broadcaster, newState, isEditable()); 
		editor.setIndices(width, height, function.isZeroCentered() );

		if( !newState ) {

			editor.setFunctionPart2D(part);
		}

		broadcaster.addGUIEventListener(this);

		//name box
		nameTextField = new JTextField(function.getDescriptor());

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
		widthSelector = new JComboBox( Core.getFunctionCreationOptions().getAvailableDimensions() ); 
		heightSelector = new JComboBox( Core.getFunctionCreationOptions().getAvailableDimensions() ); 

		widthSelector.setSelectedIndex( Core.getFunctionCreationOptions().dimensionToIndex(width) );
		heightSelector.setSelectedIndex( Core.getFunctionCreationOptions().dimensionToIndex(height) );

		zeroCenteredCheckBox = new JCheckBox( "Zero-Centered"); 

		zeroCenteredCheckBox.setSelected( function.isZeroCentered() ); 

		ActionListener listener = new ActionListener() { 

			public void actionPerformed(ActionEvent e) {

				if( e.getSource().equals(widthSelector)) {

					heightSelector.setSelectedIndex(widthSelector.getSelectedIndex());
					return; 
				}

				int indexX = widthSelector.getSelectedIndex();
				int indexY = heightSelector.getSelectedIndex(); 
				Integer[] availableDimensions = Core.getFunctionCreationOptions().getAvailableDimensions();
				Core.getFunctionCreationOptions().setDimensionXIndex2D(indexX); 
				Core.getFunctionCreationOptions().setDimensionYIndex2D(indexY);
				int width = availableDimensions[indexX];
				int height = availableDimensions[indexY];

				boolean zeroCentered = zeroCenteredCheckBox.isSelected(); 
				Core.getFunctionCreationOptions().setZeroCentered2D(zeroCentered);

				editor.setIndices( width, height, zeroCentered);

				//update function
				function.setZeroCentered(zeroCentered); 
				((Function2D)function).setDimensionX(width);
				((Function2D)function).setDimensionY(height);
				refreshLibrary();

			}
		};


		JPanel globalOptionsPanel = new JPanel();
		globalOptionsPanel.setLayout( new BoxLayout( globalOptionsPanel, BoxLayout.LINE_AXIS));
		globalOptionsPanel.add( Box.createHorizontalStrut( 5 ) );
		JLabel nameLabel = new JLabel(" Function Name: ");
		nameLabel.setLabelFor(nameTextField);
		globalOptionsPanel.add(nameLabel);
		globalOptionsPanel.add( nameTextField );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		JLabel widthLabel = new JLabel(" Width: ");
		widthLabel.setLabelFor(widthSelector);
		globalOptionsPanel.add(widthLabel);
		globalOptionsPanel.add( widthSelector ); 
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		JLabel heightLabel = new JLabel(" Height: ");
		heightLabel.setLabelFor(heightSelector);
		globalOptionsPanel.add(heightLabel);
		globalOptionsPanel.add( heightSelector ); 
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		globalOptionsPanel.add( zeroCenteredCheckBox );
		globalOptionsPanel.add( Box.createHorizontalStrut( 20 ) );
		globalOptionsPanel.setBorder( Core.getBorders().getEmptyBorder() );

		zeroCenteredCheckBox.addActionListener( listener ); 
		widthSelector.addActionListener( listener );
		heightSelector.addActionListener( listener );

		JPanel main = new FunctionEditorPanel(editor, globalOptionsPanel);
		setContent(main, openTab);
	}

	public void addButtons() {

		super.addButtons(); 
		profileButton = new JToggleButton(new PlotProfileAction() );
		buttons.add(profileButton); 
	}

	public class PlotProfileAction extends AbstractAction {

		public PlotProfileAction() {

			super("Profiles", IconCache.getIcon("/guiIcons/profile.png") ); 

			putValue( ACCELERATOR_KEY, 
					KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.CTRL_MASK ) );
		}

		public void actionPerformed(ActionEvent e) {

			plotProfile(); 

		}

	}

	public void plotProfile() {

		ProfilePlot plot = new ProfilePlot(function);
		setContent( plot, PROFILE );
	}

	public void plot() {

		Function2DOverviewPanel plot = new Function2DOverviewPanel(function);
		setContent( plot, PLOT );
	}

	public void plotFFT() {

		Function transform = Transforms.fft2D(function,	false, Transforms.NORMALIZE_ROOT_N );
		Function2DOverviewPanel plot = new Function2DOverviewPanel(transform);
		setContent( plot, FFT );
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getDescriptor().equals(GUIEvent.Descriptor.INDICES_MODIFIED_2D)) {

			ArrayList<Object> values = e.getValues(); 
			((Function2D)function).setDimensionX((Integer)values.get(0)); 
			((Function2D)function).setDimensionY((Integer)values.get(1));
			function.setZeroCentered((Boolean)values.get(2)); 

		} else  { 
			
			if( openTab.equals(EDIT_A ) ){

				((Function2D)function).setPartA(((CreateFunctionPart2DPanel)editor).getFunctionPart2D());  
				refreshLibrary();

			} else if( openTab.equals(EDIT_B ) ){

				((Function2D)function).setPartB(((CreateFunctionPart2DPanel)editor).getFunctionPart2D());
				refreshLibrary();

			}

		}

	}

	/** Repaint the desktop library after publishing the edited image. */
	protected void refreshLibrary() {
		Core.getFunctionList().refresh();
	}

	public void textChanged() {

		String name = nameTextField.getText().trim(); 
		function.setDescriptor(name); 
	}

}
