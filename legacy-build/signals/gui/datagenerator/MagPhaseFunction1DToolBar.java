package signals.gui.datagenerator;

import signals.core.Function;

@SuppressWarnings("serial")
public class MagPhaseFunction1DToolBar extends Function1DToolBar {
	
	public MagPhaseFunction1DToolBar(Function function) {
		super(function);
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
