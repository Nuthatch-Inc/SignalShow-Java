package signals.gui.plot;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.ref.SoftReference;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import signals.core.Core;
import signals.core.Deconstructable;
import signals.core.SourceData2D;
import signals.core.Constants.Part;
import signals.gui.IconCache;
import signals.operation.ArrayMath;
import signals.operation.PhaseUnwrapper2D;

@SuppressWarnings("serial")
public class ImageCursorPanel extends DisplayCursorPanel implements Deconstructable {

	//actions
	AbstractAction cursorAction, leftAction, rightAction, upAction, downAction;

	ImageCursorGraphic graphic;

	double[] indices;
	int xDimension, yDimension; 
	int cursorIndexX, cursorIndexY;

	SourceData2D sourceData; 
	
	@SuppressWarnings("unchecked")
	SoftReference displayedData; 

	int xOffset, yOffset;
	
	JLabel label;

	public ImageCursorPanel( Dimension size ) {

		displayDimension = size;
		setLayout( new BorderLayout() ); 

		graphic = new ImageCursorGraphic( size );
		add( graphic, BorderLayout.CENTER );

		titlebarComponents = new ArrayList<Component>();

		titleBar = new JPanel(); 
		refreshTitleBar();
		add( titleBar, BorderLayout.NORTH );

		graphic.setImageCursorPanel(this);

		hasToolBar = false; 
		cursorModeOn = false;
	}
	
	public void deconstruct() {
		
		graphic.deconstruct(); 
		
	}
	
	public void setGraphic( ImageCursorGraphic graphic ) {
		
		remove( graphic );
		this.graphic = graphic;
		add( graphic, BorderLayout.CENTER );
		graphic.setImageCursorPanel(this);
	}
	
	public void refreshTitleBar() {
		
		titleBar.setBackground(Core.getColors().getBackgroundColor());
		titleBar.removeAll();
		label = new JLabel();
		label.setFont( Core.getDisplayOptions().getLabelFont() );
		titleBar.setOpaque( false );
		label.setOpaque( false );
		titleBar.add( label );
	
		//add the text to the labels
		if( cursorModeOn ) setCursorLabels(); else setTitleLabels();
	
		for( Component c : titlebarComponents ) {
	
			c.setBackground( Core.getColors().getBackgroundColor() );
			titleBar.add( c );
		}
	
		revalidate();
		repaint();
	}

	public void setImage( SourceData2D data ) {

		shutOffCursorMode();
		
		sourceData = data; 
		xDimension = data.getDimensionX();
		yDimension =  data.getDimensionY();
		graphic.setImage( data.getData(), xDimension, yDimension, data.isZeroCentered() ); 
		
		if( data.isZeroCentered() ) {
			
			xOffset = Math.max( 0, (yDimension - xDimension) / 2 ); 
			yOffset = Math.max( 0, (xDimension - yDimension) / 2 ); 
			
		} else {
			
			xOffset = 0;
			yOffset = Math.max( 0, (xDimension - yDimension) );
		}
		
		
		int dimension = Math.max( data.getDimensionX(), data.getDimensionY() );
		indices = Indices.indices1D(dimension, data.isZeroCentered() );

		if( hasToolBar ) { 
			leftAction.setEnabled( false );
			rightAction.setEnabled( false );
		}

		
		boolean isPhase = data.getPart().equals( Part.PHASE ); 
		graphic.setPiScaling(isPhase);
		
		//add phase unwrapping option
		if(isPhase) {

			if( unwrapPhaseCheckBox == null ) { 

				unwrapPhaseCheckBox = new JCheckBox("Unwrap");
				unwrapPhaseCheckBox.setOpaque( false );
				unwrapPhaseCheckBox.setFont(Core.getDisplayOptions().getLabelFont());
				unwrapPhaseCheckBox.addItemListener( new ItemListener() {

					public void itemStateChanged(ItemEvent e) {

						if( e.getStateChange() == ItemEvent.SELECTED ) {

							PhaseUnwrapper2D unwrapper = new PhaseUnwrapper2D( sourceData.getData(), sourceData.getDimensionX(), sourceData.getDimensionY() );
							graphic.setData( unwrapper.unwrap() );

						} else {

							graphic.setData( sourceData.getData() );
						}
						
						

						if( cursorModeOn ) setCursorLabels();
						repaint();

					}

				});

				titlebarComponents.add( unwrapPhaseCheckBox );

			} 

		} else if( data.getPart().equals( Part.MAGNITUDE )) {

			if( logMagnitudeCheckBox == null ) { 

				logMagnitudeCheckBox = new JCheckBox("Logarithm");
				logMagnitudeCheckBox.setOpaque( false );
				logMagnitudeCheckBox.setFont(Core.getDisplayOptions().getLabelFont());
				logMagnitudeCheckBox.addItemListener( new ItemListener() {

					public void itemStateChanged(ItemEvent e) {

						graphic.setData( ArrayMath.logSquare(sourceData.getData(), isLogMagnitude(), 
								isSquaredMagnitude()) ); 

						if( cursorModeOn ) setCursorLabels();
						repaint();
					}

				});

				titlebarComponents.add( logMagnitudeCheckBox );
				
				squaredMagnitudeCheckBox = new JCheckBox("Squared");
				squaredMagnitudeCheckBox.setOpaque( false );
				squaredMagnitudeCheckBox.setFont(Core.getDisplayOptions().getLabelFont());
				squaredMagnitudeCheckBox.addItemListener( new ItemListener() {

					public void itemStateChanged(ItemEvent e) {

						graphic.setData( ArrayMath.logSquare(sourceData.getData(), isLogMagnitude(), 
								isSquaredMagnitude()) );

						if( cursorModeOn ) setCursorLabels();
						repaint();
					}

				});

				titlebarComponents.add( squaredMagnitudeCheckBox );
			}
		}

		refreshTitleBar();
	}

