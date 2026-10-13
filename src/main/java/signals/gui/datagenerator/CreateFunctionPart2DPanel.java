package signals.gui.datagenerator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import signals.core.CombineTermsRule;
import signals.core.DataGenerator;
import signals.core.Deconstructable;
import signals.gui.plot.ImageDisplayPanel;
import signals.core.FunctionPart2D;
import signals.core.FunctionTerm1D;
import signals.core.FunctionTerm2D;
import signals.functionterm.AnalyticFunctionTerm2D;
import signals.functionterm.ConstantFunctionTerm1D;
import signals.functionterm.CylinderFunctionTerm2D;
import signals.functionterm.GaussianNoiseFunctionTerm2D;
import signals.functionterm.ImageFunctionTerm2D;
import signals.functionterm.NoiseFunctionTerm2D;
import signals.functionterm.PolarFunctionTerm2D;
import signals.functionterm.XYFunctionTerm2D;
import signals.functionterm.ZeroFunctionTerm1D;
import signals.functionterm.ZeroFunctionTerm2D;
import signals.gui.IconCache;

@SuppressWarnings("serial")
public class CreateFunctionPart2DPanel extends JPanel implements GUIEventListener, Deconstructable {

	//broadcasts events for this part of the interface	
	GUIEventBroadcaster broadcaster;
	private GUIEventBroadcaster termBroadcaster;
	// Broadcasters keep weak references, so retain the active forwarding listener.
	private GUIEventListener termListener;
	private boolean bindingTerm;

	CreateDataGeneratorInterface termPanel; 
	FunctionCalculator2D calculator; 

	//combobox to select the dimension of this function
	JComboBox widthSelector, heightSelector;

	//used for choosing whether the indices of this function are zero-centered
	JCheckBox zeroCenteredCheckBox; 

	boolean editable; 

	public static final int INITIALIZE = 10; 

	protected JPanel contentPanel;
	protected JPanel termContainerPanel;
	
	boolean hasDocPath; 
	String docPath; 

	int xDimension;
	int yDimension;
	boolean zeroCentered;

	public CreateFunctionPart2DPanel( GUIEventBroadcaster broadcaster, boolean createDefaultNew ) {

		this( broadcaster, createDefaultNew, true ); 
	}

	public CreateFunctionPart2DPanel( GUIEventBroadcaster broadcaster, boolean createDefaultNew, boolean editable )  {

		//create the event broadcaster
		this.broadcaster = broadcaster; 
		this.editable = editable; 
		broadcaster.addGUIEventListener(this); 

		//create three panels for the content panel (use card layout) 
		contentPanel = new JPanel(); 
		contentPanel.setLayout(new BorderLayout() ); 

		//main layout setup: calculator and content panel
		calculator = new FunctionCalculator2D( broadcaster, editable ); 
		setLayout( new BorderLayout() ); 
		add( calculator, BorderLayout.NORTH );
		add( contentPanel, BorderLayout.CENTER ); 

		hasDocPath = false; 
		
		if( createDefaultNew )
			setButtonPanel(); 
	}

	public void setDocPath( String path ) {

		hasDocPath = true; 
		if( termPanel != null ) termPanel.setDocPath(path); 
		docPath = path; 
	}

	public FunctionCalculator2D getCalculator() {
		return calculator;
	}

	public DataGenerator getEditedDataGenerator() {

		return termPanel.getDataGenerator(); 
	}

