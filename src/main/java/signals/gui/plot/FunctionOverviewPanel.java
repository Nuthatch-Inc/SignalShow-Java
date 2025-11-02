package signals.gui.plot;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import signals.core.Core;
import signals.core.Deconstructable;
import signals.core.Function;
import signals.core.Constants.Part;
import signals.gui.ResizablePane;

@SuppressWarnings("serial")
public abstract class FunctionOverviewPanel extends JPanel implements MouseListener, ResizablePane, Deconstructable {

	protected Part[] parts = { Part.REAL_PART, Part.IMAGINARY_PART, Part.MAGNITUDE, Part.PHASE };
	public static final int REAL = 0;
	public static final int IMAG = 1;
	public static final int MAG = 2;
	public static final int PHASE = 3;
	public static final String THUMBNAIL = "thumb";
	public static final String FULLSCREEN = "full";
	protected CardLayout cardlayout;
	protected int largeSelectedIndex;
	protected JTextArea equationArea;
	protected JPanel plotPanel;
	protected JPanel thumbnailPanel; 
	protected DisplayCursorPanel[] plots; 
	protected DisplayCursorPanel largePlotPanel; 
	
	//size of a single plot 
	protected Dimension thumbSize;
	
	Function function; 
	
	public FunctionOverviewPanel( Function function ) {
		
		this(); 
		setFunction( function ); 
	}

	public FunctionOverviewPanel() {
		super();
		
		thumbnailPanel = new JPanel(); 
		thumbnailPanel.setBorder(Core.getBorders().getBuffer());
		
		//panel containing all of the plots
		plotPanel = new JPanel(); 
		
		GridLayout glayout = new GridLayout( 2, 2 ); 
		glayout.setVgap(5); 
		glayout.setHgap(5);
		thumbnailPanel.setLayout( glayout );
		
		largeSelectedIndex = -1;
		
		thumbSize = getThumbSize(); 
		
		cardlayout = new CardLayout(); 
		plotPanel.setLayout( cardlayout );
		
		setupPlots(); 
		
		//add plots
		for( int i=0; i < parts.length; i++ ) {
			
			plots[i].setBorder( Core.getBorders().getUnselectedFrameTransparent() );
			plots[i].addMouseListener( this );
			thumbnailPanel.add( plots[i] );
		}
		
		plotPanel.add( thumbnailPanel, THUMBNAIL ); 
		plotPanel.add( largePlotPanel, FULLSCREEN );
		cardlayout.show( plotPanel, THUMBNAIL );
		
		setLayout( new BorderLayout() ); 
		add( createEquationArea(), BorderLayout.NORTH ); 
		add( plotPanel, BorderLayout.CENTER ); 
		
		
	}
	
	public void deconstruct() {
		
		cardlayout = null;
		equationArea = null;
		plotPanel = null;
		thumbnailPanel = null;
		plots = null;
		largePlotPanel = null;
	}
	
	public JComponent createEquationArea() {
		
		equationArea = new JTextArea() {
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				// Limit height to prevent vertical expansion
				return new Dimension(d.width, Math.min(d.height, 60));
			}
			@Override
			public Dimension getMaximumSize() {
				return new Dimension(Integer.MAX_VALUE, 60);
			}
			@Override
			public Dimension getMinimumSize() {
				return new Dimension(1, 17);
			}
			@Override
			public void setSize(Dimension d) {
				// Clamp the height to prevent the component from being sized too tall
				super.setSize(new Dimension(d.width, Math.min(d.height, 60)));
			}
			@Override
			public void setSize(int width, int height) {
				// Clamp the height to prevent the component from being sized too tall
				super.setSize(width, Math.min(height, 60));
			}
		};
		equationArea.setBackground(new Color( 0, 0, 0, 0 )); 
		equationArea.setOpaque(false);  // Explicitly set non-opaque for transparency
		equationArea.setEditable(false);
		equationArea.setFocusable(false);
		equationArea.setLineWrap(true);
		equationArea.setFont(Core.getDisplayOptions().getLabelFont());
		
		return equationArea; 
	}
	
	public abstract Dimension getThumbSize(); 
	
	public void refreshText( String newName ) {
		
		if ( function != null ) { 
			function.setDescriptor(newName);
			equationArea.setText(function.getLongDescriptor());
			revalidate(); 
			repaint();
		}
	}
	
	public void setFunction( Function function ) {
		
		this.function = function;
		
		equationArea.setText(function.getLongDescriptor());
		
		if( largeSelectedIndex != -1 ) {
			
			largePlotPanel.removeAll(); 
			setLargePlot(parts[largeSelectedIndex]); 
		}
		
		refreshSmallPlots();
		
		revalidate(); 
		repaint();
	}
	
	
	public abstract void setupPlots(); 

	public FunctionOverviewPanel(LayoutManager layout) {
		super(layout);
	}

	public FunctionOverviewPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public FunctionOverviewPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public void sizeChanged() {
		
		for( DisplayCursorPanel plot : plots ) {
			
			plot.sizeChanged();
		}
		
		if( largePlotPanel!= null ) largePlotPanel.sizeChanged(); 
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {
		((JComponent) e.getSource()).setBorder( Core.getBorders().getSelectedFrame() );
	}

	public void mouseExited(MouseEvent e) {
		((JComponent) e.getSource()).setBorder( Core.getBorders().getUnselectedFrameTransparent() );
	}

	public void showThumbnails() {
		
		cardlayout.show( plotPanel, THUMBNAIL );
		largeSelectedIndex = -1;
	}

	public void mouseReleased(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {
		
		//find out which plot was clicked
		for( int i=0; i < parts.length; i++ ) {
			
			if( e.getSource().equals( plots[i] )) {
				
				//show a large plot
				largePlotPanel.removeAll(); 
				largeSelectedIndex = i;
				setLargePlot( parts[i] ); 
				largePlotPanel.setLogMagnitude(plots[i].isLogMagnitude());
				largePlotPanel.setPhaseUnwrapped(plots[i].isPhaseUnwrapped());
				largePlotPanel.setSquaredMagnitude(plots[i].isSquaredMagnitude());
				cardlayout.show( plotPanel, FULLSCREEN );
				break;
			}
		}
		
	}
	
	public abstract void setLargePlot( Part part ); 
	
	public abstract void refreshSmallPlots(); 

	public void updatePlots() {
		
		plots[largeSelectedIndex].setLogMagnitude(largePlotPanel.isLogMagnitude());
		plots[largeSelectedIndex].setSquaredMagnitude(largePlotPanel.isSquaredMagnitude());
		plots[largeSelectedIndex].setPhaseUnwrapped(largePlotPanel.isPhaseUnwrapped());
		showThumbnails();
	}
}