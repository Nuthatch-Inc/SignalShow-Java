package signals.gui.datagenerator;

import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import signals.core.CombineTermsRule;
import signals.core.FunctionTerm1D;
import signals.functionterm.ZeroFunctionTerm1D;
import signals.gui.CalculatorListItem;
import signals.gui.GUIDimensions;
import signals.gui.PlotThumbnailGraphic;
import signals.gui.ThumbnailGraphic;
import signals.gui.plot.FunctionTerm1DThumbnailGraphic;

/**
 * An interface for creating linear combinations of signals
 * @author Juliet
 */
@SuppressWarnings("serial")
public class FunctionCalculator1D extends DataGeneratorCalculator {
	
	public FunctionCalculator1D(GUIEventBroadcaster broadcaster,
			boolean hasButtons) {
		super(broadcaster, hasButtons);
		// TODO Auto-generated constructor stub
	}

	public FunctionCalculator1D(GUIEventBroadcaster broadcaster) {
		super(broadcaster);
	}

	protected double[] indices;

	public ArrayList<FunctionTerm1D> getFunctionTerm1DList() {

		ArrayList<FunctionTerm1D> termList = new ArrayList<FunctionTerm1D>(); 

		//loop through the list and return all the 1D function terms
		//skip over the first element in the list, which is always empty
		for( int i=1; i < list.getListSize(); ++i ) {

			Object item = ((CalculatorListItem)list.getItemAt(i)).getValue();
			if( item instanceof FunctionTerm1D) {

				termList.add((FunctionTerm1D)item);
			}
		}

		return termList;
	}

	public void setFunctionTerm1DList( ArrayList<FunctionTerm1D> termsList, CombineTermsRule rule ) {

		int[] infix = rule.getInfix();

		Iterator<FunctionTerm1D> termsIter = termsList.iterator();

		for( int element : infix ) {

			switch( element ) {

			case CombineTermsRule.FUNCTION_TERM:

				FunctionTerm1D term = termsIter.next(); 
				PlotThumbnailGraphic graphic = 
					new FunctionTerm1DThumbnailGraphic( term, indices );
				CalculatorListItem newItem = new CalculatorListItem( graphic, 
						CombineTermsRule.FUNCTION_TERM, term, 
						ThumbnailGraphic.getSmallDimension(), 
						ThumbnailGraphic.getLargeDimension() ); 

				list.insertAfterSelected( newItem );


				break;

			case CombineTermsRule.ADD:

				addPlusSymbol(); 

				break;
				
			case CombineTermsRule.SUBTRACT: 
				
				addMinusSymbol(); 
				
				break; 

			case CombineTermsRule.MULTIPLY:

				addTimesSymbol(); 

				break;

			case CombineTermsRule.RIGHT_PAREN:

				addRightParenSymbol(); 

				break;

			case CombineTermsRule.LEFT_PAREN:

				addLeftParenSymbol();

				break;

			}
		}
		
		scrollToLeft(); 

	}

	@Override
	public void createNewFunction() { 

		FunctionTerm1D newTerm = new ZeroFunctionTerm1D(new ParameterBlock()); 
		PlotThumbnailGraphic graphic = 
			new FunctionTerm1DThumbnailGraphic( newTerm, indices );

		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				CombineTermsRule.FUNCTION_TERM, newTerm, 
				ThumbnailGraphic.getSmallDimension(), 
				ThumbnailGraphic.getLargeDimension() ); 

		addedNew = true;
		list.insertAfterSelected( newItem );
		broadcaster.broadcast(this, GUIEvent.Descriptor.NEW_SELECTED, newTerm ); 
	}

	/**
	 * Called when the currently selected FunctionTerm1D is updated
	 * @param updatedTerm new term
	 * @param typeCode code for the type of function
	 */
	public void setFunctionTerm1D( FunctionTerm1D updatedTerm ) {

		CalculatorListItem selectedItem = (CalculatorListItem) list.getSelectedItem();
		selectedItem.setGraphic( new FunctionTerm1DThumbnailGraphic( updatedTerm, indices ) );
		selectedItem.setValue( updatedTerm );
		list.repaint();
	}
	
	public JPanel getButtonPanel() { 

		JPanel buttonPanel = super.getButtonPanel(); 
		
		AddAction addAction = new AddAction();
		SubtractAction subtractAction = new SubtractAction(); 
		MultiplyAction multiplyAction = new MultiplyAction(); 

		buttonPanel.add( new JButton( addAction ));
		buttonPanel.add( new JButton( subtractAction ));
		buttonPanel.add( new JButton( multiplyAction ));
		
		//TODO add key binding for minus
		
		//add key bindings
		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("shift EQUALS"), "add");
		getActionMap().put("add", addAction);

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("shift 8"), "multiply");
		getActionMap().put("multiply", multiplyAction);

		return buttonPanel; 
	}

	@Override
	public int getLeftParenCode() {
		return CombineTermsRule.LEFT_PAREN;
	}

	@Override
	public int getRightParenCode() {
		// TODO Auto-generated method stub
		return CombineTermsRule.RIGHT_PAREN;
	}
	
	
	public void delete() {
		
		super.delete(); 
		//createNewFunction(); 
		
	}

	public void setIndices(double[] indices) {

		this.indices = indices;
		for( int i=0; i < list.getListSize(); i++ ) {
			CalculatorListItem item = (CalculatorListItem) list.getItemAt( i );
			Object value = item.getValue();
			if( value != null && value instanceof FunctionTerm1D ) {

				item.setGraphic( new FunctionTerm1DThumbnailGraphic( (FunctionTerm1D)value, indices)); 
			}
		}
		list.repaint();
	}

	public int getListHeight() {

		return GUIDimensions.PLOT_LIST_HEIGHT;
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 
		if( adjusting ) return; 

		switch( e.getDescriptor() ) {

		case INDICES_MODIFIED_1D: //comes from general interface

			setIndices( (double[])(e.getValue(0))); 

			break; 

		case MODIFIED: //comes from term panel

			setFunctionTerm1D( (FunctionTerm1D)(e.getValue(0)));

			break; 

		}

	}

}
