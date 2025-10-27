package signals.gui.plot;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import signals.core.FunctionTerm1D;
import signals.functionterm.PolarFunctionTerm2D;
import signals.functionterm.SeparableFunctionTerm2D;
import signals.gui.GUIDimensions;

@SuppressWarnings("serial")
public class SeparableFunctionTerm2DPreviewPanel extends JPanel {

	FunctionTerm2DThumbnailGraphic graphic; 
	
	FunctionTerm1DThumbnailGraphic part1, part2;
	JLabel label1, label2;
	
	FunctionTerm2DDisplayPanel largePanel; 
	SmallPanel smallPanel1, smallPanel2; 
	
	public SeparableFunctionTerm2DPreviewPanel() {
	
		largePanel = new FunctionTerm2DDisplayPanel( GUIDimensions.largeImageThumbnailDimension );
		smallPanel1 = new SmallPanel();
		smallPanel2 = new SmallPanel();
		
		Font italicFont = new Font ("Helvetica", Font.ITALIC, 14);
		
		label1 = new JLabel(); 
		label1.setFont( italicFont ); 
		label2 = new JLabel();
		label2.setFont( italicFont ); 
		
		JPanel plotPanel = new JPanel(); 
		plotPanel.setLayout( new BoxLayout( plotPanel, BoxLayout.PAGE_AXIS ) );

		plotPanel.add( label1 ); 
		plotPanel.add( smallPanel1 );
		plotPanel.add( Box.createVerticalStrut( 50 ) );
		plotPanel.add( label2 ); 
		plotPanel.add( smallPanel2 );
		
		add( largePanel ); 
		add( plotPanel );
		
		setBorder( BorderFactory.createEmptyBorder( 20, 0 , 0, 0 ) );
		
	}
	
	public void setFunction(SeparableFunctionTerm2D function, int xDimension, int yDimension, boolean zeroCentered ) {
		
		double[] a_indices = null, b_indices = null; 
		
		if( function instanceof PolarFunctionTerm2D ) {
			
			int dimension = Math.max( xDimension, yDimension ); 
			 a_indices = Indices.indices1D(dimension, zeroCentered);
			 b_indices = Indices.indices1D(dimension, zeroCentered);
			
		} else {
			
			 a_indices = Indices.indices1D(xDimension, zeroCentered);
			 b_indices = Indices.indices1D(yDimension, zeroCentered);
		}
		
		FunctionTerm1D partA = function.getFunctionTerm1DA(); 
		label1.setIcon(function.getPartAIcon());
		part1 = new FunctionTerm1DThumbnailGraphic( partA, a_indices );
		smallPanel1.term = part1; 
		smallPanel1.repaint(); 
		
		FunctionTerm1D partB = function.getFunctionTerm1DB();
		label2.setIcon(function.getPartBIcon());
		part2 = new FunctionTerm1DThumbnailGraphic( partB, b_indices );
		smallPanel2.term = part2;
		smallPanel2.repaint();
		
		largePanel.setIndices(xDimension, yDimension, zeroCentered);
		largePanel.setFunctionTerm(function);
	}

	public class SmallPanel extends JPanel {
		
		FunctionTerm1DThumbnailGraphic term; 
		
		public SmallPanel() {
			
			setPreferredSize( FunctionTerm1DThumbnailGraphic.getSmallDimension() );
		}
		
		public void paintComponent( Graphics g ) {
			
			super.paintComponent(g);
			if( term != null ) term.paintSmallGraphic((Graphics2D)g);
		}
	}
}
