package signals.gui.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import signals.core.Core;
import signals.core.SourceData1D;
import signals.core.Constants.Part;
import signals.core.Constants.PlotStyle;
import signals.gui.IconCache;
import signals.gui.ResizablePane;
import signals.operation.ArrayMath;
import signals.operation.PhaseUnwrapper1D;

/**
 * An interactive panel containing a plot graphic, a titlebar, 
 * and a toolbar with functionality including: 
 * -cursor
 * -scaling/windowing/autofit
 * -painting options
 * @author Juliet
 *
 */
@SuppressWarnings("serial")
public class PlotCursorPanel extends DisplayCursorPanel implements ResizablePane {

	//actions
	AbstractAction paintAction, windowfitAction, autofitAction, cursorAction, leftAction, rightAction;

	//graphic
	PlotGraphic graphic;

	double[] indices; 
	int cursorIndex;
	
	ArrayList<JLabel> labels;

	//source data
	ArrayList<SourceData1D> sourceData;
	
	boolean compactLabelMode; 

	public PlotCursorPanel( Dimension plotSize ) {

		setLayout( new BorderLayout() ); 
		
		sourceData = new ArrayList<SourceData1D>();
		titlebarComponents = new ArrayList<Component>();
		
		createGraphic( plotSize ); 
		add( graphic, BorderLayout.CENTER );
		
		graphic.setPlotPanel( this );

		titleBar = new JPanel(); 
		refreshTitleBar();
		add( titleBar, BorderLayout.NORTH );

		hasToolBar = false; 
		cursorModeOn = false;
		compactLabelMode = true; 
	
	}
	
	public Color getColor( int graphicIndex ) {
		
		if( graphic.getDataSequences().size() > graphicIndex ) {
			
			return graphic.getDataSequences().get(graphicIndex).getColor(); 
		}
		
		return Color.white; 
	}
	
	public void setColor( int graphicIndex, Color color ) {
		
		if( graphic.getDataSequences().size() > graphicIndex ) {
			
			graphic.getDataSequences().get(graphicIndex).setColor(color); 
			labels.get(graphicIndex).setForeground( color );
		}
		
	}
	
	public void setPlotStyle( int graphicIndex, PlotStyle style ) {
		
		if( graphic.getDataSequences().size() > graphicIndex ) {
			
			graphic.getDataSequences().get(graphicIndex).setPlotStyle(style); 
		}
		
	}
	
	public int getNumberPlots() {
		
		return graphic.getDataSequences().size(); 
	}
	
	public void createGraphic( Dimension plotSize ) {
		
		graphic = new PlotGraphic( plotSize );
	}


	public boolean isCompactLabelMode() {
		return compactLabelMode;
	}


	public void setCompactLabelMode(boolean compactLabelMode) {
		this.compactLabelMode = compactLabelMode;
	}


	public void sizeChanged() {
		
		graphic.sizeChanged();
	}
	
	public PlotGraphic getGraphic() {
		return graphic;
	}
	
	public void lockScale( SourceData1D data ) {
		
		graphic.lockScale(data.getIndices1D(), data.getData()); 
	}

