package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import signals.core.CombineTermsRule;
import signals.core.DataGenerator;
import signals.gui.CalculatorListItem;
import signals.gui.GUI;
import signals.gui.GUIDimensions;
import signals.gui.HorizontalThumbnailList;
import signals.gui.IconCache;
import signals.gui.IconThumbnailGraphic;
import signals.gui.StringThumbnailGraphic;
import signals.gui.ThumbnailGraphic;

@SuppressWarnings("serial")
public abstract class DataGeneratorCalculator extends JPanel implements ListSelectionListener, GUIEventListener {

	protected HorizontalThumbnailList list;
	
	boolean addedNew;
	
	GUIEventBroadcaster broadcaster; 
	
	boolean adjusting; 

	public DataGeneratorCalculator( GUIEventBroadcaster broadcaster ) {
		
		this( broadcaster, true ); 
	}
	
	public DataGeneratorCalculator( GUIEventBroadcaster broadcaster, boolean hasButtons ) {
		
		this.broadcaster = broadcaster; 
		broadcaster.addGUIEventListener(this); 
		
		adjusting = true; 

		//create the list
		Dimension listSize = new Dimension( 5000, getListHeight() );
		list = new HorizontalThumbnailList( listSize );
		list.getColumnModel().getSelectionModel().addListSelectionListener(this);
		
		addEqualsSign(); 
		
		//TODO: add key bindings for numpad (when on a numpad computer)

		//panel layout
		setLayout( new BorderLayout() ); 
		JScrollPane listScrollPane = new JScrollPane( list , ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
		
		listScrollPane.setBorder( BorderFactory.createLoweredBevelBorder() ); 
			
		listScrollPane.setPreferredSize( new Dimension( getListWidth(), getListHeight() + GUI.getScrollBarWidth() + 10 ) );
		
		if( hasButtons ) add( getButtonPanel(), BorderLayout.EAST );
		add( listScrollPane, BorderLayout.CENTER );
		setBorder( BorderFactory.createEmptyBorder(5, 10, 5, 10) ); 
		
		adjusting = false; 
		
	}
	
	public void scrollToLeft() {
		
		list.scrollToLeft(); 
	}
	
	public void addEqualsSign() {
		
		ImageIcon equalsIcon = IconCache.getIcon("/operationIcons/EqualsOp.png"); 
		Dimension opDimension = new Dimension( equalsIcon.getIconWidth()+30, getListHeight() ); 
		IconThumbnailGraphic graphic = new IconThumbnailGraphic( equalsIcon, opDimension, opDimension );
		list.addItem( new CalculatorListItem( graphic, 0, null, opDimension, opDimension ) );
	}
	
	public JPanel getButtonPanel() { 
		
		//create actions
		LeftParenAction leftParenAction = new LeftParenAction(); 
		RightParenAction rightParenAction = new RightParenAction(); 
		DeleteAction deleteAction = new DeleteAction();
		NewFunctionAction newFunctionAction = new NewFunctionAction(); 

		//create the calculator button panel
		JPanel buttonPanel = new JPanel(); 
		buttonPanel.setLayout( new GridLayout( 3, 2, 0, 0 ));
		buttonPanel.add( new JButton( leftParenAction ));
		buttonPanel.add( new JButton( rightParenAction ));
		buttonPanel.add( new JButton( deleteAction ));
		buttonPanel.add( new JButton( newFunctionAction ));
		buttonPanel.setBorder( BorderFactory.createEmptyBorder(0, 5, 0, 0) );

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("shift 9"), "left");
		getActionMap().put("left", leftParenAction);

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("shift 0"), "right");
		getActionMap().put("right", rightParenAction);

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("F1"), "new");
		getActionMap().put("new", newFunctionAction);

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("DELETE"), "delete");
		getActionMap().put("delete", deleteAction);

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("LEFT"), "left_ar");
		getActionMap().put("left_ar", new AbstractAction() {

			public void actionPerformed( ActionEvent e ) {
				list.selectPrev();
			}
		});

		getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("RIGHT"), "right_ar");
		getActionMap().put("right_ar", new AbstractAction() {

			public void actionPerformed( ActionEvent e ) {
				list.selectNext();
			}
		});
		
		return buttonPanel;
	}
	
	public void removeAllItems() {
		
		adjusting = true; 
		
		while( list.getListSize() > 1 ) {
			
			list.setSelected(list.getListSize()-1); 
			delete(); 
			
		}

		adjusting = false; 
	}
	
	public int getListWidth() {
		
		return 800; 
	}

	public int[] getCodeList() {

		int size = list.getListSize();
		int[] codeList = new int[size-1]; 

		//skip over the first element in the list, which is always empty
		for( int i=1; i < size; i++ ) {
			
			codeList[i-1] = ((CalculatorListItem)list.getItemAt(i)).getCode(); //get the first code

		}

		return codeList;
	}
	
	protected class AddAction extends AbstractAction {

		public AddAction() {

			super( "", IconCache.getIcon("/guiIcons/Plus.png")  );
			putValue(SHORT_DESCRIPTION, "Add" );

		}

		public void actionPerformed(ActionEvent e) {
			
			addPlusSymbol(); 
			if( list.isLastElementSelected() )createNewFunction();
			
		}
	}
	
	protected class SubtractAction extends AbstractAction {

		public SubtractAction() {

			super( "", IconCache.getIcon("/guiIcons/Minus.png")  );
			putValue(SHORT_DESCRIPTION, "Subtract" );

		}

		public void actionPerformed(ActionEvent e) {
			
			addMinusSymbol(); 
			if( list.isLastElementSelected() )createNewFunction();
			
		}
	}
	
	public void addPlusSymbol() {
		
		ImageIcon opIcon = IconCache.getIcon("/operationIcons/PlusOp.png"); 
		
		Dimension opDimension = new Dimension( opIcon.getIconWidth()+20, getListHeight() ); 
		
		IconThumbnailGraphic graphic = new IconThumbnailGraphic( opIcon, opDimension, opDimension );
		
		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				CombineTermsRule.ADD, null, opDimension, opDimension ); 
		
		list.insertAfterSelected(newItem); 
		
	}
	
	public void addMinusSymbol() {
		
		ImageIcon opIcon = IconCache.getIcon("/operationIcons/SubtractOp.png"); 
		
		Dimension opDimension = new Dimension( opIcon.getIconWidth()+20, getListHeight() ); 
		
		IconThumbnailGraphic graphic = new IconThumbnailGraphic( opIcon, opDimension, opDimension );
		
		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				CombineTermsRule.SUBTRACT, null, opDimension, opDimension ); 
		
		list.insertAfterSelected(newItem); 
		
	}

	protected class MultiplyAction extends AbstractAction {

		public MultiplyAction() {

			super( "", IconCache.getIcon("/guiIcons/Times.png") );
			putValue(SHORT_DESCRIPTION, "Multiply" );

		}

		public void actionPerformed(ActionEvent e) {
			
			addTimesSymbol();
			if( list.isLastElementSelected() )createNewFunction();
			
		}

	}
	
	public void addTimesSymbol() {

		ImageIcon opIcon = IconCache.getIcon("/operationIcons/TimesOp.png"); 
		
		Dimension opDimension = new Dimension( opIcon.getIconWidth()+20, getListHeight() ); 
		
		IconThumbnailGraphic graphic = new IconThumbnailGraphic( opIcon, opDimension, opDimension );
		
		CalculatorListItem newItem = new CalculatorListItem( graphic, 
				CombineTermsRule.MULTIPLY, null, opDimension, opDimension ); 
		
		list.insertAfterSelected(newItem); 
	
	}


	protected class LeftParenAction extends AbstractAction {

		public LeftParenAction() {

			super( "", IconCache.getIcon("/guiIcons/LeftParen.png")  );
			putValue(SHORT_DESCRIPTION, "Open Parenthesis" );
		}

		public void actionPerformed(ActionEvent e) {
			
			addLeftParenSymbol();
		}

	}
	
	public void addLeftParenSymbol() {

		StringThumbnailGraphic leftParenGraphic = new StringThumbnailGraphic("(", 
				ThumbnailGraphic.getSmallDimension(), ThumbnailGraphic.getLargeDimension());

		list.insertAfterSelected( new CalculatorListItem( leftParenGraphic, getLeftParenCode(), null, 
				new Dimension( 40, GUIDimensions.PLOT_LIST_HEIGHT ), new Dimension( 100, GUIDimensions.PLOT_LIST_HEIGHT*2 ) ) );

		
	}

	protected class RightParenAction extends AbstractAction {

		public RightParenAction() {

			super( "", IconCache.getIcon("/guiIcons/RightParen.png")  );
			putValue(SHORT_DESCRIPTION, "Closed Parenthesis" );
		}

		public void actionPerformed(ActionEvent e) {

			addRightParenSymbol();
		}

	}
	
	public void addRightParenSymbol() {
		
		
		StringThumbnailGraphic rightParenGraphic = new StringThumbnailGraphic(")", 
				ThumbnailGraphic.getSmallDimension(), ThumbnailGraphic.getLargeDimension());

		list.insertAfterSelected( new CalculatorListItem( rightParenGraphic, getRightParenCode(), null, 
				new Dimension( 40, GUIDimensions.PLOT_LIST_HEIGHT ), new Dimension( 100, GUIDimensions.PLOT_LIST_HEIGHT*2 ) ) );

		
	}
	
	public abstract int getRightParenCode(); 
	public abstract int getLeftParenCode(); 


	protected class NewFunctionAction extends AbstractAction {

		public NewFunctionAction() {

			super( "", IconCache.getIcon("/guiIcons/functionPlot.png")  );
			putValue(SHORT_DESCRIPTION, "New Term" );
		}

		public void actionPerformed(ActionEvent e) {

			createNewFunction(); 
		}

	}
	
	public void delete() {
		
		if( list.getSelectedIndex() != 0 ) list.removeSelected(); 
	}

	public int getSelectedIndex() {
		
		return list.getSelectedIndex(); 
	}

	protected class DeleteAction extends AbstractAction {

		public DeleteAction() {

			super( "", IconCache.getIcon("/guiIcons/delete.png")  );
			putValue(SHORT_DESCRIPTION, "Delete" );
		}

		public void actionPerformed(ActionEvent e) {

			
			delete(); 

		}

	}

	public void valueChanged(ListSelectionEvent e) {

		if ( !e.getValueIsAdjusting() && list.getSelectedIndex() != -1 ) {

			adjusting = true; 
			updateDependents();
			adjusting = false; 
		}
	}
	
	protected void updateDependents() {
		
		if( addedNew ) {
			
			addedNew = false; 
			return; 
		}

		//check to see if the currently selected function is a function1Dterm
		CalculatorListItem selectedItem = (CalculatorListItem) list.getSelectedItem();
		Object value = selectedItem.getValue();
		boolean isFunction = value instanceof DataGenerator;
		if( isFunction ) {

			broadcaster.broadcast(this, GUIEvent.Descriptor.EXISTING_SELECTED, value ); 
		

		} else {

			broadcaster.broadcast(this, GUIEvent.Descriptor.NON_SELECTED, value );
		}
	}
	
	public abstract void createNewFunction();
	public abstract int getListHeight();
}