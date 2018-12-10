import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormatSymbols;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import radQC.RadQCFrame;

/**
 * Start application class, containing the main method! <br>
 * 
 * @author Dan Fulea, 21 Apr. 2015
 */
public class Start {
	private static String filename = "JavaLookAndFeelLoader.laf";

	/**
	 * defaultLookAndFeel void method. <br>
	 * Try setting a default system look and feel!
	 */
	private static void defaultLookAndFeel() {
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (final Exception exc) {

		// Trying to let only crossPlatformLookAndFeel due to incompatibility of
		// TEXT FONTS of JTextArea
		// between WINDOWS vista or 7 and WINDOWS XP when migrating from old
		// java (jre) 1.4 to new java 6!!
		JFrame.setDefaultLookAndFeelDecorated(true);// the key to set Java Look
													// and Feel from the start!
		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (final Exception exc1) {
			System.err.println("Error defaultLookAndFell " + exc1);
		}
		// }
	}
	
	/**
	 * Converts ASCII int value to a String.
	 * 
	 * @param i
	 *            the ASCII integer
	 * @return the string representation
	 */
	private static String asciiToStr(int i) {
		char a[] = new char[1];
		a[0] = (char) i;
		return (new String(a)); // char to string
	}
	
	/**
	 * loadLookAndFeel void method. <br>
	 * Try loading the look and feel!
	 */
	private static void loadLookAndFeel() {
		String fileSeparator = System.getProperty("file.separator");
		String curentDir = System.getProperty("user.dir");
		String filename1 = curentDir + fileSeparator + filename;

		File f = new File(filename1);
		int i = 0;
		String desiredLF = "";
		boolean foundB = false;
		if (f.exists()) {
			try {
				FileReader fr = new FileReader(f);
				while ((i = fr.read()) != -1) {
					String s1 = new String();
					s1 = asciiToStr(i);
					desiredLF = desiredLF + s1;
				}
				fr.close();

				JFrame.setDefaultLookAndFeelDecorated(true);

				for (LookAndFeelInfo info : UIManager
						.getInstalledLookAndFeels()) {
					if (desiredLF.equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						foundB = true;
						break;
					}
				}

				if (!foundB) {
					if (desiredLF.equals("System")) {
						foundB = true;
						UIManager.setLookAndFeel(UIManager
								.getSystemLookAndFeelClassName());
					} else if (desiredLF.equals("Java")) {
						foundB = true;
						UIManager.setLookAndFeel(UIManager
								.getCrossPlatformLookAndFeelClassName());
					}
				}

				if (!foundB)
					UIManager.setLookAndFeel(desiredLF);

				// empty
				if (desiredLF.equals("")) {
					defaultLookAndFeel();
				}

			} catch (Exception e) {
				defaultLookAndFeel();
			}
		} else {
			defaultLookAndFeel();
		}
	}

	/**
	 * Extract any numbers from a string 
	 * @param src the source string
	 * @return the result
	 */
	public static String extractDigits(String src) {
		DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
		char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();
		
		boolean stop=false;
		boolean enter=false;
		int index =0; 
		
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < src.length(); i++) {
	        char c = src.charAt(i);	        
	        if (Character.isDigit(c)) {	        	
	        	stop=true;
	        	enter=true;
	            builder.append(c);
	        } else {
	        	if (enter && c == localeDecimalSeparator){
	        		stop=true;//just continue
	        		builder.append(c);
	        	}
	        	else
	        		stop=false;
	        }
	        
	        if (!stop && enter){
	        	index=i;
	        	break;	        	
	        }	        
	    }
	    //we have index of next character non number which is SPACE
	    String str= src.substring(index+1);
	    //System.out.println(str);
	    String[] result=str.split(" ");
		String unitS =result[0];
		System.out.println("Units="+unitS);
	    return builder.toString();
	}
	
	/**
	 * Main method. <br>
	 * @param args args
	 */
	public static void main(String[] args) {
		
		loadLookAndFeel();
		// Toolkit.getDefaultToolkit().beep();//audio beep!!!
		//new RadQCFrame();
		SwingUtilities.invokeLater(

				new Runnable() {
					public void run() {
						// Turn off metal's use of bold fonts
						UIManager.put("swing.boldMetal", Boolean.FALSE);
						new RadQCFrame();
					}
				}
		);

	}

}