	public void setCursorLocationX( double location ) {

		cursorIndexX = (int)(Math.max( 0, Math.min(location, indices.length-1)));
		setCursorLabels();
	}

	public void setCursorLocationY( double location ) {

		cursorIndexY = (int)(Math.max( 0, Math.min(location, indices.length-1)));
		setCursorLabels();
	}

	public class CursorAction extends AbstractAction {

		public CursorAction() {

			super( "", IconCache.getIcon("/plotIcons/cursor.png") );
		}

		public void actionPerformed(ActionEvent e) {

			graphic.addMouseListeners();
			cursorIndexX = indices.length / 2; //center index
			cursorIndexY = indices.length / 2; //center index
			graphic.setCursorLocationX( cursorIndexX );
			graphic.setCursorLocationY( cursorIndexY );
			graphic.setCursorOn( true );
			cursorModeOn = true;

			leftAction.setEnabled( true ); 
			rightAction.setEnabled( true ); 
			upAction.setEnabled( true );
			downAction.setEnabled( true );

			setCursorLabels();
		} 

	}

	public class LeftAction extends AbstractAction {


		public LeftAction() {

			super( "", IconCache.getIcon("/plotIcons/leftArrow.png") );
		}

		public void actionPerformed(ActionEvent e) {

			int newCursorIndex = cursorIndexX - 1; 
			if( (newCursorIndex - xOffset ) >= 0 ) {

				cursorIndexX = newCursorIndex; 
				graphic.setCursorLocationX( cursorIndexX );
				setCursorLabels(); 
			} 

		} 

	}

	public class RightAction extends AbstractAction {


		public RightAction() {

			super( "", IconCache.getIcon("/plotIcons/rightArrow.png") );
		}

		public void actionPerformed(ActionEvent e) {

			int newCursorIndex = cursorIndexX + 1; 
			if( (newCursorIndex-xOffset) < indices.length ) {

				cursorIndexX = newCursorIndex; 
				graphic.setCursorLocationX( cursorIndexX );
				setCursorLabels(); 
			}
		} 

	}

	public class UpAction extends AbstractAction {

		public UpAction() {

			super( "", IconCache.getIcon("/plotIcons/upArrow.png") );
		}

		public void actionPerformed(ActionEvent e) {

			int newCursorIndex = cursorIndexY + 1; 
			if( (newCursorIndex+yOffset) < indices.length ) {

				cursorIndexY = newCursorIndex; 
				graphic.setCursorLocationY( cursorIndexY );
				setCursorLabels(); 
			} 

		} 

	}

	public class DownAction extends AbstractAction {


		public DownAction() {

			super( "", IconCache.getIcon("/plotIcons/downArrow.png") );
		}

		public void actionPerformed(ActionEvent e) {

			int newCursorIndex = cursorIndexY - 1; 
			if( (newCursorIndex+yOffset) >= 0 ) {

				cursorIndexY = newCursorIndex; 
				graphic.setCursorLocationY( cursorIndexY );
				setCursorLabels(); 
			}
		} 

	}
	
