package signals.core;

import java.awt.image.renderable.ParameterBlock;

public abstract class BinaryOperation extends Operation {

	public BinaryOperation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BinaryOperation(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
	}
	
	/** Binary sample operations require matching signal lengths or image grids. */
	public boolean acceptsDimensions(Function inputA, Function inputB) {
		if (inputA instanceof Function1D a && inputB instanceof Function1D b) {
			return a.getDimension() == b.getDimension();
		}
		if (inputA instanceof Function2D a && inputB instanceof Function2D b) {
			return a.getDimensionX() == b.getDimensionX() && a.getDimensionY() == b.getDimensionY();
		}
		return false;
	}

	public abstract Function create( Function inputA, Function inputB );

}
