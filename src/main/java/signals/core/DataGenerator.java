/**
 * 
 */
package signals.core;

import java.awt.image.renderable.ParameterBlock;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.SpinnerNumberModel;


/**
 * Abstract base class for function terms and operators, ways
 * of creating functions that may be selected from a list
 * @author Juliet
 *
 */
public abstract class DataGenerator {

	//a parameter block stores the parameters and sources used to create the data
	protected ParameterBlock paramBlock; 
	
	//each subclass of DataGenerator has a typeModel, which is shared for all instances of that subclass
	protected DataGeneratorTypeModel typeModel; 
	
	//the data generator manager stores the typeModels for each subclass
	private static DataGeneratorManager manager=new DataGeneratorManager();

	public DataGenerator( DataGenerator datagenerator, ParameterBlock paramBlock ) {
		if( paramBlock != null ) this.paramBlock = (ParameterBlock)paramBlock.clone();
		typeModel = manager.register(datagenerator);
	}
	
	
	/**
	 * Constructor that accepts a ParameterBlock
	 * @param paramBlock stores the settings and parameters. This parameter is cloned.
	 */
	public DataGenerator( ParameterBlock paramBlock ) {
		if( paramBlock != null ) this.paramBlock = (ParameterBlock)paramBlock.clone();
		typeModel = manager.register(this);
	}
	
	public DataGenerator() {
		
		this( null ); 
	}
	
	public DataGenerator getDefaultInstance() {

		return getInstance( getDefaultParamBlock() );
	}
	
	public ParameterBlock getDefaultParamBlock() { 
		
		//create the parameter block
		ParameterBlock pb = new ParameterBlock(); 
		
		//get the default parameters
		Object[] parameters = typeModel.getParamDefaults();
		if( parameters != null ) {
			for( int i=0; i<parameters.length; ++i ) {
				pb.add( parameters[i] );
			}
		}
		
		Object[] sources = typeModel.getSourceDefaults();
		if( sources != null ) {
			for( int i=0; i<sources.length; ++i ) {
				
				pb.addSource( sources[i] );
			}
		}
		
		return pb;
	}
 

	/**
	 * Abstract Factory method that allows instances to create other instances
	 * @param paramBlock parameter block to use
	 * @return an instance of the specific class
	 */
	@SuppressWarnings("unchecked")
	public DataGenerator getInstance( ParameterBlock paramBlock ) {
		
		java.lang.reflect.Constructor co;
		try {
			co = getClass().getConstructor(ParameterBlock.class);
			return (DataGenerator) co.newInstance(paramBlock);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	/**
	 * @return a clone of the parameter block
	 */
	public ParameterBlock getParamBlock() {
		return paramBlock;
	}
	
	/**
	 * @return a clone of the parameter block
	 */
	public ParameterBlock getParamBlockClone() {
		return (ParameterBlock)paramBlock.clone();
	}
	
	public Object getParameter( int index ) {
		
		return paramBlock.getObjectParameter(index);
	}
	
	public void setParameter( Object value, int index ) {
		
		paramBlock.set( value, index );
	}
	
	/**
	 * 
	 * @return the parameters of this data generator
	 */
	public Vector<Object> getParameters() {
		
		return paramBlock.getParameters(); 
	}

	/**
	 * @param paramBlock the paramBlock to set. This parameter is cloned.
	 */
	public void setParamBlock(ParameterBlock paramBlock) {
		this.paramBlock = (ParameterBlock)paramBlock.clone();
	}
	
	/**
	 * Subclasses should override this method. It gets called when the type model is initialized
	 *
	 */
	public void initTypeModel( DataGeneratorTypeModel model ) {}

	public String getEquation( String[] variables ) {
		
		return ""; //TODO: make abstract
	} 
	
	
	/**
	 * @return the typeModel
	 */
	public DataGeneratorTypeModel getTypeModel() {
		return typeModel;
	}
	
	public SpinnerNumberModel[] getSpinnerModels() {
		
		SpinnerNumberModel[] defaultModels = getTypeModel().getSpinnerModels();
		Vector<Object> parameters = getParameters();
		int numParams = defaultModels.length; 
		
		SpinnerNumberModel[] models = new SpinnerNumberModel[numParams];
		
		for( int i = 0; i < numParams; ++i ) {
			
			SpinnerNumberModel model = defaultModels[i];
			
			models[i] = new SpinnerNumberModel( (Number)parameters.get(i), model.getMinimum(),
					model.getMaximum(), model.getStepSize() );
		}
		
		return models;
		
	}

}
