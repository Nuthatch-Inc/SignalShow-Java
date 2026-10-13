package signals.gui.datagenerator;

import signals.core.Core;
import signals.core.Function;
import signals.core.OperatorSystem;
import signals.gui.plot.Function2DOverviewPanel;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public class OperatorSystemToolBar2D extends OperatorSystemToolBar {

	public OperatorSystemToolBar2D(OperatorSystem system) {
		super(system);
	}

	@Override
	public void plot() {
		Function result = getResultForView();
		if (result == null) return;
		Function2DOverviewPanel plot = new Function2DOverviewPanel(result);
		Core.getGUI().setContent(plot); 
		setContent( plot, PLOT ); 
	}

	@Override
	public void plotFFT() {
		
		Function result = getResultForView();
		if (result == null) return;
		Function transform = Transforms.fft2D(result,	false, Transforms.NORMALIZE_ROOT_N );
		Function2DOverviewPanel plot = new Function2DOverviewPanel(transform);
		Core.getGUI().setContent(plot); 
		setContent( plot, FFT );
	}

	@Override
	public void edit() {
		
		CreateOperationSystem2DPanel systemPanel = new CreateOperationSystem2DPanel( system ); 
		Core.getGUI().setContent(systemPanel); 
		setContent( systemPanel, EDIT );
		
	}

}
