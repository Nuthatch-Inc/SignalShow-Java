package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function1D;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.UnaryOperation;
import signals.gui.operation.OperationOptionsPanel;
import signals.gui.operation.ParameterOptionsPanel;

public class ComplementFilterOp extends UnaryOperation implements ParametricOperation {

	public ComplementFilterOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ComplementFilterOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	int order; 

	/* (non-Javadoc)
	 * @see signals.core.Operation#getOptionsInterface()
	 */
	@Override
	public OperationOptionsPanel getOptionsInterface() {

		return new ParameterOptionsPanel(this); 
	}

	public ComplementFilterOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
		order = 1; 
	}
	@Override
	public Function create(Function input) {
		
//		SUB Complement (arrayr(), arrayi(), order%)
//	    CALL FFT1D(N%, arrayr(), arrayi(), -1, "F", 2)
//	    REDIM array1r(N% - 1) AS DOUBLE, array1i(N% - 1) AS DOUBLE, array2r(N% - 1) AS DOUBLE, array2i(N% - 1) AS DOUBLE
//	        mmax = 0#
//	    FOR i% = 0 TO N% - 1
//	        array2r(i%) = MAGNITUDE(arrayr(i%), arrayi(i%))
//	        IF array2r(i%) > mmax# THEN mmax# = array2r(i%)
//	        array1i(i%) = -Phase(arrayr(i%), arrayi(i%))
//	        NEXT
//	    FOR i% = 0 TO N% - 1
//	        array1r(i%) = (array2r(i%) - mmax#) / mmax#
//	        array2i(i%) = array1r(i%)
//	        array2r(i%) = 1#
//	        NEXT
//	    k% = 1
//	    FOR j% = 1 TO order%
//	        k% = -k%
//	        FOR i% = 0 TO N% - 1
//	            array2r(i%) = array2r(i%) + k% * array2i(i%)
//	            array2i(i%) = array2i(i%) * array1r(i%)
//	            NEXT i%
//	        NEXT j%
//	    FOR i% = 0 TO N% - 1
//	        arrayr(i%) = xsng!(array2r(i%))
//	        arrayi(i%) = xsng!(array1i(i%))
//	        NEXT
//	    CALL ConvReal(arrayr(), arrayi())
//	    ERASE array1r, array1i, array2r, array2i
//	    CALL FFT1D(N%, arrayr(), arrayi(), 1, "F", 2)
//
//	END SUB

		double[] real = input.getReal(); 
		double[] imag = input.getImaginary(); 
		boolean zeroCentered = input.isZeroCentered(); 

		int xDimension = 0; 
		int yDimension = 0; 
		
		double[][] transformed = null; 
		
		if( input instanceof Function1D ) {
			
			transformed = Transforms.computeFFT1D( real, imag, zeroCentered, false, Transforms.NORMALIZE_ROOT_N );
			
		} else {
			
			Function2D function2D = (Function2D)input; 
			xDimension = function2D.getDimensionX(); 
			yDimension = function2D.getDimensionY(); 
			transformed = Transforms.computeFFT2D(real, imag, zeroCentered, false, xDimension, yDimension, Transforms.NORMALIZE_ROOT_N );
		}

		int dimension = real.length; 
		
		double[] magnitude = ArrayMath.magnitude(transformed[0], transformed[1]); 
		double max = ArrayMath.max(magnitude);
		
		double[] phase = ArrayMath.negate(ArrayMath.phase(transformed[0], transformed[1]));
		
		double[] argument = new double[dimension]; 
		double[] argumentcopy = new double[dimension]; 


		for ( int i = 0; i < dimension; i++ )  { 

			argument[i] = ( magnitude[i] - max ) / max;
			argumentcopy[i] = argument[i];
			magnitude[i] = 1.0;
		}

		int k = 1; 
		for ( int j = 1; j <= order; j++ )  {

			k = -k;
			for ( int i = 0; i < dimension; i++ )  { 
				magnitude[i] = magnitude[i] + k * argumentcopy[i];
				argumentcopy[i] = argumentcopy[i] * argument[i];
			}
		}
		
		real = ArrayMath.real( magnitude, phase ); 
		imag = ArrayMath.imaginary(magnitude, phase); 

		String name = "Complement Filter{ " + input.getCompactDescriptor() + " }"; 
		
		if( input instanceof Function1D ) {
			
			double[][] output = Transforms.computeFFT1D( real,imag, zeroCentered, true, Transforms.NORMALIZE_ROOT_N );
			return FunctionFactory.createFunction1D( output[0], output[1], zeroCentered, name ); 
			
		} else {
			
			double[][] output = Transforms.computeFFT2D( real, imag, zeroCentered, true, xDimension, yDimension, Transforms.NORMALIZE_ROOT_N );
			return FunctionFactory.createFunction2D( output[0], output[1], zeroCentered, name, xDimension, yDimension ); 
		}

	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
		//		model.setLargeIcon("/operationIcons/FFTLarge.png");
		//		model.setSmallIcon("/operationIcons/FFTSmall.png");
		model.setName("Complement Filter");

		model.setDocPath("/operationdoc/complementfilter.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/ComplementFilterOp.png"; 
	}

	public int getNumParams() {
		return 1;
	}

	public String getParamDescriptor() {
		return "Order";
	}

	public String getParamName(int index) {
		return "Order";
	}

	public double getValue(int index) {
		return order;
	}

	public void setValue(int index, double value) {
		order = (int)value; 
	}

}
