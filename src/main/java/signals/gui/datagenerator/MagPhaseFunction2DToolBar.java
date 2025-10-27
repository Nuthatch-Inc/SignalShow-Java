package signals.gui.datagenerator;

import signals.core.Function;

@SuppressWarnings("serial")
public class MagPhaseFunction2DToolBar extends Function2DToolBar {
	
	public MagPhaseFunction2DToolBar(Function function) {
		super(function);
	}
	
	public MagPhaseFunction2DToolBar(Function function, boolean newState) {
		super(function, newState);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getPartAName() {
	
		return "Magnitude";
	}

	@Override
	public String getPartBName() {
	
		return "Phase";
	}
	
}
