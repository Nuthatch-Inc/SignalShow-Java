package signals.gui.datagenerator;

import java.awt.Dimension;

import javax.swing.ListModel;

import signals.core.Core;
import signals.core.OperatorSystem;
import signals.gui.GUIDimensions;

@SuppressWarnings("serial")
public class Function2DSelectorPanel extends FunctionSelectorPanel {

	public Function2DSelectorPanel(GUIEventBroadcaster broadcaster, OperatorSystem excludeSystem) {
		super(broadcaster, excludeSystem);
	}
	
	@Override
	public ListModel getFunctionListModel() {
		return Core.getGUI().getFunction2DList( excludeSystem ).getModel(); 
	}
	
	@Override
	public Dimension getButtonSize() {

		return GUIDimensions.smallImageThumbnailDimension;
	}

}
