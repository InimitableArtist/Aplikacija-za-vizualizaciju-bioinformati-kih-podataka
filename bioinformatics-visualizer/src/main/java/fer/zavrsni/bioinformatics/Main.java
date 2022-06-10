package fer.zavrsni.bioinformatics;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.structure.gui.JMatrixPanel;

import htsjdk.samtools.*;
import java.awt.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main 
{
    public static void main( String[] args )
    {
    	
    	try {
    		SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					MainScreen screen = new MainScreen();
					
				}
    			
    		});
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
