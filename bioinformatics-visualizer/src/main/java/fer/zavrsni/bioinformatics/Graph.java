package fer.zavrsni.bioinformatics;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingWorker;

public class Graph {
	
	private int amp;
	private int len;
	private int[] data;
	
	public Graph(SwingWorker<Void, Void> worker, int len, ArrayList<Read> listOfReads) {
		this.len = len;
		data = new int[len];
		for (Read r : listOfReads) {
			try {
				if (worker.isCancelled()) {
					return;
				}
				int currentPos = r.getPos();
				for (BEDGap gap : r.getGaps()) {
					for (int i = currentPos; i < r.getPos() + gap.getPos(); i++) {
						data[i]++;
					}
					currentPos = r.getPos() + gap.getPos() + gap.getLen();
				}
				for (int i = currentPos; i < r.getPos() + r.getReadLen(); i++) {
					
					data[i]++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		amp = Arrays.stream(data).max().getAsInt();
	}
	
	public int getAmp() {
		return amp;
	}
	public void setAmp(int amp) {
		this.amp = amp;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}
	

}