	public void setTitleLabels() {
		
		if( sourceData != null ) label.setText( sourceData.getCompactLabel() );
	}

	@SuppressWarnings("unchecked")
	public void setCursorLabels() {
	
		NumberFormat formatter = Core.getDisplayOptions().getFormat();
	
		if( cursorIndexX < indices.length && cursorIndexY < indices.length && 
				cursorIndexX >= 0 && cursorIndexY >= 0) {
		
			int x = cursorIndexX - xOffset; 
			int y = cursorIndexY + yOffset;
			
			int index = ( indices.length - 1 - y ) * xDimension + x;
			String value = "0"; 
			if( index < (xDimension * yDimension) && index >= 0 ) { //in bounds
				
				
				if( displayedData == null ) { 
					
					if( sourceData.getPart() == Part.PHASE ) {
						
						if( unwrapPhaseCheckBox != null && unwrapPhaseCheckBox.isSelected() ) {
							
							PhaseUnwrapper2D unwrapper = new PhaseUnwrapper2D( sourceData.getData(), sourceData.getDimensionX(), sourceData.getDimensionY() );
							displayedData = new SoftReference( unwrapper.unwrap() ) ; 
						
						} else {
							
							displayedData = new SoftReference( sourceData.getData() ); 
						}
						
					} else if ( sourceData.getPart() == Part.MAGNITUDE )  {
						
						if( logMagnitudeCheckBox != null && squaredMagnitudeCheckBox != null ) {
							
							displayedData = new SoftReference( ArrayMath.logSquare(sourceData.getData(), logMagnitudeCheckBox.isSelected(), 
									squaredMagnitudeCheckBox.isSelected()) ); 
						} else {
							
							displayedData = new SoftReference( sourceData.getData() ) ; 
						}
					} else {
						
						displayedData = new SoftReference( sourceData.getData() ); 
					}
						
				} // null condition
				
				if( sourceData.getPart() == Part.PHASE ) {
				
					value = formatter.format( ((double[])displayedData.get())[index]/Math.PI ) + "\u03C0)"; 
					
				} else {
					
					value = formatter.format( ((double[])displayedData.get())[index] ); 
				}
			}
			
			label.setText( sourceData.getCompactLabel() + ": (" + indices[cursorIndexX] + ", " +
					"" + indices[cursorIndexY] + " ): " + value );
	
		}
	}

	public void removeAll() {
	
		shutOffCursorMode();
		sourceData = null;
		titlebarComponents = new ArrayList<Component>();
		unwrapPhaseCheckBox = null;
		logMagnitudeCheckBox = null;
		refreshTitleBar();
	}

	/**
	 * Creates the toolbar and adds it
	 *
	 */
	public void showToolBar() {
	
		toolBar = new JPanel(); 
		add( toolBar, BorderLayout.SOUTH );
		hasToolBar = true;
	
		cursorAction = new CursorAction();
		leftAction = new LeftAction(); 
		rightAction = new RightAction(); 
		upAction = new UpAction();
		downAction = new DownAction();
	
		leftAction.setEnabled( false ); 
		rightAction.setEnabled( false );
		upAction.setEnabled( false );
		downAction.setEnabled( false );
	
		toolBar.add( new JButton( cursorAction) );
		toolBar.add( new JButton(leftAction) ); 
		toolBar.add( new JButton(rightAction) );
		toolBar.add( new JButton(upAction) );
		toolBar.add( new JButton(downAction) );
		toolBar.add( Box.createHorizontalGlue() );
	
		toolBar.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("LEFT"), "left_ar");
		toolBar.getActionMap().put("left_ar", leftAction );
	
		toolBar.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("RIGHT"), "right_ar");
		toolBar.getActionMap().put("right_ar", rightAction );
	
		toolBar.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("UP"), "up_ar");
		toolBar.getActionMap().put("up_ar", upAction );
	
		toolBar.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("DOWN"), "down_ar");
		toolBar.getActionMap().put("down_ar", downAction );
	}

	public void shutOffCursorMode() {
	
		if( cursorModeOn ) {
			cursorModeOn = false;
			leftAction.setEnabled( false ); 
			rightAction.setEnabled( false ); 
			upAction.setEnabled( false );
			downAction.setEnabled( false );
			graphic.setCursorOn(false);
		}
	
	}

	@Override
	public void sizeChanged() {
		// TODO Auto-generated method stub
		
	}

}
