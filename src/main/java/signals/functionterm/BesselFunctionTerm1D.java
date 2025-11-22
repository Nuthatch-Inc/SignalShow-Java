/**
 * 
 */
package signals.functionterm;

import java.awt.image.renderable.ParameterBlock;

import javax.swing.SpinnerNumberModel;

import signals.core.DataGeneratorTypeModel;

/**
 * @author Juliet
 * Represents a 1D Bessel Function
 */
public class BesselFunctionTerm1D extends AnalyticFunctionTerm1D { 

	public BesselFunctionTerm1D() {
		super();
		// TODO Auto-generated constructor stub
	}

	static int order_idx = 3; 
	
	/**
	 * @param paramBlock
	 */
	public BesselFunctionTerm1D(ParameterBlock paramBlock) {

		super(paramBlock);

	}
	
	/**
	 * @return the amplitude
	 */
	public int getOrder() {
		
		return paramBlock.getIntParameter(order_idx);
	}
	
	/**
	 * @param order the order to set
	 */
	public void setOrder(double order) {
		
		paramBlock.set( order, order_idx );

	}

	/* (non-Javadoc)
	 * @see signals.core.FunctionTerm1D#create(double[])
	 */
	@Override
	public double[] create(double[] indices) {

		double width = getWidth(); 
		double center = getCenter();
		double amplitude = getAmplitude();
		int order = getOrder();

		int dimension = indices.length; 
		double[] bessel = new double[ dimension ];	

		for ( int i = 0; i < dimension; i++ )  { //i in array coordinates

			double c = Math.PI * Math.abs( ( indices[i] - center ) / width ); 
			double besselF = 0;

			if( order == 0 ) {

				besselF = Bessel.J0( c ); 

			} else if ( order == 1 ) {

				besselF = Bessel.J1( c );

			} else { //recursive case

				//special case, don't bother to calculate
				if( c == 0 ) {

					besselF = 0; 

				} else {

					double besselF1 = Bessel.J1( c ); 
					double besselF0 = Bessel.J0( c ); 

					for( int j = 2; j <= order; j++ ) {

						besselF = 2 * ( j - 1) / c * besselF1 - besselF0; 
						besselF0 = besselF1; 
						besselF1 = besselF; 

					} //end for

				} //end else

			} //end recursive case

			//
			// Fix round-off errors
			//

			if( besselF > 1) {

				besselF = 0; 
			}

			if( order > 5 ) {

				if( ( ( 3 * c / order < 1 ) && ( Math.abs( besselF ) > .0001 ) ) ||
						( c < 8.6 ) && ( besselF < 0 ) ) {

					besselF = 0; 
				} 
			}

			//multiply by amplitude and store in array
			bessel[i] = amplitude * besselF;

		}	

		return bessel;

	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		
		//parameter classes with order
		Class[] param_classes = { Double.class, Double.class, Double.class, Integer.class };
		model.setParamClasses( param_classes );
		
		String[] param_names = { "A"+'\u2080', "x"+'\u2080', "b", "n" };  
		model.setParamNames( param_names );
		
		Object[] param_defaults = { new Double(1), new Double( 0 ), new Double( 32 ), new Integer( 0 ) }; 
		model.setParamDefaults( param_defaults );
		
//		model.setLargeIcon("/functionIcons/Bessel1DLarge.png");
//		model.setSmallIcon("/functionIcons/Bessel1DSmall.png");
		model.setName("Bessel");
		
		SpinnerNumberModel amplitudeSpinnerModel = new SpinnerNumberModel( 1.0, -100000.0, 100000.0, 0.1);
		SpinnerNumberModel centerSpinnerModel = new SpinnerNumberModel( 0.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel( 32.0, -100000.0, 100000.0, 1.0);
		SpinnerNumberModel orderSpinnerModel = new SpinnerNumberModel( new Integer(0), 
				new Integer(0), new Integer(100000), new Integer(1) );
		
		SpinnerNumberModel[] spinnerModels = { amplitudeSpinnerModel, centerSpinnerModel, widthSpinnerModel, orderSpinnerModel };
		model.setSpinnerModels(spinnerModels);
		
		model.setDocPath("/functiondoc/bessel.html");
	}
	
	@Override
	public String getEquation(String[] variables) {
		return amplitudeMultiplierString() + "J" + getOrder() + "["+formattedParamString(variables[0])+"]"; 
	}
}
