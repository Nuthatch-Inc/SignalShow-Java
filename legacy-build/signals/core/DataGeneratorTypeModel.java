package signals.core;

import javax.swing.SpinnerNumberModel;

public class DataGeneratorTypeModel {
	
	//name to appear in lists
	protected String name;
	
	//classes to which each of the parameters belongs
	@SuppressWarnings("unchecked")
	protected Class[] paramClasses; 
	
	//classes to which each of the sources belongs
	@SuppressWarnings("unchecked")
	protected Class[] sourceClasses; 
	
	//names of each of the parameters
	protected String[] paramNames; 

	//names of each of the sources
	protected String[] sourceNames;
	
	//default values for the parameters
	protected Object[] paramDefaults;
	
	//default values for the sources
	protected Object[] sourceDefaults;
	
	//default spinner models
	protected SpinnerNumberModel[] spinnerModels;
	
	//path for the doc file
	protected String docPath; 

	//code representing how the interface used to create this signal generator should be styled
	protected Constants.StyleCode styleCode; 
	
	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}
	
	/**
	 * @return the spinnerModels
	 */
	public SpinnerNumberModel[] getSpinnerModels() {
		return spinnerModels;
	}

	/**
	 * @param spinnerModels the spinnerModels to set
	 */
	public void setSpinnerModels(SpinnerNumberModel[] spinnerModels) {
		this.spinnerModels = spinnerModels;
	}
	
	/**
	 * 
	 * @return an array storing the class of each parameter
	 */
	@SuppressWarnings("unchecked")
	public Class[] getParamClasses() {
		return paramClasses; 
	}

	/**
	 * @return an array storing the name of each parameter
	 */
	public String[] getParamNames() {
		return paramNames;
	}

	/**
	 * @return an array storing the class of each source
	 */
	@SuppressWarnings("unchecked")
	public Class[] getSourceClasses() {
		return sourceClasses;
	}

	/**
	 * @return an array storing the name of each source
	 */
	public String[] getSourceNames() {
		return sourceNames;
	}
	
	/**
	 * @return a code indicating the style of interface in which this function should be created
	 */
	public Constants.StyleCode getStyleCode() {
		return styleCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param paramClasses the paramClasses to set
	 */
	@SuppressWarnings("unchecked")
	public void setParamClasses(Class[] paramClasses) {
		this.paramClasses = paramClasses;
	}

	/**
	 * @param paramNames the paramNames to set
	 */
	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	/**
	 * @return the sourceDefaults
	 */
	public Object[] getSourceDefaults() {
		return sourceDefaults;
	}

	/**
	 * @param sourceDefaults the sourceDefaults to set
	 */
	public void setSourceDefaults(Object[] sourceDefaults) {
		this.sourceDefaults = sourceDefaults;
	}

	/**
	 * @param sourceClasses the sourceClasses to set
	 */
	@SuppressWarnings("unchecked")
	public void setSourceClasses(Class[] sourceClasses) {
		this.sourceClasses = sourceClasses;
	}

	/**
	 * @param source_names the sourceNames to set
	 */
	public void setSourceNames(String[] source_names) {
		this.sourceNames = source_names;
	}

	/**
	 * @param styleCode the styleCode to set
	 */
	public void setStyleCode(Constants.StyleCode styleCode) {
		this.styleCode = styleCode;
	}

	/**
	 * @return the paramDefaults
	 */
	public Object[] getParamDefaults() {
		return paramDefaults;
	}

	/**
	 * @param paramDefaults the paramDefaults to set
	 */
	public void setParamDefaults(Object[] paramDefaults) {
		this.paramDefaults = paramDefaults;
	}

}
