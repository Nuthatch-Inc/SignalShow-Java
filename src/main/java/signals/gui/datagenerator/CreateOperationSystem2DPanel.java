package signals.gui.datagenerator;

import java.util.ArrayList;

import signals.core.CombineOpsRule;
import signals.core.Function;
import signals.core.FunctionProducer;
import signals.core.Operation;
import signals.core.OperatorSystem;
import signals.core.OperatorSystem2D;


@SuppressWarnings("serial")
public class CreateOperationSystem2DPanel extends CreateOperationSystemPanel {

	public CreateOperationSystem2DPanel(OperatorSystem system) {
		super(system, true); // Pass true to start in 2D mode
	}

	@Override
	public void createBinaryPanel() {
		
		binaryOperationPanel = new CreateBinaryOperation2DPanel(broadcaster); 
		
	}

	@Override
	public void createUnaryPanel() {
		
		unaryOperationPanel = new CreateUnaryOperation2DPanel(broadcaster); 
		
	}

	@Override
	public void createCalculator() {
		
		calculator = new OperationCalculator2D(broadcaster); 
		
	}

	@Override
	public void createFunctionSelectorPanel() {
		functionSelectorPanel = new Function2DSelectorPanel(broadcaster, system); 
	}

	@Override
	public Function getFunction() {
		
		ArrayList<FunctionProducer> inputList = calculator.getFunctionList();
		ArrayList<Operation> opList = calculator.getOperationList(); 
		int[] rule = calculator.getCodeList();
		CombineOpsRule combineRule = new CombineOpsRule( rule, opList );
		OperatorSystem system = new OperatorSystem2D(combineRule, inputList, opList );
		Function output = system.getFunction();
	
		return output;
	}

	@Override
	public void clear() {
		
		calculator.removeAllItems(); 
		system = new OperatorSystem2D();
	}
}