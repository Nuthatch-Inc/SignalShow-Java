package signals.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;

public class CalculatorListItem implements ThumbnailProducer {
	
	//the graphic associated with this item
	ThumbnailGraphic graphic; 
	
	//a code used for classifying this item
	int code; 
 	
	//the value of this item 
	Object value; 
	
	//sizes
	Dimension smallSize, largeSize; 


	/**
	 * @param graphic
	 * @param code
	 * @param value
	 * @param smallSize
	 * @param largeSize
	 */
	public CalculatorListItem(ThumbnailGraphic graphic, int code,
				Object value, Dimension smallSize, Dimension largeSize) {
		super();
		this.graphic = graphic;
		this.code = code;
		this.value = value;
		this.smallSize = smallSize;
		this.largeSize = largeSize;
	}

	public void paintLargeGraphic(Graphics2D g) {
		graphic.paintLargeGraphic(g);
		
	}

	public void paintSmallGraphic(Graphics2D g) {
		graphic.paintSmallGraphic(g);
		
	}
	
	/**
	 * @param graphic the graphic to set
	 */
	public void setGraphic(ThumbnailGraphic graphic) {
		this.graphic = graphic;
	}

	
	public void setDirty() {
		
		graphic.setDirty(); 
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	public Dimension getLargeThumbailSize() {
		return largeSize;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	public Dimension getSmallThumbnailSize() {
		return smallSize;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	public ThumbnailGraphic getGraphic() {
		return graphic;
	}

	public String getCompactDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLongDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void paintGraphic(Graphics2D g, int width, int height) {
		//graphic.paintGraphic(g, width, height);
		graphic.paintSmallGraphic(g);
	}

}