	public void addPlot( SourceData1D data ) {

		indices = data.getIndices1D();
		graphic.add( indices, data.getData(), compactLabelMode ? data.getCompactLabel() : data.getLongLabel() );
		sourceData.add(data);
		
		if( hasToolBar ) { 
			leftAction.setEnabled( false );
			rightAction.setEnabled( false );
		}
		
		//add phase unwrapping option
		if( data.getPart().equals( Part.PHASE )) {
			
			graphic.setPiScaledYAxis(true);
		
			if( unwrapPhaseCheckBox == null ) { 
			
				unwrapPhaseCheckBox = new JCheckBox("Unwrap");
				unwrapPhaseCheckBox.setFont(Core.getDisplayOptions().getLabelFont());
				unwrapPhaseCheckBox.addItemListener( new ItemListener() {
	
					public void itemStateChanged(ItemEvent e) {
						
						for( int i = 0; i < sourceData.size(); i++ ) {
							
							if( sourceData.get(i).getPart().equals( Part.PHASE ) ) {
								
								if( e.getStateChange() == ItemEvent.SELECTED ) {
									
									
									int startingIndex = ( indices[0]==0 ? 0 : indices.length/2 );
									graphic.getDataSequences().get(i).setYData(
											PhaseUnwrapper1D.unwrapItoh( sourceData.get(i).getData(), startingIndex ), 
												true);
									
								} else {
									
									graphic.getDataSequences().get(i).setYData(
											sourceData.get(i).getData(), true );
									
								}
								
								if( cursorModeOn ) setCursorLabels();
								repaint();
								
							}
							
						}
						
					}

				});
				
				titlebarComponents.add( unwrapPhaseCheckBox );
			}
		} else if( data.getPart().equals( Part.MAGNITUDE )) {
		
			if( logMagnitudeCheckBox == null ) { 
			
				logMagnitudeCheckBox = new JCheckBox("Logarithm");
				logMagnitudeCheckBox.setFont(Core.getDisplayOptions().getLabelFont());
				logMagnitudeCheckBox.addItemListener( new ItemListener() {
	
					public void itemStateChanged(ItemEvent e) {
						
						for( int i = 0; i < sourceData.size(); i++ ) {
							

								graphic.getDataSequences().get(i).setYData( 
											ArrayMath.logSquare(sourceData.get(i).getData(), 
													isLogMagnitude(), 
													isSquaredMagnitude()), true );
								
							}

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

						for( int i = 0; i < sourceData.size(); i++ ) {
							

							graphic.getDataSequences().get(i).setYData( 
										ArrayMath.logSquare(sourceData.get(i).getData(), 
												isLogMagnitude(), 
												isSquaredMagnitude()), true );


						}

						if( cursorModeOn ) setCursorLabels();
						repaint();
					}

				});

				titlebarComponents.add( squaredMagnitudeCheckBox );
			}
		}
		
		refreshTitleBar();
	}
	
	/**
	 * @param defaultPlotStyle the defaultPlotStyle to set
	 */
	public void setDefaultPlotStyle(PlotStyle defaultPlotStyle) {
		graphic.setDefaultPlotStyle(defaultPlotStyle);
	}
	
	public PlotStyle getDefaultPlotStyle() {
		
		return graphic.getDefaultPlotStyle(); 
	}

	public void setCursorLocation( double location ) {
		
		cursorIndex = (int)(location - indices[0]);
		setCursorLabels();
	}
	
	//sets up the title bar
	public void refreshTitleBar() {

		titleBar.setBackground(Core.getColors().getBackgroundColor());
		titleBar.removeAll();
		labels = new ArrayList<JLabel>();
		ArrayList<PlotGraphic.DataSequence> dataSequences = graphic.getDataSequences();

		for( PlotGraphic.DataSequence sequence : dataSequences ) {

			JLabel cursorLabel = new JLabel();
			cursorLabel.setFont( Core.getDisplayOptions().getLabelFont());
			cursorLabel.setBackground( Core.getColors().getBackgroundColor() );
			cursorLabel.setForeground( sequence.getColor() );
			cursorLabel.setOpaque( true );
			titleBar.add( cursorLabel );
			labels.add( cursorLabel );

		}
		
		//add the text to the labels
		if( cursorModeOn ) setCursorLabels(); else setTitleLabels();

		for( Component c : titlebarComponents ) {
			
			c.setBackground( Core.getColors().getBackgroundColor() );
			titleBar.add( c );
		}
 
		
		revalidate();
		repaint();
	}
	
	public void setTitleLabels() {
		
		ArrayList<PlotGraphic.DataSequence> dataSequences = graphic.getDataSequences();
		Iterator<JLabel> labelsIter = labels.iterator();
		for( PlotGraphic.DataSequence sequence : dataSequences ) {
			
			labelsIter.next().setText( sequence.getName() );
		}
	}
	
