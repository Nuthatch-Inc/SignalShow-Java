package signals.core;

import java.util.HashMap;

/**
 * Stores the DataGeneratorTypeModels for each DataGenerator class
 * @author Juliet
 *
 */
public class DataGeneratorManager {

	private HashMap<String, Object> managementMap=new HashMap<String, Object>();

	/**
	 * 
	 * @param generator
	 * @param typeModelClass
	 * @return
	 */
	public DataGeneratorTypeModel register(DataGenerator generator){

		String className = generator.getClass().getName();
		DataGeneratorTypeModel model =(DataGeneratorTypeModel)managementMap.get(className);

		if (model == null) {
			
			model = new DataGeneratorTypeModel(); 
			generator.initTypeModel(model);
			managementMap.put(className,model);
		} 
		
		return model;
	}
}
