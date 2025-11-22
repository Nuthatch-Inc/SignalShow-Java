package signals.gui.datagenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.ListModel;

import signals.core.Core;
import signals.core.Function;
import signals.core.FunctionProducer;
import signals.core.OperatorSystem;

@SuppressWarnings("serial")
public abstract class FunctionSelectorPanel extends JPanel {

	OperationCalculator calculator; 

	ArrayList<FunctionButton> buttons;

	FunctionProducer selectedFunction; 

	FunctionButton selectedButton; 

	GUIEventBroadcaster broadcaster;

	OperatorSystem excludeSystem; //this system should not appear in this panel. 

	public FunctionSelectorPanel( GUIEventBroadcaster broadcaster, OperatorSystem excludeSystem) {

		this.broadcaster = broadcaster; 
		this.excludeSystem = excludeSystem; 
		buttons = new ArrayList<FunctionButton>(); 

		ListModel model = getFunctionListModel();

		int numElements = model.getSize();

		setLayout( new FlowLayout(FlowLayout.LEFT, 5, 5 ) );

		for( int i = 0; i < numElements; ++i ) {

			FunctionButton button = new FunctionButton( (FunctionProducer)model.getElementAt(i) ); 
			buttons.add( button );
			add( button ); 

		}

		setCurrentFunction( (FunctionProducer)model.getElementAt(0)); 
		selectedButton = buttons.get(0); 

	}

	public void setCalculator( OperationCalculator calculator ) {

		this.calculator = calculator; 
	}

	public void setCurrentFunction( FunctionProducer function ) {

		selectedFunction = function; 

		for( FunctionButton button : buttons ) {

			if( button.function.equals(function) ) {

				button.setBorder( Core.getBorders().getSelectedFrame() );
				selectedButton = button; 
			}
			else button.setBorder( Core.getBorders().getUnselectedFrameTransparent() );
		}

	}

	public void addNewFunction( Function function ) {

		FunctionButton button = new FunctionButton( function ); 
		buttons.add( button );
		add( button ); 
		revalidate(); 
		repaint(); 
		setCurrentFunction( function ); 
		updateDependents(); 
	}

	public void modifySelectedFunction( Function function ) {

		selectedButton.setFunction(function); 
		selectedFunction = function; 
		selectedButton.repaint(); 
		updateDependents(); 
	}

	public FunctionProducer getFunction() {
		return selectedFunction;
	}

	public void updateDependents() {

		broadcaster.broadcast(this, GUIEvent.Descriptor.MODIFIED, selectedFunction); 
	}

	public abstract ListModel getFunctionListModel();

	public abstract Dimension getButtonSize();

	public class FunctionButton extends JPanel implements MouseListener {

		FunctionProducer function; 

		public FunctionButton( FunctionProducer functionProducer ) {

			this.function = functionProducer; 
			setPreferredSize( getButtonSize() ); 
			setBorder( Core.getBorders().getUnselectedFrameTransparent() );
			addMouseListener( this ); 
			setBackground( Color.white ); 
		}

		public void setFunction( Function function ) {

			this.function = function; 
		}

		public void paintComponent( Graphics g ) {

			super.paintComponent(g);
			
			// Use paintSmallGraphic() like the horizontal calculator does
			function.getFunction().paintSmallGraphic((Graphics2D)g);
		}


		public void mouseClicked(MouseEvent e) {}


		public void mouseEntered(MouseEvent e) {

			if( !function.equals(selectedFunction) ) setBorder( Core.getBorders().getHoverFrame() );
		}


		public void mouseExited(MouseEvent e) {

			if( !function.equals(selectedFunction) ) setBorder( Core.getBorders().getUnselectedFrameTransparent() );
		}


		public void mousePressed(MouseEvent e) {

			setCurrentFunction( function ); 
			selectedButton = this; 
			updateDependents(); 
		}

		public void mouseReleased(MouseEvent e) {}

	}

}