	public void setButtonPanel() {

		disconnectTermEditor();

		//button panel 
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new GridLayout( 2, 2, 10, 10 ));
		buttonPanel.add( new JButton( new XYTermAction() ));
		buttonPanel.add( new JButton( new PolarTermAction() ));
		buttonPanel.add( new JButton( new NoiseTermAction() ));
		buttonPanel.add( new JButton( new ImageTermAction() ));
		buttonPanel.add( new JButton( new AnalyticTermAction() ));
		buttonPanel.setBorder( BorderFactory.createEmptyBorder(50, 125, 50, 125));
		contentPanel.removeAll(); 
		contentPanel.add( buttonPanel, BorderLayout.CENTER); 
		contentPanel.revalidate(); 
		contentPanel.repaint(); 
	}

	public void setFunctionPart2D( FunctionPart2D part ) {

		ArrayList<FunctionTerm2D> termsList = part.getTermsList(); 
		CombineTermsRule rule = part.getCombineTermsRule(); 
		calculator.setFunctionTerm2DList(termsList, rule);
	}

	public void setIndices( int xDimension, int yDimension, boolean zeroCentered ) {

		this.xDimension = xDimension; 
		this.yDimension = yDimension; 
		this.zeroCentered = zeroCentered;

		broadcastIndices();
	}

	public void broadcastIndices() { 

		ArrayList<Object> values = new ArrayList<Object>(); 

		values.add( xDimension ); 
		values.add( yDimension ); 
		values.add( zeroCentered ); 

		if (termBroadcaster != null) {
			termBroadcaster.broadcast(this, GUIEvent.Descriptor.INDICES_MODIFIED_2D, values);
		}
		broadcaster.broadcast(this, GUIEvent.Descriptor.INDICES_MODIFIED_2D, values); 

	}

	public void setFunctionTerm2D( FunctionTerm2D term2D ) { 

		calculator.insertNewFunction( term2D );
	}

	public void setXYTerm() {

		//create a new function for the X part 
		FunctionTerm1D term1D = new ZeroFunctionTerm1D(new ParameterBlock()); 

		//set the Y part constant
		FunctionTerm1D constantTerm = new ConstantFunctionTerm1D( null ); 

		ParameterBlock pb2 = new ParameterBlock(); 
		pb2.addSource( term1D ); 
		pb2.addSource( (FunctionTerm1D)constantTerm.getDefaultInstance() );

		//create a function term 2D
		XYFunctionTerm2D term2D = new XYFunctionTerm2D( pb2 );

		setFunctionTerm2D( term2D );
	}

	public void setPolarTerm() {

		//create a new function
		FunctionTerm1D term1D = new ZeroFunctionTerm1D(new ParameterBlock()); 

		//set the Y part constant
		FunctionTerm1D constantTerm = new ConstantFunctionTerm1D( null ); 

		ParameterBlock pb2 = new ParameterBlock(); 
		pb2.addSource( term1D ); 
		pb2.addSource( (FunctionTerm1D)constantTerm.getDefaultInstance() );

		//create a function term 2D
		PolarFunctionTerm2D term2D = new PolarFunctionTerm2D( pb2 );
		setFunctionTerm2D( term2D );
	}

	public void setImageTerm() {

		ZeroFunctionTerm2D term = new ZeroFunctionTerm2D( null ); 

		//create a function term 2D
		ZeroFunctionTerm2D term2D = (ZeroFunctionTerm2D) term.getDefaultInstance();
		setFunctionTerm2D( term2D );
	}

	public void setAnalyticTerm() {

		CylinderFunctionTerm2D term = new CylinderFunctionTerm2D( null ); 

		//create a function term 2D
		CylinderFunctionTerm2D term2D = (CylinderFunctionTerm2D) term.getDefaultInstance();
		setFunctionTerm2D( term2D );
	}

	public void setNoiseTerm() {

		//create a new function - Gaussian noise for now 
		GaussianNoiseFunctionTerm2D term = new GaussianNoiseFunctionTerm2D( null ); 

		//create a function term 2D
		GaussianNoiseFunctionTerm2D term2D = (GaussianNoiseFunctionTerm2D) term.getDefaultInstance();
		setFunctionTerm2D( term2D );
	}

	/**
	 * 
	 * @return the functionPart1D that the user has created
	 */
	public FunctionPart2D getFunctionPart2D() {

		if( calculator.getFunctionTerm2DList().isEmpty() ) {

			ArrayList<FunctionTerm2D> termsList = new ArrayList<FunctionTerm2D>();

			ZeroFunctionTerm2D term = new ZeroFunctionTerm2D( null ); 
			ZeroFunctionTerm2D term2D = (ZeroFunctionTerm2D) term.getDefaultInstance();
			termsList.add( term2D );

			int[] rule = {1};
			CombineTermsRule combineRule = new CombineTermsRule( rule );
			FunctionPart2D part = new FunctionPart2D( termsList, combineRule );
			return part;
		}


		ArrayList<FunctionTerm2D> termsList = calculator.getFunctionTerm2DList();
		int[] rule = calculator.getCodeList();
		CombineTermsRule combineRule = new CombineTermsRule( rule );
		FunctionPart2D part = new FunctionPart2D( termsList, combineRule );

		return part;
	}

	@Override
	public void deconstruct() {
		disconnectTermEditor();
		calculator.deconstruct();
	}

	private void disconnectTermEditor() {
		termBroadcaster = null;
		termListener = null;
		if (termPanel instanceof Component) releaseImagePreviews((Component) termPanel);
	}

	// The term editors have different nested layouts (including tabs). Release only
	// view-owned image buffers; the definition may still belong to a live document.
	private static void releaseImagePreviews(Component component) {
		if (component instanceof ImageDisplayPanel) {
			((ImageDisplayPanel) component).deconstruct();
		} else if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents())
				releaseImagePreviews(child);
		}
	}

	public void setupCorrectTermPanel( DataGenerator function ) {

		// Removed editors must not publish into another term or a selected operator.
		disconnectTermEditor();
		GUIEventBroadcaster events = new GUIEventBroadcaster();
		if( function instanceof XYFunctionTerm2D ) {
			termPanel = new CreateFunctionTerm2DXYPanel(events, editable);
		} else if ( function instanceof PolarFunctionTerm2D ) {
			termPanel = new CreateFunctionTerm2DPolarPanel(events, editable);
		} else if ( function instanceof NoiseFunctionTerm2D ) {
			termPanel = new CreateFunctionTerm2DNoisePanel(events, editable);
		} else if ( function instanceof ImageFunctionTerm2D ) {
			termPanel = new CreateFunctionTerm2DImagePanel(events, editable);
		} else if( function instanceof AnalyticFunctionTerm2D ) {
			termPanel = new CreateFunctionTerm2DAnalyticPanel(events, editable);
		} else {
			throw new IllegalArgumentException("Unsupported 2D term: " + function.getClass());
		}

		termBroadcaster = events;
		termListener = event -> {
			if (termBroadcaster == events && !bindingTerm
					&& event.getDescriptor() == GUIEvent.Descriptor.MODIFIED) {
				broadcaster.broadcast(this, GUIEvent.Descriptor.MODIFIED, event.getValue(0));
			}
		};
		events.addGUIEventListener(termListener);

		ArrayList<Object> indices = new ArrayList<Object>();
		indices.add(xDimension);
		indices.add(yDimension);
		indices.add(zeroCentered);
		events.broadcast(this, GUIEvent.Descriptor.INDICES_MODIFIED_2D, indices);
		if( hasDocPath ) termPanel.setDocPath(docPath);

		contentPanel.removeAll();
		contentPanel.add( (Component) termPanel, BorderLayout.CENTER);
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void bindTerm(DataGenerator function, boolean newTerm) {
		bindingTerm = true;
		try {
			setupCorrectTermPanel(function);
			if (newTerm) termPanel.setListNew(function);
			else termPanel.setListExisting(function);
		} finally {
			bindingTerm = false;
		}
		// Publish only after both separable subterms and their defaults are ready.
		calculator.setFunctionTerm2D((FunctionTerm2D) termPanel.getDataGenerator());
	}

	public void setListNew( DataGenerator function ) {
		bindTerm(function, true);
	}

	public void setListExisting( DataGenerator function ) {
		bindTerm(function, false);
	}

	public class XYTermAction extends AbstractAction {

		public XYTermAction() {

			super( "", IconCache.getIcon("/guiIcons/createf2DXY.png") );
		}

		public void actionPerformed(ActionEvent e) {
			setXYTerm();
		}
	}

	public class PolarTermAction extends AbstractAction {

		public PolarTermAction() {

			super( "", IconCache.getIcon("/guiIcons/createf2DPolar.png") );
		}

		public void actionPerformed(ActionEvent e) {
			setPolarTerm();
		}
	}

	public class ImageTermAction extends AbstractAction {

		public ImageTermAction() {

			super( "", IconCache.getIcon("/guiIcons/createf2DImage.png") );
		}

		public void actionPerformed(ActionEvent e) {
			setImageTerm();
		}
	}

	public class AnalyticTermAction extends AbstractAction {

		public AnalyticTermAction() {

			super( "", IconCache.getIcon("/guiIcons/createf2DAnalytic.png") );
		}

		public void actionPerformed(ActionEvent e) {
			setAnalyticTerm();
		}
	}

	public class NoiseTermAction extends AbstractAction {

		public NoiseTermAction() {

			super( "", IconCache.getIcon("/guiIcons/createf2DNoise.png") );
		}

		public void actionPerformed(ActionEvent e) {
			setNoiseTerm();
		}
	}

	public void GUIEventOccurred(GUIEvent e) {

		if( e.getSource().equals(this) ) return; 

		switch( e.getDescriptor() ) {

		case NEW_SELECTED: //comes from calculator

			setListNew( (DataGenerator)(e.getValue(0)));

			break; 

		case EXISTING_SELECTED: //comes from calculator

			setListExisting( (DataGenerator)(e.getValue(0))); 

			break; 

		case NON_SELECTED: //comes from calculator

			disconnectTermEditor();

			if( e.getValue(0) != null && (Integer)(e.getValue(0)) == INITIALIZE ) {

				setButtonPanel(); 

			} else {

				contentPanel.removeAll(); 
				contentPanel.revalidate(); 
				contentPanel.repaint(); 
			}

			break; 

		}

	}
}
