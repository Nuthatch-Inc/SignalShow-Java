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
import signals.core.Function2D;
import signals.core.SourceData1D;
import signals.core.Constants.Part;
import signals.gui.ResizablePane;
import signals.gui.plot.ImageDisplayPanel;
import signals.gui.plot.PlotCursorPanel;

/**
 * A panel that shows a small preview of an operation's output,
 * displaying the real and imaginary parts in stacked plots (1D) or side-by-side images (2D).
 * @author Juliet
 */
@SuppressWarnings("serial")
public class OperationPreviewPanel extends JPanel implements ResizablePane {
	
	private PlotCursorPanel realPlot;
	private PlotCursorPanel imagPlot;
	
	// 2D image display panels
	private ImageDisplayPanel realImagePanel;
	private ImageDisplayPanel imagImagePanel;
	
	// Container panels for switching between 1D and 2D displays
	private JPanel plotsContainer1D;
	private JPanel plotsContainer2D;
	
	/**
	 * Creates a new operation preview panel
	 * Defaults to 1D mode
	 */
	public OperationPreviewPanel() {
		this(false);
	}
	
	/**
	 * Creates a new operation preview panel
	 * @param startIn2DMode if true, starts with 2D image display; if false, starts with 1D plots
	 */
	public OperationPreviewPanel(boolean startIn2DMode) {
		setLayout(new BorderLayout());
		
		// Add padding around the entire panel
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
		
		// Create 1D plots container (vertical stacking)
		plotsContainer1D = new JPanel();
		plotsContainer1D.setLayout(new BoxLayout(plotsContainer1D, BoxLayout.Y_AXIS));
		
		// Create real part plot
		realPlot = new PlotCursorPanel(new Dimension(320, 180));
		realPlot.setCompactLabelMode(true);
		plotsContainer1D.add(realPlot);
		
		plotsContainer1D.add(Box.createVerticalStrut(10));
		
		// Create imaginary part plot
		imagPlot = new PlotCursorPanel(new Dimension(320, 180));
		imagPlot.setCompactLabelMode(true);
		plotsContainer1D.add(imagPlot);
		
		// Create 2D images container (vertical stacking)
		plotsContainer2D = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				// Calculate square size based on available width, maintaining aspect ratio
				Dimension parentSize = getParent() != null ? getParent().getSize() : new Dimension(400, 600);
				int availableWidth = Math.max(100, parentSize.width - 20); // Account for padding
				int imageSize = Math.max(100, availableWidth - 20); // Leave some margin
				return new Dimension(availableWidth, imageSize * 2 + 80); // Two images plus spacing and borders
			}
		};
		plotsContainer2D.setLayout(new BoxLayout(plotsContainer2D, BoxLayout.Y_AXIS));
		
		// Create real part image panel with border
		JPanel realImageContainer = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				Dimension parentSize = getParent() != null ? getParent().getSize() : new Dimension(400, 300);
				int availableWidth = Math.max(100, parentSize.width - 20);
				int imageSize = Math.max(100, availableWidth - 20);
				return new Dimension(availableWidth, imageSize + 30); // Image plus border
			}
		};
		realImageContainer.setBorder(BorderFactory.createTitledBorder("Real Part of Result"));
		realImagePanel = new ImageDisplayPanel(new Dimension(200, 200));
		realImageContainer.add(realImagePanel, BorderLayout.CENTER);
		plotsContainer2D.add(realImageContainer);
		
		plotsContainer2D.add(Box.createVerticalStrut(10));
		
		// Create imaginary part image panel with border
		JPanel imagImageContainer = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				Dimension parentSize = getParent() != null ? getParent().getSize() : new Dimension(400, 300);
				int availableWidth = Math.max(100, parentSize.width - 20);
				int imageSize = Math.max(100, availableWidth - 20);
				return new Dimension(availableWidth, imageSize + 30); // Image plus border
			}
		};
		imagImageContainer.setBorder(BorderFactory.createTitledBorder("Imaginary Part of Result"));
		imagImagePanel = new ImageDisplayPanel(new Dimension(200, 200));
		imagImageContainer.add(imagImagePanel, BorderLayout.CENTER);
		plotsContainer2D.add(imagImageContainer);
		
		// Start with the appropriate container based on mode
		if (startIn2DMode) {
			add(plotsContainer2D, BorderLayout.CENTER);
		} else {
			add(plotsContainer1D, BorderLayout.CENTER);
		}
		
		// Set minimum size constraint (allow smaller sizes for flexibility)
		setMinimumSize(new Dimension(100, 200));
	}
	
	@Override
	public void sizeChanged() {
		// Notify child plot components of size change and force recalculation
		if (realPlot != null) {
			realPlot.sizeChanged();
			realPlot.getGraphic().calculateDimension();
			realPlot.revalidate();
			realPlot.repaint();
		}
		if (imagPlot != null) {
			imagPlot.sizeChanged();
			imagPlot.getGraphic().calculateDimension();
			imagPlot.revalidate();
			imagPlot.repaint();
		}
		// ImageDisplayPanel doesn't have sizeChanged(), but the container's getPreferredSize()
		// override will handle dynamic resizing for 2D images
		if (realImagePanel != null) {
			realImagePanel.revalidate();
			realImagePanel.repaint();
		}
		if (imagImagePanel != null) {
			imagImagePanel.revalidate();
			imagImagePanel.repaint();
		}
		revalidate();
		repaint();
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
	
	private void refreshImages() {
		if (realImagePanel != null) {
			realImagePanel.revalidate();
			realImagePanel.repaint();
		}
		if (imagImagePanel != null) {
			imagImagePanel.revalidate();
			imagImagePanel.repaint();
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
		
		if (function instanceof Function1D) {
			// Switch to 1D display if needed
			if (getComponentCount() > 0 && getComponent(0) != plotsContainer1D) {
				removeAll();
				add(plotsContainer1D, BorderLayout.CENTER);
			}
			
			// Clear existing plots
			if (realPlot != null) {
				realPlot.removeAll();
			}
			if (imagPlot != null) {
				imagPlot.removeAll();
			}
			
			// Add new plots
			if (realPlot != null && imagPlot != null) {
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
			
		} else if (function instanceof Function2D) {
			// Switch to 2D display if needed
			if (getComponentCount() > 0 && getComponent(0) != plotsContainer2D) {
				removeAll();
				add(plotsContainer2D, BorderLayout.CENTER);
			}
			
			Function2D func2D = (Function2D) function;
			try {
				// Get dimensions
				int dimX = func2D.getDimensionX();
				int dimY = func2D.getDimensionY();
				boolean zeroCentered = func2D.isZeroCentered();
				
				// Get data
				double[] realData = function.getPart(Part.REAL_PART);
				double[] imagData = function.getPart(Part.IMAGINARY_PART);
				
				// Setup and display real part
				if (realImagePanel != null && realData != null) {
					realImagePanel.setIndices(dimX, dimY, zeroCentered);
					realImagePanel.display(realData);
				}
				
				// Setup and display imaginary part
				if (imagImagePanel != null && imagData != null) {
					imagImagePanel.setIndices(dimX, dimY, zeroCentered);
					imagImagePanel.display(imagData);
				}
				
			} catch (Exception e) {
				// Silently handle errors during preview generation
				e.printStackTrace();
			}
			
			// Refresh images
			SwingUtilities.invokeLater(this::refreshImages);
		}
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
		if (realImagePanel != null) {
			realImagePanel.revalidate();
			realImagePanel.repaint();
		}
		if (imagImagePanel != null) {
			imagImagePanel.revalidate();
			imagImagePanel.repaint();
		}
	}
}
