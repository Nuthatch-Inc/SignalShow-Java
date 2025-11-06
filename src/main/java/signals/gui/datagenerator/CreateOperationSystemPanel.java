package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import signals.core.BinaryOperation;
import signals.core.CombineOpsRule;
import signals.core.Core;
import signals.core.Function;
import signals.core.FunctionProducer;
import signals.core.Operation;
import signals.core.OperatorSystem;
import signals.core.Savable;
import signals.core.UnaryOperation;
import signals.gui.ResizablePane;

@SuppressWarnings("serial")
public abstract class CreateOperationSystemPanel extends JPanel implements GUIEventListener, Savable, ResizablePane {

	//broadcasts events for this part of the interface	
	GUIEventBroadcaster broadcaster; 
	
	CreateOperationPanel operationPanel; 
	protected OperationCalculator calculator;
	
	CreateOperationPanel unaryOperationPanel, binaryOperationPanel; 
	protected FunctionSelectorPanel functionSelectorPanel;
	
	protected CardLayout layout;
	protected static final String UNARY_PANEL = "unary";
	protected static final String BINARY_PANEL = "binary";
	protected static final String BLANK_PANEL = "blank";
	protected static final String FUNCTION_PANEL = "function";
	protected JPanel contentPanel;

	boolean initialized; 
	
	OperatorSystem system; 
	
	// Preview panel for showing operation output
	OperationPreviewPanel systemPreviewPanel;
	
	public CreateOperationSystemPanel( OperatorSystem system ) {
		this(system, false); // Default to 1D mode
	}
	
	public CreateOperationSystemPanel( OperatorSystem system, boolean is2D ) {
		
		//create the event broadcaster
		this.system = system; 
		broadcaster = new GUIEventBroadcaster(); 
		broadcaster.addGUIEventListener(this); 
		
		//create three panels for the content panel (use card layout) 
		contentPanel = new JPanel(); 
		layout = new CardLayout();
		contentPanel.setLayout( layout );
		
		JPanel blankPanel = new JPanel(); 
		createUnaryPanel(); 
		createBinaryPanel(); 
		createFunctionSelectorPanel(); 
		
		// Wrap the function selector scroll pane in a fixed-width panel
		JScrollPane functionScrollPane = new JScrollPane(functionSelectorPanel);
		JPanel functionPanelWrapper = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(400, super.getPreferredSize().height);
			}
			
			@Override
			public Dimension getMaximumSize() {
				return new Dimension(400, Integer.MAX_VALUE);
			}
			
