package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

@SuppressWarnings("serial")
public abstract class CreateOperationSystemPanel extends JPanel implements GUIEventListener, Savable {

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
	
	public CreateOperationSystemPanel( OperatorSystem system ) {
		
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
		
		contentPanel.add( blankPanel, BLANK_PANEL );
		contentPanel.add( (JComponent)unaryOperationPanel, UNARY_PANEL );
		contentPanel.add( (JComponent)binaryOperationPanel, BINARY_PANEL );
		contentPanel.add( new JScrollPane( functionSelectorPanel ), FUNCTION_PANEL );
		
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
		
		setLayout( new BorderLayout() ); 
		add( upperPanel, BorderLayout.NORTH );
		add( contentPanel, BorderLayout.CENTER ); 
		setSystem( system ); 
		initialized = true; 

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