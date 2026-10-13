package signals.gui.datagenerator;

import signals.core.Core;
import signals.core.Function;
import signals.core.OperatorSystem;
import signals.gui.plot.Function1DOverviewPanel;
import signals.operation.Transforms;

@SuppressWarnings("serial")
public class OperatorSystemToolBar1D extends OperatorSystemToolBar {

	public OperatorSystemToolBar1D(OperatorSystem system) {
		super(system);
	}

	@Override
	public void plot() {
		Function result = getResultForView();
		if (result == null) return;
		Function1DOverviewPanel plot = new Function1DOverviewPanel(result);
		setContent( plot, PLOT ); 
	}

	@Override
	public void plotFFT() {
		
		Function result = getResultForView();
		if (result == null) return;
		Function transform = Transforms.fft1D(result, false, Transforms.NORMALIZE_ROOT_N );
		Function1DOverviewPanel plot = new Function1DOverviewPanel(transform);
		setContent( plot, FFT );
	}

	@Override
	public void edit() {
		
		CreateOperationSystem1DPanel systemPanel = new CreateOperationSystem1DPanel( system ); 
		Core.getGUI().setContent(systemPanel); 
		setContent( systemPanel, EDIT );
	}

}
