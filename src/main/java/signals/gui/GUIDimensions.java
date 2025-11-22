package signals.gui;

import java.awt.Dimension;

public class GUIDimensions {
	
	public static final int LIST_WIDTH = 128; 
	public static final int PLOT_LIST_HEIGHT = 100;
	public static final int OFFSET = 20;
	
	public static Dimension smallPlotThumbnailDimension,
	largePlotThumbnailDimension, smallImageThumbnailDimension, 
	largeImageThumbnailDimension, smallLabelDimension, 
	largeLabelDimension; 
	
	static { 
		
		smallPlotThumbnailDimension = new Dimension( LIST_WIDTH, PLOT_LIST_HEIGHT );
		largePlotThumbnailDimension = new Dimension( LIST_WIDTH*3, PLOT_LIST_HEIGHT*3 );
		smallImageThumbnailDimension = new Dimension( LIST_WIDTH, LIST_WIDTH );
		largeImageThumbnailDimension = new Dimension( 256, 256 );
		smallLabelDimension = new Dimension( LIST_WIDTH, 45 );
		largeLabelDimension = new Dimension( LIST_WIDTH, 110 );
		
	}

}