	public void setCursorLabels() {
		
		NumberFormat formatter = Core.getDisplayOptions().getFormat();
		
		ArrayList<PlotGraphic.DataSequence> dataSequences = graphic.getDataSequences();
		Iterator<JLabel> labelsIter = labels.iterator();
		int i = 0; 
		for( PlotGraphic.DataSequence sequence : dataSequences ) {
			
			if( sourceData.get(i++).getPart() == Part.PHASE ) {
				
				labelsIter.next().setText( sequence.getName() + ": (" + 
						formatter.format(sequence.xData[cursorIndex]) + ", " + 
						formatter.format(sequence.yData[cursorIndex]/Math.PI) + "\u03C0)" );
				
			} else {
			
				labelsIter.next().setText( sequence.getName() + ": (" + 
						formatter.format(sequence.xData[cursorIndex]) + ", " + 
						formatter.format(sequence.yData[cursorIndex]) + ")" );
			}
			
		}
		
	}

	public void removeAll() {

		graphic.removeAll();
		sourceData = new ArrayList<SourceData1D>();
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
		
		leftAction.setEnabled( false ); 
		rightAction.setEnabled( false );
		
		toolBar.add( new JButton(cursorAction) );
		toolBar.add( new JButton(leftAction) ); 
		toolBar.add( new JButton(rightAction) );
		toolBar.add( Box.createHorizontalGlue() );
		
		toolBar.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("LEFT"), "left_ar");
		toolBar.getActionMap().put("left_ar", leftAction );

		toolBar.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put(KeyStroke.getKeyStroke("RIGHT"), "right_ar");
		toolBar.getActionMap().put("right_ar", rightAction );
	}

	public void cursorActionCode() {
		
		graphic.addMouseListeners();
		cursorIndex = indices.length / 2; //center index
		graphic.setCursorLocation( indices[cursorIndex] );
		graphic.setCursorOn( true );
		cursorModeOn = true;
		
		leftAction.setEnabled( true ); 
		rightAction.setEnabled( true ); 
		
		setCursorLabels();
		
	}
	
	public class CursorAction extends AbstractAction {

		public CursorAction() {

			super( "", IconCache.getIcon("/plotIcons/cursor.png") );
		}

		public void actionPerformed(ActionEvent e) {

			cursorActionCode(); 
		} 

	}

//	public class AutofitAction extends AbstractAction {
//
//
//		public AutofitAction() {
//
//		
//		}
//
//		public void actionPerformed(ActionEvent e) {
//
//		} 
//
//	}
//	public class WindowfitAction extends AbstractAction {
//
//
//		public WindowfitAction() {
//
//			
//		}
//
//		public void actionPerformed(ActionEvent e) {
//
//		} 
//
//	}

	public void moveLeft() {
		
		int newCursorIndex = cursorIndex - 1; 
		if( newCursorIndex >= 0 ) {
			
			cursorIndex = newCursorIndex; 
			graphic.setCursorLocation( indices[cursorIndex] );
			setCursorLabels(); 
		} 
	}
	
	public class LeftAction extends AbstractAction {


		public LeftAction() {

			super( "",IconCache.getIcon("/plotIcons/leftArrow.png") );
		}

		public void actionPerformed(ActionEvent e) {
			
			moveLeft(); 

		} 

	}
	
	public void moveRight() {
		
		int newCursorIndex = cursorIndex + 1; 
		if( newCursorIndex < indices.length ) {
			
			cursorIndex = newCursorIndex; 
			graphic.setCursorLocation( indices[cursorIndex] );
			setCursorLabels(); 
		}
		
	}

	public class RightAction extends AbstractAction {


		public RightAction() {

			super( "", IconCache.getIcon("/plotIcons/rightArrow.png") );
		}

		public void actionPerformed(ActionEvent e) {

			moveRight(); 
		} 

	}

}
