package signals.gui.plot;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import signals.core.Function;
import signals.demo.AnimatedPane;
import signals.gui.ResizablePane;
import signals.gui.SpringUtilities;

@SuppressWarnings("serial")
public class ArgandDiagram extends AnimatedPane implements ResizablePane {

	protected ArgandCursorPanel argandPanel; 
	protected TimeTraceCursorPanel realPanel, imagPanel; 
	protected Function function; 
	JPanel mainPanel; 
	double[] real, imag; 

	public ArgandDiagram() {

		int longDimension = 300; 
		int shortDimension = 200; 

		Dimension argandDimension = new Dimension( longDimension, longDimension ); 
		Dimension realDimension = new Dimension( longDimension, shortDimension ); 
		Dimension imagDimension = new Dimension( shortDimension, longDimension );

		argandPanel = new ArgandCursorPanel( argandDimension ); 
		realPanel = new TimeTraceCursorPanel( realDimension, true ); 
		imagPanel = new TimeTraceCursorPanel( imagDimension, false ); 
		JPanel strut = new JPanel(); 

		JPanel demoPanel = new JPanel(new SpringLayout()); 
		demoPanel.add(argandPanel); 
		demoPanel.add( imagPanel ); 
		demoPanel.add( realPanel );
		demoPanel.add( strut ); 

		SpringUtilities.makeCompactGrid(demoPanel,
				2, 2, 		//rows, cols
				5, 5,        //initX, initY
				5, 5);       //xPad, yPad

		setLayout( new BorderLayout() ); 

		mainPanel = new JPanel(); 
		mainPanel.add( demoPanel); 

		add( mainPanel, BorderLayout.CENTER ); 
		add( createAnimationToolBar(), BorderLayout.SOUTH ); 


	}

	public void addToMain( JComponent component ) {

		mainPanel.add( component ); 
	}

	@Override
	public void toBegin() {
		 
		argandPanel.setFunction(real, imag); 
		realPanel.setFunction(real); 
		imagPanel.setFunction(imag); 
	}

	public JComponent createAnimationToolBar() {

		JPanel animationToolBar = new JPanel(); 

		animationToolBar.add( new JButton( toBeginAction ) );
		animationToolBar.add( new JButton(slowerAction) );
		animationToolBar.add( new JButton(stepAction) );
		animationToolBar.add( new JButton(stopAction) );
		animationToolBar.add( new JButton(playAction) );
		animationToolBar.add( new JButton(fasterAction) );

		return animationToolBar; 

	} 

	public void stepForward() {

		super.stepForward(); 
	}

	@Override
	public void step() {

		//		NumberFormat formatter = Core.getDisplayOptions().getFormat();
		//		if( labels.size() > 0 )
		//			labels.get(0).setText(title + ": (" + 
		//					formatter.format(real[cursorIndex]) + ", " + 
		//					formatter.format(imag[cursorIndex]) + " )");

		argandPanel.moveRight(); 
		imagPanel.moveRight(); 
		realPanel.moveRight(); 
	}

	@Override
	public void stepBack() {

		argandPanel.moveLeft(); 
		imagPanel.moveLeft(); 
		realPanel.moveLeft(); 
	}

	public void setFunction( Function function ) {

		this.function = function; 

		double[] real = function.getReal(); 
		double[] imag = function.getImaginary(); 
		setData( real, imag ); 

	}

	public void setData( double[] real, double[] imag ) {

		this.real = real; 
		this.imag = imag; 

		double[] dataExtrema = PlottingMath.extrema( imag, PlottingMath.extrema(real) ); 

		argandPanel.getGraphic().setAutofitOn(false); 
		argandPanel.getGraphic().setWindow(dataExtrema, dataExtrema); 
		argandPanel.setFunction(real, imag); 

		double[] imag_x_extrema = { 0, TimeTraceCursorPanel.MAX_NUM_PTS }; 

		imagPanel.getGraphic().setAutofitOn(false);
		imagPanel.getGraphic().setWindow(imag_x_extrema, dataExtrema); 
		imagPanel.setFunction(imag); 

		double[] imag_y_extrema = { -(TimeTraceCursorPanel.MAX_NUM_PTS), 0 };

		realPanel.getGraphic().setAutofitOn(false);
		realPanel.getGraphic().setWindow(dataExtrema, imag_y_extrema); 
		realPanel.setFunction(real); 
	}

	public void sizeChanged() {
		
		argandPanel.sizeChanged(); 
		realPanel.sizeChanged();
		imagPanel.sizeChanged(); 
	}

}
