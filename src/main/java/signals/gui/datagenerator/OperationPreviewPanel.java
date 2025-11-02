package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import signals.core.Function;
import signals.core.Function1D;
import signals.core.SourceData1D;
import signals.core.Constants.Part;
import signals.gui.plot.PlotCursorPanel;

/**
 * A panel that shows a small preview of an operation's output,
 * displaying the real and imaginary parts in stacked plots.
 * @author Juliet
 */
@SuppressWarnings("serial")
public class OperationPreviewPanel extends JPanel {
	
	private PlotCursorPanel realPlot;
	private PlotCursorPanel imagPlot;
	
	public OperationPreviewPanel() {
		setLayout(new BorderLayout());
		
		// Add padding around the entire panel
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
		
		// Create container for the two plots - use GridLayout for equal sizing
		JPanel plotsContainer = new JPanel();
		plotsContainer.setLayout(new BoxLayout(plotsContainer, BoxLayout.Y_AXIS));
		
		// Create real part plot
		realPlot = new PlotCursorPanel(new Dimension(320, 180));
		realPlot.setCompactLabelMode(true);
		plotsContainer.add(realPlot);
		
		plotsContainer.add(Box.createVerticalStrut(10));
		
		// Create imaginary part plot
		imagPlot = new PlotCursorPanel(new Dimension(320, 180));
		imagPlot.setCompactLabelMode(true);
		plotsContainer.add(imagPlot);
		
		add(plotsContainer, BorderLayout.CENTER);
		
		// Set preferred size for the plots
		setPreferredSize(new Dimension(350, 400));
		setMinimumSize(new Dimension(300, 300));
	}
	
	private void refreshPlots() {
		if (realPlot != null) {
			realPlot.getGraphic().sizeChanged();
			realPlot.getGraphic().calculateDimension();
			realPlot.revalidate();
			realPlot.repaint();
		}
		if (imagPlot != null) {
			imagPlot.getGraphic().sizeChanged();
			imagPlot.getGraphic().calculateDimension();
			imagPlot.revalidate();
			imagPlot.repaint();
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Updates the preview with a new function's output
	 * @param function The function to display
	 */
	public void updatePreview(Function function) {
		if (function == null) {
			clearPreview();
			return;
		}
		
		// Clear existing plots
		if (realPlot != null) {
			realPlot.removeAll();
		}
		if (imagPlot != null) {
			imagPlot.removeAll();
		}
		
		// Add new plots
		if (function instanceof Function1D && realPlot != null && imagPlot != null) {
			Function1D func1D = (Function1D) function;
			try {
				realPlot.addPlot(new SourceData1D(func1D, Part.REAL_PART, "Real Part of Result"));
				imagPlot.addPlot(new SourceData1D(func1D, Part.IMAGINARY_PART, "Imaginary Part of Result"));
			} catch (Exception e) {
				// Silently handle errors during preview generation
				e.printStackTrace();
			}
		}
		
		// Refresh plots with proper dimension calculation
		SwingUtilities.invokeLater(this::refreshPlots);
	}
	
	/**
	 * Clears all plots from the preview
	 */
	public void clearPreview() {
		if (realPlot != null) {
			realPlot.removeAll();
			realPlot.revalidate();
			realPlot.repaint();
		}
		if (imagPlot != null) {
			imagPlot.removeAll();
			imagPlot.revalidate();
			imagPlot.repaint();
		}
	}
}