			@Override
			public Dimension getMinimumSize() {
				return new Dimension(400, 100);
			}
		};
		functionPanelWrapper.add(functionScrollPane, BorderLayout.CENTER);
		
		contentPanel.add( blankPanel, BLANK_PANEL );
		contentPanel.add( (JComponent)unaryOperationPanel, UNARY_PANEL );
		contentPanel.add( (JComponent)binaryOperationPanel, BINARY_PANEL );
		contentPanel.add( functionPanelWrapper, FUNCTION_PANEL );
		
		JButton saveButton = new JButton( "Export Result as Data"); 
		saveButton.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				Core.getFunctionList().addItem(getFunction());
			}
			
		}); 
		
		JButton clearButton = new JButton( "Clear" ); 
		clearButton.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				clear(); 
				
			}
			
		}); 
		
		createCalculator(); 
		
		JPanel buttonPanel = new JPanel(); 
		buttonPanel.add( saveButton ); 
		buttonPanel.add( clearButton ); 
		
		JPanel upperPanel = new JPanel( new BorderLayout() ); 
		upperPanel.add( calculator, BorderLayout.CENTER ); 
		upperPanel.add( buttonPanel, BorderLayout.SOUTH ); 
		
		// Create system-level preview panel
		systemPreviewPanel = new OperationPreviewPanel(is2D);
		
		// Wrap contentPanel to constrain its width
		JPanel contentPanelWrapper = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(600, super.getPreferredSize().height);
			}
			
			@Override
			public Dimension getMaximumSize() {
				return new Dimension(600, Integer.MAX_VALUE);
			}
			
			@Override
			public Dimension getMinimumSize() {
				return new Dimension(600, 200);
			}
		};
		contentPanelWrapper.add(contentPanel, BorderLayout.CENTER);
		
		// Use a horizontal box layout for the center area to respect max sizes
		JPanel centerArea = new JPanel();
		centerArea.setLayout(new BoxLayout(centerArea, BoxLayout.X_AXIS));
		centerArea.add(contentPanelWrapper);
		centerArea.add(Box.createHorizontalStrut(10)); // Add padding between content and preview
		centerArea.add(systemPreviewPanel);
		centerArea.add(Box.createHorizontalGlue());
		
		setLayout( new BorderLayout() ); 
		add( upperPanel, BorderLayout.NORTH );
		add( centerArea, BorderLayout.CENTER );
		setSystem( system ); 
		initialized = true;
		
		// Initialize preview with current system output
		updateSystemPreview();

	}
	
	public void setSystem( OperatorSystem system ) {
		
		this.system = system; 
		ArrayList<FunctionProducer> functionList = system.getInputList(); 
		ArrayList<Operation> operationList = system.getOpList(); 
		CombineOpsRule rule = system.getCombineOpsRule(); 
		calculator.setList(functionList, operationList, rule); 
	}
	
	public abstract Function getFunction(); 
	public abstract void createUnaryPanel(); 
	public abstract void createBinaryPanel(); 	
	public abstract void createCalculator(); 
	public abstract void createFunctionSelectorPanel();
	public abstract void clear(); 
	
	public void setupCorrectPanel( Object newTerm ) {
		
		if( newTerm instanceof UnaryOperation ) {
			
			operationPanel = unaryOperationPanel; 
			layout.show(contentPanel, UNARY_PANEL); 
			
		} else if ( newTerm instanceof BinaryOperation ) {
			
			operationPanel = binaryOperationPanel;
			layout.show(contentPanel, BINARY_PANEL); 
			
		}
		
	}

	public void setListExisting( Object value ) {
	
		setupCorrectPanel( value ); 
		if( value instanceof Operation ) {
		
			operationPanel.setListExisting((Operation)value); 
			
		} 
	}
	
	public void setListNew( Object value ) {
		
		setupCorrectPanel( value ); 
		if( value instanceof Operation ) {
			
			operationPanel.setListNew((Operation)value); 
			
		} 
		
	}
	
	public void saveStateToModel() {
		
		system.setInputList(calculator.getFunctionList()); 
		ArrayList<Operation> opList = calculator.getOperationList(); 
		system.setOpList(opList); 
		int[] rule = calculator.getCodeList();
		system.setCombineOpsRule(new CombineOpsRule( rule, opList ) );
		Core.getFunctionList().refresh();
		
		// Update the preview with new system output
		updateSystemPreview();
	}
	
	/**
	 * Updates the system preview panel with the current output
	 */
	protected void updateSystemPreview() {
		if (systemPreviewPanel != null) {
			try {
				Function output = getFunction();
				systemPreviewPanel.updatePreview(output);
			} catch (Exception e) {
				// If we can't generate output, clear the preview
				systemPreviewPanel.clearPreview();
			}
		}
	}
	
	@Override
	public void sizeChanged() {
		// Propagate resize event to preview panel
		if (systemPreviewPanel != null) {
			systemPreviewPanel.sizeChanged();
		}
	}
	
	public void GUIEventOccurred(GUIEvent e) {
		
		if( !initialized ) return; 
		if( e.getSource().equals(this) ) return; 
	
		switch( e.getDescriptor() ) {
			
		case NEW_SELECTED: //comes from calculator
		
			setListNew( e.getValue(0));
			saveStateToModel();
			
			break; 
		
		case EXISTING_SELECTED: //comes from calculator
			
			setListExisting( e.getValue(0)); 
			saveStateToModel();
			
			break; 
			
		case NON_SELECTED: //comes from calculator
			
			layout.show( contentPanel, BLANK_PANEL );
			saveStateToModel();
			
			break; 
			
		case SELECTED_CODE1: 
			
			layout.show(contentPanel, FUNCTION_PANEL); 
			functionSelectorPanel.setCurrentFunction((FunctionProducer)e.getValue(0)); 
			saveStateToModel();
			
			break; 
			
		case NOTIFY: 
			
			saveStateToModel();
			break; 
			
		case PARAM_CHANGED: 
			
			saveStateToModel();
			break; 
			
		
		}
		
	}

}