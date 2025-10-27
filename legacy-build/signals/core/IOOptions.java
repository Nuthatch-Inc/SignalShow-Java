package signals.core;

import java.io.File;
import java.util.ArrayList;

import signals.core.Constants.Part;

public class IOOptions {
	
	File currentDirectory; 
	ArrayList<Part> outputParts; 
	
	public IOOptions() {
		
		outputParts = new ArrayList<Part>(); 
		outputParts.add(Part.REAL_PART); 
	}

	public File getCurrentDirectory() {
		return currentDirectory;
	}

	public ArrayList<Part> getOutputParts() {
		return outputParts;
	}

	public void setOutputParts(ArrayList<Part> outputParts) {
		this.outputParts = outputParts;
	}

	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

}
