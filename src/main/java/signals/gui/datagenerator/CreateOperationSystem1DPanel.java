package signals.gui.datagenerator;

import java.util.ArrayList;

import signals.core.CombineOpsRule;
import signals.core.Function;
import signals.core.FunctionProducer;
import signals.core.Operation;
import signals.core.OperatorSystem;
import signals.core.OperatorSystem1D;


@SuppressWarnings("serial")
public class CreateOperationSystem1DPanel extends CreateOperationSystemPanel {

	public CreateOperationSystem1DPanel(OperatorSystem system) {
		super(system);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createBinaryPanel() {
		
		binaryOperationPanel = new CreateBinaryOperation1DPanel(broadcaster); 
		
	}

	@Override
	public void createUnaryPanel() {
		
		unaryOperationPanel = new CreateUnaryOperation1DPanel(broadcaster); 
		
	}
	
	@Override
	public void createCalculator() {
		
		calculator = new OperationCalculator1D(broadcaster); 
		
	}

	@Override
	public void createFunctionSelectorPanel() {
		functionSelectorPanel = new Function1DSelectorPanel(broadcaster, system); 
	}
	
	public Function getFunction() {
		
		ArrayList<FunctionProducer> inputList = calculator.getFunctionList();
		ArrayList<Operation> opList = calculator.getOperationList(); 
		int[] rule = calculator.getCodeList();
		CombineOpsRule combineRule = new CombineOpsRule( rule, opList );
		OperatorSystem system = new OperatorSystem1D(combineRule, inputList, opList );
		Function output = system.getFunction();
	
		return output;
	}

	@Override
	public void clear() {
		
		calculator.removeAllItems(); 
		system = new OperatorSystem1D();
		
	}

}