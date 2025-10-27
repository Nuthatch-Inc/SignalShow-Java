package signals.gui.datagenerator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import signals.core.CombineOpsRule;
import signals.core.FunctionProducer;
import signals.core.Operation;
import signals.gui.CalculatorListItem;
import signals.gui.IconCache;
import signals.gui.IconThumbnailGraphic;
import signals.operation.FourierTransformOp;
import signals.operation.PlusOp;

@SuppressWarnings("serial")
public abstract class OperationCalculator extends DataGeneratorCalculator {

	boolean adjusting; 

	public OperationCalculator(GUIEventBroadcaster broadcaster) {
	
		super(broadcaster);
	}

	public JPanel getButtonPanel() { 

		JPanel buttonPanel = super.getButtonPanel(); 
		
		NewUnaryOperationAction newUnaryOperationAction = new NewUnaryOperationAction();
		NewBinaryOperationAction newBinaryOperationAction = new NewBinaryOperationAction();

		buttonPanel.add( new JButton( newUnaryOperationAction ));
		buttonPanel.add( new JButton( newBinaryOperationAction ));

		return buttonPanel; 
	}
	
	@Override
	public int getLeftParenCode() {
		return CombineOpsRule.LEFT_PAREN;
	}

	@Override
	public int getRightParenCode() {
		return CombineOpsRule.RIGHT_PAREN;
	}

	public int getListWidth() {

		return 500; 
	}
	
	public ArrayList<FunctionProducer> getFunctionList() {

		ArrayList<FunctionProducer> termList = new ArrayList<FunctionProducer>(); 

		//loop through the list and return all the function terms
		//skip over the first element in the list, which is always empty
		for( int i=1; i < list.getListSize(); ++i ) {

			Object item = ((CalculatorListItem)list.getItemAt(i)).getValue();
			if( item instanceof FunctionProducer) {

				termList.add((FunctionProducer)item);

			}
		}

		return termList;
	}
	
	public ArrayList<Operation> getOperationList() {

		ArrayList<Operation> termList = new ArrayList<Operation>(); 

		//loop through the list and return all the operations
		//skip over the first element in the list, which is always empty
		for( int i=1; i < list.getListSize(); ++i ) {

			Object item = ((CalculatorListItem)list.getItemAt(i)).getValue();
			if( item instanceof Operation) {

				termList.add((Operation)item);
				
			}
		}

		return termList;
	}

	protected class NewUnaryOperationAction extends AbstractAction { 

		public NewUnaryOperationAction() {

			super( "", IconCache.getIcon("/guiIcons/UnaryOp.png") );
			putValue(SHORT_DESCRIPTION, "New Unary Operation" );
		}

		public void actionPerformed(ActionEvent e) {

			if( list.isLastElementSelected() ) { 
				adjusting = true; 
				createNewUnaryOperation(); 
				addLeftParenSymbol(); 
				createNewFunction();
				addRightParenSymbol(); 
				list.selectPrev();
				list.selectPrev();
				list.selectPrev();
				adjusting = false; 

			} else { 

				createNewUnaryOperation(); 
			}
			
			broadcaster.broadcast(this, GUIEvent.Descriptor.NOTIFY); 
		}

	}

	protected class NewBinaryOperationAction extends AbstractAction { 

		public NewBinaryOperationAction() {

			super( "", IconCache.getIcon("/guiIcons/BinaryOp.png") );
			putValue(SHORT_DESCRIPTION, "New Binary Operation" );
		}

		public void actionPerformed(ActionEvent e) {

			if( list.isLastElementSelected() ) {

				if( list.getModel().getColumnCount() == 1 ) { //only cursor in list

					adjusting = true; 
					createNewFunction(); 
					createNewBinaryOperation(); 
					adjusting = false; 
					createNewFunction(); 
					list.selectPrev();

				} else {

					adjusting = true; 
					createNewBinaryOperation(); 
					adjusting = false; 
					createNewFunction(); 
					list.selectPrev();

				}

			} else {

				createNewBinaryOperation();
			}
			
			broadcaster.broadcast(this, GUIEvent.Descriptor.NOTIFY); 

		}

	}
	
	public abstract void setList( ArrayList<FunctionProducer> functionList, ArrayList<Operation> opsList, 
			CombineOpsRule rule );

	public void createNewUnaryOperation() {

		Operation fftOp = new FourierTransformOp(null);
		ImageIcon opIcon = fftOp.getOpIcon(); 

		Dimension opDimension = new Dimension( opIcon.getIconWidth()+20, getListHeight() ); 

		IconThumbnailGraphic graphic = new IconThumbnailGraphic( opIcon, opDimension, opDimension );

		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				fftOp.getPriority(), fftOp, opDimension, opDimension ); 

		list.insertAfterSelected( newItem );
		broadcaster.broadcast(this, GUIEvent.Descriptor.NEW_SELECTED, fftOp );
	}

	public void createNewBinaryOperation() {

		Operation plusOp = new PlusOp(null);
		ImageIcon opIcon = plusOp.getOpIcon(); 

		Dimension opDimension = new Dimension( opIcon.getIconWidth()+20, getListHeight() ); 

		IconThumbnailGraphic graphic = new IconThumbnailGraphic( opIcon, opDimension, opDimension );

		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				plusOp.getPriority(), plusOp, opDimension, opDimension ); 

		list.insertAfterSelected( newItem );
		broadcaster.broadcast(this, GUIEvent.Descriptor.NEW_SELECTED, plusOp );
	}

	public void setOperation( Operation operation ) {

		ImageIcon opIcon = operation.getOpIcon(); 
		Dimension opDimension = new Dimension( opIcon.getIconWidth()+20, getListHeight() ); 
		IconThumbnailGraphic graphic = new IconThumbnailGraphic( opIcon, opDimension, opDimension );

		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				operation.getPriority(), operation, opDimension, opDimension ); 

		adjusting = true; 

		list.removeSelected();
		list.insertAfterSelected(newItem);

		adjusting = false;
		
		broadcaster.broadcast(this, GUIEvent.Descriptor.NOTIFY); 

	}
	
	public abstract void setFunction( FunctionProducer function ); 
	
	protected void updateDependents() {
		
		if( adjusting ) return; 
		
		//check to see if the currently selected function is a function1Dterm
		CalculatorListItem selectedItem = (CalculatorListItem) list.getSelectedItem();
		Object value = selectedItem.getValue();
		if( value instanceof Operation ) {

			broadcaster.broadcast(this, GUIEvent.Descriptor.EXISTING_SELECTED, value ); 
		

		} else if( value instanceof FunctionProducer ) {
			
			broadcaster.broadcast(this, GUIEvent.Descriptor.SELECTED_CODE1, value );
			
		} else {

			broadcaster.broadcast(this, GUIEvent.Descriptor.NON_SELECTED, value );
		}
	}
	
	public void GUIEventOccurred(GUIEvent e) {
		
		if( e.getSource().equals(this) ) return; 

		switch( e.getDescriptor() ) {

		case MODIFIED:

			Object value = e.getValue(0); 
			
			if( value instanceof FunctionProducer ) {
				 
				adjusting = true; 
				setFunction( (FunctionProducer)value ); 
				adjusting  = false; 
				
			} else if( value instanceof Operation ) {
				
				setOperation( (Operation)value );
			}
		
			break; 

		}
		
	}

}
