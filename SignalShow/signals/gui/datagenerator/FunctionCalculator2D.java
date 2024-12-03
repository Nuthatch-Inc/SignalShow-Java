package signals.gui.datagenerator;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import signals.core.CombineTermsRule;
import signals.core.FunctionTerm2D;
import signals.gui.CalculatorListItem;
import signals.gui.GUIDimensions;
import signals.gui.ThumbnailGraphic;
import signals.gui.plot.FunctionTerm2DThumbnailGraphic;

/**
 * An interface for creating linear combinations of signals
 * @author Juliet
 */
@SuppressWarnings("serial")
public class FunctionCalculator2D extends DataGeneratorCalculator {

	int xDimension, yDimension;
	boolean zeroCentered;
	
	public FunctionCalculator2D(GUIEventBroadcaster broadcaster,
			boolean hasButtons) {
		super(broadcaster, hasButtons);
		// TODO Auto-generated constructor stub
	}

	public FunctionCalculator2D(GUIEventBroadcaster broadcaster) {
		super(broadcaster);
	}

	public ArrayList<FunctionTerm2D> getFunctionTerm2DList() {

		ArrayList<FunctionTerm2D> termList = new ArrayList<FunctionTerm2D>(); 

		//loop through the list and return all the 2D function terms
		//skip over the first element in the list, which is always empty
		for( int i=1; i < list.getListSize(); ++i ) {

			Object item = ((CalculatorListItem)list.getItemAt(i)).getValue();
			if( item instanceof FunctionTerm2D) {

				termList.add((FunctionTerm2D)item);
			}
		}

		return termList;
	}

	public void setFunctionTerm2DList( ArrayList<FunctionTerm2D> termsList, CombineTermsRule rule ) {

		int[] infix = rule.getInfix();

		Iterator<FunctionTerm2D> termsIter = termsList.iterator();

		for( int element : infix ) {

			switch( element ) {

			case CombineTermsRule.FUNCTION_TERM:

				FunctionTerm2D term = termsIter.next();
				FunctionTerm2DThumbnailGraphic graphic = 
					new FunctionTerm2DThumbnailGraphic( term, xDimension, yDimension, zeroCentered );

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

	public JPanel getButtonPanel() { 

		JPanel buttonPanel = super.getButtonPanel(); 

		AddAction addAction = new AddAction();
		SubtractAction subtractAction = new SubtractAction();
		MultiplyAction multiplyAction = new MultiplyAction(); 

		buttonPanel.add( new JButton( addAction ));
		buttonPanel.add( new JButton( subtractAction ));
		buttonPanel.add( new JButton( multiplyAction ));

		//add key bindings
		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("shift EQUALS"), "add");
		getActionMap().put("add", addAction);

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("shift 8"), "multiply");
		getActionMap().put("multiply", multiplyAction);

		return buttonPanel; 
	}

	public void delete() {

		super.delete(); 
		//createNewFunction(); 

	}

	@Override
	public void createNewFunction() { 

		broadcaster.broadcast(this, GUIEvent.Descriptor.NON_SELECTED, CreateFunctionPart2DPanel.INITIALIZE );
	}

	public void insertNewFunction( FunctionTerm2D term2D ) {

		FunctionTerm2DThumbnailGraphic graphic = 
			new FunctionTerm2DThumbnailGraphic( term2D, xDimension, yDimension, zeroCentered );

		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				CombineTermsRule.FUNCTION_TERM, term2D, 
				ThumbnailGraphic.getSmallDimension(), 
				ThumbnailGraphic.getLargeDimension() ); 

		addedNew = true;
		list.insertAfterSelected( newItem );
		broadcaster.broadcast(this, GUIEvent.Descriptor.NEW_SELECTED, term2D ); 
	}

	/**
	 * Called when the currently selected FunctionTerm2D is updated
	 * @param updatedTerm new term
	 * @param typeCode code for the type of function
	 */
	public void setFunctionTerm2D( FunctionTerm2D updatedTerm ) {

		CalculatorListItem selectedItem = (CalculatorListItem) list.getSelectedItem();
		selectedItem.setGraphic( new FunctionTerm2DThumbnailGraphic( updatedTerm, xDimension, yDimension, zeroCentered ) );
		selectedItem.setValue( updatedTerm );
		list.repaint();
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

	public void setIndices( int xDimension, int yDimension, boolean zeroCentered ) {

		this.xDimension = xDimension; 
		this.yDimension = yDimension; 
		this.zeroCentered = zeroCentered;
		for( int i=0; i < list.getListSize(); i++ ) {
			CalculatorListItem item = (CalculatorListItem) list.getItemAt( i );
			Object value = item.getValue();
			if( value != null && value instanceof FunctionTerm2D ) {

				item.setGraphic( new FunctionTerm2DThumbnailGraphic( (FunctionTerm2D)value,  xDimension, yDimension, zeroCentered)); 
			}
		}
		list.repaint();
	}
	
	public void refreshList() {
		
		for( int i=0; i < list.getListSize(); i++ ) {
			CalculatorListItem item = (CalculatorListItem) list.getItemAt( i );
			Object value = item.getValue();
			if( value != null && value instanceof FunctionTerm2D ) {

				item.setGraphic( new FunctionTerm2DThumbnailGraphic( (FunctionTerm2D)value,  xDimension, yDimension, zeroCentered)); 
			}
		}
		list.repaint();
	}

	public int getListHeight() {

		return GUIDimensions.LIST_WIDTH;
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 

		switch( e.getDescriptor() ) {

		case INDICES_MODIFIED_2D: 

			ArrayList<Object> values = e.getValues(); 
			setIndices( (Integer)values.get(0), (Integer)values.get(1), (Boolean)values.get(2) );

			break; 

		case MODIFIED:

			setFunctionTerm2D( (FunctionTerm2D)(e.getValue(0)));

			break; 

		}

	}

}
