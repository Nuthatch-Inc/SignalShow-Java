package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.Function;
//import signals.core.FunctionFactory;
import signals.core.UnaryOperation;

public class IntegrateOp extends UnaryOperation {

	public IntegrateOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IntegrateOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	public IntegrateOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}

	@Override
	public Function create(Function input) {
		
		//TODO 
		return null; 
	}

	@Override
	public String getOpIconPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
