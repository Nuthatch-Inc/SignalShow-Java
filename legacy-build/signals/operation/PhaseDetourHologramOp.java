package signals.operation;

import java.awt.image.renderable.ParameterBlock;

import signals.core.CombineOpsRule;
import signals.core.DataGeneratorTypeModel;
import signals.core.Function;
import signals.core.Function2D;
import signals.core.FunctionFactory;
import signals.core.FunctionTerm2D;
import signals.core.UnaryOperation;
import signals.functionterm.RandomPhaseFunctionTerm2D;
import signals.functionterm.ZeroFunctionTerm2D;
import signals.gui.operation.CGHOptionsPanel;
import signals.gui.operation.OperationOptionsPanel;

public class PhaseDetourHologramOp extends UnaryOperation {

	public PhaseDetourHologramOp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PhaseDetourHologramOp(ParameterBlock paramBlock, int priority) {
		super(paramBlock, priority);
		// TODO Auto-generated constructor stub
	}

	boolean randomPhase; 
	boolean errorDiffusion; 
	boolean widerApertures; 

	public boolean isWiderApertures() {
		return widerApertures;
	}

	public void setWiderApertures(boolean widerApertures) {
		this.widerApertures = widerApertures;
	}

	public PhaseDetourHologramOp(ParameterBlock paramBlock) {
		super(paramBlock, CombineOpsRule.UNARY_OP_TIER_2);
	}

	public boolean isRandomPhase() {
		return randomPhase;
	}

	public OperationOptionsPanel getOptionsInterface() {
		
		return new CGHOptionsPanel(this); 
	}

	public void setRandomPhase(boolean randomPhase) {
		this.randomPhase = randomPhase;
	}


	public boolean isErrorDiffusion() {
		return errorDiffusion;
	}


	public void setErrorDiffusion(boolean errorDiffusion) {
		this.errorDiffusion = errorDiffusion;
	}

	@Override
	public Function create(Function input) { //Input is a 2D function

		double[] magnitude = input.getReal(); 

		FunctionTerm2D randomPhaseTerm = (FunctionTerm2D) (randomPhase ?  new RandomPhaseFunctionTerm2D(null).getDefaultInstance() :
			new ZeroFunctionTerm2D(null).getDefaultInstance()); 

		Function2D f2D = (Function2D)input;
		int xDimension = f2D.getDimensionX(); 
		int yDimension = f2D.getDimensionY();
		double[] randomPhase = randomPhaseTerm.create(xDimension, yDimension, input.isZeroCentered());

		Function inputFunction = FunctionFactory.createMagPhaseFunction2D(
				magnitude, randomPhase, true, "", xDimension, yDimension);

		Function transform = Transforms.fft2D(inputFunction, true, Transforms.NORMALIZE_NONE); 

		double[] real_fft = transform.getReal(); 
		double[] imag_fft = transform.getImaginary();
		double[] mag_fft = transform.getMagnitude(); 
		double[] phase_fft = transform.getPhase(); 

		double max_mag = ArrayMath.max(mag_fft);  

		if( errorDiffusion ) {

			double residual_real = 0; 
			double residual_imag = 0; 

			for( int i = 0; i <  real_fft.length; i++ ) {

				double residual_added_real = real_fft[i] + residual_real; 
				double residual_added_imag = imag_fft[i] + residual_imag; 

				//if phase is on boundary, do not add the residual
				//				double residual_added_phase = ArrayMath.phase(residual_added_real, residual_added_imag);
				//				double phase_to_quantize = ( residual_added_phase < -Math.PI || residual_added_phase > Math.PI ) ? 
				//					phase_fft[i] : residual_added_phase;
				double phase_to_quantize = ArrayMath.phase(residual_added_real, residual_added_imag);

				//double phase_to_quantize = residual_added_phase; 
				double quantized_phase = Math.round( phase_to_quantize / (2.0*Math.PI) * 7.0 ); 
				double residual_phase = phase_to_quantize - (quantized_phase * (2.0*Math.PI) / 7.0 );
				quantized_phase += 4; 

				//if this magnitude is the maximum magnitude, do not add the residual. 
				double magnitude_to_quantize = (mag_fft[i] == max_mag) ? 
						mag_fft[i] : ArrayMath.magnitude(residual_added_real, residual_added_imag); 
						double quantized_magnitude = Math.round( magnitude_to_quantize / max_mag * 8.0 ); 
						double residual_magnitude = magnitude_to_quantize - ( quantized_magnitude * max_mag / 8.0 ) ; 

						residual_real = Math.cos( residual_phase ) * residual_magnitude; 
						residual_imag = Math.sin( residual_phase ) * residual_magnitude; 

						mag_fft[i] = quantized_magnitude; 
						phase_fft[i] = quantized_phase;

			} 

		} else { 

			for( int i = 0; i <  mag_fft.length; i++ ) {

				mag_fft[i] = Math.round( mag_fft[i] / max_mag * 8.0 ); 
				phase_fft[i] = Math.round( phase_fft[i] / (2.0*Math.PI) * 7.0 ) + 4; 

			}

		}

		int large_x = xDimension*8; 
		int large_y = yDimension*8; 

		double[][] mag_expanded = ArrayUtilities.expand(mag_fft, xDimension, yDimension); 
		double[][] phase_expanded = ArrayUtilities.expand(phase_fft, xDimension, yDimension); 
		double[][] hologram = new double[large_y][large_x]; 

		for( int y = 0; y < yDimension ; y++ ) {

			for( int x = 0; x < xDimension; x++ ) {

				int[][] cell_array = encode((int) mag_expanded[y][x], (int) phase_expanded[y][x] ); 

				for( int yi = 0; yi < 8; yi++ ) {

					for( int xi = 0; xi <8; xi++ ) {

						hologram[y*8 + yi][x*8 + xi] = cell_array[yi][xi];

					}
				}

			}
		}

		double[] flat_hologram = ArrayUtilities.flatten(hologram); 

		return FunctionFactory.createFunction2D(flat_hologram, 
				new double[flat_hologram.length], true, "hologram", large_x, large_y); 
	}

	public int[][] encode( int magnitude, int phase ) {

		int[][] cell_array = new int[8][8];
		int phase_left = (phase+1)%8; 
		int phase_right = (phase+7)%8; 

		//put phase in x direction and magnitude in y direction
		for( int y = (8-magnitude); y < 8; y++ ) {

			cell_array[y][phase] = 1; 
			
			if( widerApertures ) {
				cell_array[y][phase_left] = 1; 
				cell_array[y][phase_right] = 1; 
			}

		}

		return cell_array; 
	}

	/* (non-Javadoc)
	 * @see signals.core.AnalyticFunctionTerm1D#initTypeModel(signals.core.DataGeneratorTypeModel)
	 */
	@Override
	public void initTypeModel(DataGeneratorTypeModel model) {

		super.initTypeModel(model);
//		model.setLargeIcon("/operationIcons/HologramLarge.png");
//		model.setSmallIcon("/operationIcons/HologramSmall.png");
		model.setName("Phase Detour Hologram");
		
		model.setDocPath("/operationdoc/hologram.html");
	}

	@Override
	public String getOpIconPath() {

		return "/operationIcons/HologramOp.png"; 
	}

}
