package fer.zavrsni.bioinformatics;

import java.util.ArrayList;
import java.util.Comparator;

public class ReadComparator implements Comparator<Read>{
	
	public int compare(Read arg0, Read arg1) {
		return arg0.getReadName().compareTo(arg1.getReadName());
	}
	

}
