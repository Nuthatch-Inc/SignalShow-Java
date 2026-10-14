package signals.io;

import javax.swing.ImageIcon;

/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {
	
    /*
     * Get the extension of a file.
     */
    public static String getExtension(String s) {
        String ext = null;
        int i = extensionIndex(s);

        if (i >= 0) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    /**
     * returns the name of the file without the extension
     */
    public static String getNameNoExtension(String s) {
  
        int i = extensionIndex(s);

        if (i >= 0) {
            s = s.substring(0, i);
        }
        return s;
    }

    // Save dialogs pass complete paths; only the final filename has an extension.
    private static int extensionIndex(String path) {
        int filenameStart = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\')) + 1;
        int dot = path.lastIndexOf('.');
        return dot > filenameStart && dot < path.length() - 1 ? dot : -1;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}

