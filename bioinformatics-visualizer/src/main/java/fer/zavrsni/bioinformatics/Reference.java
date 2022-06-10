package fer.zavrsni.bioinformatics;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.biojava.nbio.core.sequence.DNASequence;

public class Reference extends JPanel {
	
	private MainScreen screen;
	private Dimension dim;
	private LinkedHashMap<String, DNASequence> refSequences;
	private int DIM = 40;
	private int xPosition = 0;
	private int zoom = 0;
	
	public Reference(MainScreen screen) {
		this.screen = screen;
		dim = new Dimension(screen.getDimensions().width, DIM);
		this.setPreferredSize(dim);
		this.setVisible(true);
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		if (!(refSequences == null)) {
			String currentSeq = refSequences.get(Controller.getCurrentSeq()).toString();
			if (zoom == 0 || zoom == 1) {
				int recW = 1 + 4*zoom;
				for (int i = 0; i < dim.width/recW; i++) {
					g.setColor(ColorPalette.getBaseColor(currentSeq.charAt(i + xPosition)));
					g.fillRect(i*recW, dim.height - 40, recW, 40);
				}
			}
			else if (zoom < 0) {
				int n = (-zoom) * 5;
				for (int i = 0; i < dim.width; i++) {
					g.setColor(ColorPalette.getMultiBaseColor(xPosition, n, i, currentSeq));
					g.fillRect(i, dim.height - 40, 1, 40);
				}
			} else {
				int fontSize = (zoom - 1) * 10;
				g.setFont(new Font("Serif", Font.BOLD, fontSize));
				for (int i = 0; i < dim.width / fontSize; i++) {
					//System.out.println(currentSeq.charAt(i + xPosition));
					
					g.setColor(ColorPalette.getBaseColor(currentSeq.charAt(i + xPosition)));
					g.drawString(currentSeq.substring(i + xPosition, i + xPosition + 1), i * fontSize, fontSize);
				}
				
			}
		}
		
	}
	
	public void onZoomChanged(int zoom) {
		this.zoom = zoom;
		repaint();
	}
	public void onPositionChanged(int xPos) {
		this.xPosition = xPos;
		repaint();
	}
	
	public void onSequenceChanged(String seq) {
		zoom = 0;
		xPosition = 0;
		repaint();
	}
	
	public void reset() {
		Controller.setFastaLoaded(false);
		zoom = 0;
		xPosition = 0;
		refSequences = null;
	}
	
	public MainScreen getScreen() {
		return screen;
	}

	public void setScreen(MainScreen screen) {
		this.screen = screen;
	}

	public Dimension getDim() {
		return dim;
	}

	public void setDim(Dimension dim) {
		this.dim = dim;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public LinkedHashMap<String, DNASequence> getRefSequences() {
		return refSequences;
	}

	public void setRefSequences(LinkedHashMap<String, DNASequence> map) {
		refSequences = map;
	}
	
	public int getSequenceLen(String sequenceName) {
		//return refSequences.get(sequenceName).getLength();
		//String sequ = refSequences.get(sequenceName).getSequenceAsString();
		for (Entry entry : refSequences.entrySet()) {
			System.out.println("current ref name: " + entry.getKey());
			
		}
		int len = refSequences.get(sequenceName).getLength();
		
		return len; 
	}
	
	public ArrayList<String> getNamesOfSequences() {
		ArrayList<String> n = new ArrayList<String>();
		for(Map.Entry<String, DNASequence> entry : refSequences.entrySet()) {
			n.add(entry.getKey());
		}
		return n;
	}
	
	public void setDimensions(Dimension dim) {
		this.dim = dim;
	}
	
	public Dimension getDimensions() {
		return dim;
		
	}
	public int getCurrentSeqLen() {
		return refSequences.get(Controller.getCurrentSeq()).getLength();
	}

}
