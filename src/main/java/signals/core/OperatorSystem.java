package signals.core;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

import signals.gui.GUIDimensions;
import signals.gui.IconCache;
import signals.gui.IconThumbnailGraphic;
import signals.gui.ThumbnailProducer;
import signals.gui.datagenerator.OperatorSystemToolBar;

/**
 * A series of operations and input functions combined together in an equation 
 * Analogous to a "Function" made of many "function terms" 
 * @author Juliet
 *
 */
public abstract class OperatorSystem implements ThumbnailProducer, FunctionProducer, Showable {

	//input functions
	ArrayList<FunctionProducer> inputList;

	//list of operations
	ArrayList<Operation> opList; 
	
	ArrayList<Long> inputID; 
	
	//rule dictating how to combine operations to produce the correct output
	CombineOpsRule combineOpsRule;
	
	IconThumbnailGraphic graphic; 
	
	@SuppressWarnings("unchecked")
	//soft reference to output object
	SoftReference output; 
	
	String showState; 

	
	/**
	 * @param combineOpsRule
	 * @param inputFunction
	 * @param opList
	 */
	public OperatorSystem(CombineOpsRule combineOpsRule, ArrayList<FunctionProducer> inputList, ArrayList<Operation> opList) {
		super();
		this.combineOpsRule = combineOpsRule;
		this.inputList = inputList;
		this.opList = opList;
		inputID = new ArrayList<Long>(); 
		for( FunctionProducer function: inputList ) {
			
			inputID.add(function.getFunction().getID()); 
		}
		graphic = new IconThumbnailGraphic( IconCache.getIcon("/guiIcons/operator.png"), 
				getSmallThumbnailSize(), getLargeThumbailSize() );
		showState = OperatorSystemToolBar.EDIT; 
	}
	
	public abstract OperatorSystem clone(); 
	
	public void setDirty() {
		
		graphic.setDirty(); 
	}
	
	public void freeMemory() {
		
		output = null; 
	}
	
	public OperatorSystem() {
		
		super(); 
		inputList = new ArrayList<FunctionProducer>(); 
		inputID = new ArrayList<Long>(); 
		opList = new ArrayList<Operation>(); 
		int[] empty = {}; 
		combineOpsRule = new CombineOpsRule(empty, opList); 
		graphic = new IconThumbnailGraphic( IconCache.getIcon("/guiIcons/operator.png"), 
				getSmallThumbnailSize(), getLargeThumbailSize() );
		showState = OperatorSystemToolBar.EDIT; 
	}

	public String getCompactDescriptor() {
		return "Operator System";
	}

	public String getLongDescriptor() {
		
		return "Operator System";
	}

	public String getShowState() {
		return showState;
	}

	public void setShowState(String showState) {
		this.showState = showState;
	}

	public Dimension getLargeThumbailSize() {
		return GUIDimensions.largeImageThumbnailDimension; 
	}

	public Dimension getSmallThumbnailSize() {
		return GUIDimensions.smallImageThumbnailDimension; 
	}

	public void paintLargeGraphic(Graphics2D g) {
		
		Function out = getFunction(); 
		if( out != null ) out.paintLargeGraphic((Graphics2D)g); 
		graphic.paintSmallGraphic((Graphics2D)g); 

	}
	
	public void paintGraphic( Graphics2D g, int w, int h ) {
		
		Function out = getFunction(); 
		if( out != null ) {
			out.setDirty(); 
			out.paintGraphic((Graphics2D)g, w, h); 
		}
		graphic.paintGraphic(g, w, h);
	}
	
	public void checkDirtyInputs() {
		
		if( output == null ) return; 
		for( int i = 0; i < inputList.size(); i++ ) {
			
			if( (inputList.get(i).getFunction() == null) || 
					(inputList.get(i).getFunction().getID() != inputID.get(i))) {
				output = null;
				return; 
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Function getFunction() {
		
		checkDirtyInputs(); 
		Function out = null; 
		if( output == null || output.get() == null ) {
			
			out = combineOpsRule.combine(inputList); 
			output = new SoftReference( out ); 
			
		} else out = (Function) output.get(); 
		
		return out; 
	}

	public void paintSmallGraphic(Graphics2D g) {
		
		Function out = getFunction(); 
		if( out != null ) out.paintSmallGraphic((Graphics2D)g); 
		graphic.paintSmallGraphic((Graphics2D)g); 

	}
	
	public ArrayList<FunctionProducer> getInputList() {
		return inputList;
	}

	public void setInputList(ArrayList<FunctionProducer> inputList) {
		this.inputList = inputList;
		inputID = new ArrayList<Long>(); 
		for( FunctionProducer function: inputList ) {
			
			inputID.add(function.getFunction().getID()); 
		}
		output = null;
	
	}

	public ArrayList<Operation> getOpList() {
		return opList;
	}

	public void setOpList(ArrayList<Operation> opList) {
		this.opList = opList;
		output = null;
	}

	public CombineOpsRule getCombineOpsRule() {
		return combineOpsRule;
	}

	public void setCombineOpsRule(CombineOpsRule combineOpsRule) {
		this.combineOpsRule = combineOpsRule;
		output = null; 
	}

	public IconThumbnailGraphic getGraphic() {
		return graphic;
	}

	public void setGraphic(IconThumbnailGraphic graphic) {
		this.graphic = graphic; 
		output = null;
	}

}
