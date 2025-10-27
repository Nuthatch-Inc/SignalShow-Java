package signals.gui.datagenerator;

import signals.core.Function;

@SuppressWarnings("serial")
public class ReImFunction1DToolBar extends Function1DToolBar {
	
	public ReImFunction1DToolBar(Function function) {
		super(function);
	}

	@Override
	public String getPartAName() {
	
		return "Real Part";
	}

	@Override
	public String getPartBName() {
	
		return "Imaginary Part";
	}
	
}
