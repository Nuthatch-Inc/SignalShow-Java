package signals.gui;

import java.awt.Dimension;

import javax.swing.ImageIcon;

public class IconListItem {
	
	//small and large objects representing the value of this cell
	ImageIcon smallIcon, largeIcon;
	
	//value of this cell
	Object value; 

	/**
	 * @param smallIcon
	 * @param largeIcon
	 */
	public IconListItem(Object value, ImageIcon smallIcon, ImageIcon largeIcon ) {
		super();
		this.value = value;
		this.smallIcon = smallIcon;
		this.largeIcon = largeIcon;
	}

	public Dimension getLargeIconSize() {
		return GUIDimensions.largeLabelDimension; 
	}

	public Dimension getSmallIconSize() {
		return GUIDimensions.smallLabelDimension;
	}

	/**
	 * @return the largeIcon
	 */
	public ImageIcon getLargeIcon() {
		return largeIcon;
	}

	/**
	 * @param largeIcon the largeIcon to set
	 */
	public void setLargeIcon(ImageIcon largeIcon) {
		this.largeIcon = largeIcon;
	}

	/**
	 * @return the smallIcon
	 */
	public ImageIcon getSmallIcon() {
		return smallIcon;
	}

	/**
	 * @param smallIcon the smallIcon to set
	 */
	public void setSmallIcon(ImageIcon smallIcon) {
		this.smallIcon = smallIcon;
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


}
