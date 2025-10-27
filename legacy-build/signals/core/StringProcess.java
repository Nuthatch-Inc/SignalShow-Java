package signals.core;

public class StringProcess {
	
public static String classNameConvert( String input ) {
		
		int begin = input.lastIndexOf('.'); 
		int end = input.lastIndexOf( "Function" );
		if( end == -1 ) end = input.lastIndexOf( "Op" );
		input = input.substring( begin+1, end ); 
	
        if (input == null || input.length() ==0)
           return "";
        StringBuilder newText = new StringBuilder(input.length() + 3);
        newText.append(input.charAt(0));
        for (int i = 1; i < input.length(); i++)
        {
            if ( Character.isUpperCase(input.charAt(i)) )
                newText.append(' ');
            newText.append(input.charAt(i));
        }
        return newText.toString();
	}

}
