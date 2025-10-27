package signals.gui.datagenerator;

import signals.core.Function;

@SuppressWarnings("serial")
public class ReImFunction2DToolBar extends Function2DToolBar {
	
	public ReImFunction2DToolBar(Function function) {
		super(function);
	}
	
	public ReImFunction2DToolBar(Function function, boolean newState) {
		super(function, newState);
		// TODO Auto-generated constructor stub
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
