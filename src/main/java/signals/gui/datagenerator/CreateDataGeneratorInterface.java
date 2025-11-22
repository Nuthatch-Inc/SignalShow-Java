package signals.gui.datagenerator;

import signals.core.DataGenerator;

public interface CreateDataGeneratorInterface {

	public DataGenerator getDataGenerator(); 
	
	public void setListNew( DataGenerator newTerm ); 
	
	public void setListExisting( DataGenerator newTerm ); 
	
	public void setDataGenerator( DataGenerator newTerm ); 
	
	public void setDocPath( String path); 
}
