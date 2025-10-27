package signals.gui.datagenerator;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import signals.core.CombineOpsRule;
import signals.core.Core;
import signals.core.Function2D;
import signals.core.FunctionProducer;
import signals.core.Operation;
import signals.gui.CalculatorListItem;
import signals.gui.Function2DThumbnailGraphic;
import signals.gui.GUIDimensions;
import signals.gui.IconThumbnailGraphic;
import signals.gui.ThumbnailGraphic;

/**
 * An interface for creating linear combinations of signals
 * @author Juliet
 */
@SuppressWarnings("serial")
public class OperationCalculator2D extends OperationCalculator {


	public OperationCalculator2D(GUIEventBroadcaster broadcaster) {
		super(broadcaster);
	}
	
	public void setList( ArrayList<FunctionProducer> functionList, ArrayList<Operation> opsList, 
			CombineOpsRule rule ) {

		int[] infix = rule.getInfix();

		Iterator<FunctionProducer> termsIter = functionList.iterator();
		Iterator<Operation> opsIter = opsList.iterator();

		for( int element : infix ) {

			switch( element ) {

			case CombineOpsRule.FUNCTION: 

				FunctionProducer term = termsIter.next(); 
				Function2DThumbnailGraphic graphic = 
					new Function2DThumbnailGraphic( (Function2D)term.getFunction() );
				CalculatorListItem newItem = new CalculatorListItem( graphic, 
						CombineOpsRule.FUNCTION, term, 
						ThumbnailGraphic.getSmallDimension(), 
						ThumbnailGraphic.getLargeDimension() ); 

				list.insertAfterSelected( newItem );


				break;

			case CombineOpsRule.BINARY_OP_TIER_1:
			case CombineOpsRule.BINARY_OP_TIER_2:
			case CombineOpsRule.BINARY_OP_TIER_3:
			case CombineOpsRule.UNARY_OP_TIER_1:
			case CombineOpsRule.UNARY_OP_TIER_2:
			case CombineOpsRule.UNARY_OP_TIER_3:

				Operation op = opsIter.next(); 
				ImageIcon opIcon = op.getOpIcon(); 

				Dimension opDimension = new Dimension( opIcon.getIconWidth()+20, getListHeight() ); 

				IconThumbnailGraphic graphicOp = new IconThumbnailGraphic( opIcon, opDimension, opDimension );

				CalculatorListItem itemOp = new CalculatorListItem( graphicOp, 
						op.getPriority(), op, opDimension, opDimension ); 

				list.insertAfterSelected( itemOp );

				break;
				
			case CombineOpsRule.RIGHT_PAREN:

				addRightParenSymbol(); 

				break;

			case CombineOpsRule.LEFT_PAREN:

				addLeftParenSymbol();

				break;

			}
		}
		
		scrollToLeft(); 
		broadcaster.broadcast(this, GUIEvent.Descriptor.NOTIFY); 

	}

	@Override
	public void createNewFunction() { 
		
		FunctionProducer selected = (FunctionProducer) Core.getGUI().getFunction2DList().getSelectedItem(); 
		Function2DThumbnailGraphic graphic = 
			new Function2DThumbnailGraphic( (Function2D) selected.getFunction() );
		
		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				CombineOpsRule.FUNCTION, selected, 
				ThumbnailGraphic.getSmallDimension(), 
				ThumbnailGraphic.getLargeDimension() ); 

		list.insertAfterSelected( newItem );
	}
	
	public void setFunction( FunctionProducer function ) {
		
		CalculatorListItem selectedItem = (CalculatorListItem) list.getSelectedItem();
		selectedItem.setGraphic( new Function2DThumbnailGraphic( (Function2D)function.getFunction() ) );
		selectedItem.setValue( function );
		list.repaint();
		broadcaster.broadcast(this, GUIEvent.Descriptor.NOTIFY); 
	}

	public int getListHeight() {
		
		return GUIDimensions.LIST_WIDTH;
	}
}
