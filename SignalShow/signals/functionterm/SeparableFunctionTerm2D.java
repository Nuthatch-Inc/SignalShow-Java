/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.ImageIcon;

import signals.core.FunctionTerm1D;
import signals.core.FunctionTerm2D;

/**
 * @author Juliet
 * Represents a 2D function (image) that is composed of 
 */
public abstract class SeparableFunctionTerm2D extends FunctionTerm2D {
	
	//a 2D analytic function term is make of two 1D function terms and a
	//rule to combine them
	protected FunctionTerm1D functionTerm1DA, functionTerm1DB;

	public SeparableFunctionTerm2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * in the parameter block: functionTerm1D A and B as sources
	 */
	public SeparableFunctionTerm2D(ParameterBlock paramBlock) {
		super(paramBlock);
		functionTerm1DA = (FunctionTerm1D)paramBlock.getSource(0);
		functionTerm1DB = (FunctionTerm1D)paramBlock.getSource(1);
	}

	/**
	 * @return the functionTerm1DA
	 */
	public FunctionTerm1D getFunctionTerm1DA() {
		return functionTerm1DA;
	}

	/**
	 * @param functionTerm1DA the functionTerm1DA to set
	 */
	public void setFunctionTerm1DA(FunctionTerm1D functionTerm1DA) {
		this.functionTerm1DA = functionTerm1DA;
	}

	/**
	 * @return the functionTerm1DB
	 */
	public FunctionTerm1D getFunctionTerm1DB() {
		return functionTerm1DB;
	}

	/**
	 * @param functionTerm1DB the functionTerm1DB to set
	 */
	public void setFunctionTerm1DB(FunctionTerm1D functionTerm1DB) {
		this.functionTerm1DB = functionTerm1DB;
	}
	
	public abstract String getPartAName(); 
	public abstract String getPartBName();
	
	public abstract ImageIcon getPartAIcon(); 
	public abstract ImageIcon getPartBIcon(); 
	
}
