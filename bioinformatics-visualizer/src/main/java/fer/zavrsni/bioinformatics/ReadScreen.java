package fer.zavrsni.bioinformatics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


import java.awt.BorderLayout;

import htsjdk.samtools.Cigar;

public class ReadScreen extends JPanel {

		
	private Scrollbar scrollbar;
	private static final int GAP_SIZE = 10;
	private static final int HISTOGRAM_OFFSET = 0;
	private static final int READS_OFFSET = HISTOGRAM_OFFSET + 100 + GAP_SIZE;
	
	private Dimension dim;
	private MainScreen screen;
	private Map<String, Graph> graphMap;
	private Map<String, ArrayList<Read>> readMap;
	private ArrayList<Read> showReads = new ArrayList<Read>();
	private Read currentlySelectedRead;
	
	//private Scrollbar scrollbar;
	
	public ReadScreen(final MainScreen screen) {
		this.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseClicked(MouseEvent arg0) {
				if (currentlySelectedRead == null) {
					return;
				}
				if(SwingUtilities.isLeftMouseButton(arg0)) {
					//String readName = currentlySelectedRead.getReadName();
					//String finalString = readName;
					//StringSelection selection = new StringSelection(finalString);
					//Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					//clipboard.setContents(selection, null);
					ReadScreen.this.currentlySelectedRead.setSelected(true);
					screen.getRulerScreen().readSelected(true);
					repaint();
				} else if (SwingUtilities.isRightMouseButton(arg0)) {
					ReadScreen.this.currentlySelectedRead.setSelected(false);
					screen.getRulerScreen().readSelected(false);
					repaint();
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			
			

			public void mouseMoved(MouseEvent arg0) {
				int xCoord = arg0.getX();
				int yCoord = arg0.getY();
				
				
				//Ako nema prikazanog očitanja na trenutnoj lokaciji kursora, ne radi ništa
				Read r = getReadHere(xCoord, yCoord);
				if (r == null) {
					setToolTipText("");
					ReadScreen.this.currentlySelectedRead = null;
					return;
				} 
				//Ako se na trenutačnoj lokaciji kursora nalazi prikazano očitanje, ispiši u tooltip informacije o njemu
				else {
					ReadScreen.this.setCurrentlySelectedRead(r);
					String name = "Name: " + r.getReadName();
					String AlignmentStart = "Alignment start: " + r.getPos();
					String AlignmentEnd = "Alignment end: " + r.getAlignEnd();
					String UnclippedStart = "Unclipped start: " + r.getUnclippedStart();
					String UnclippedEnd = "Unclipped end: " + r.getUnclippedEnd();
					String finalString = "<html>" + name + "<br>" + AlignmentStart + "<br>" + AlignmentEnd + 
							"<br>" + UnclippedStart + "<br>" + UnclippedEnd + "</html>";
					ReadScreen.this.setToolTipText(finalString);
				}
				//return;
				
			}
			
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.dim = new Dimension(screen.getDimensions().width, 20);
		this.screen = screen;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
		setFocusable(true);
		
		
		repaint();
		
	}
	
	public void setTooltip(String s) {
		this.setTooltip(s);
	}

	public void paintComponent(Graphics g) {
			
		
		super.paintComponent(g);
		showReads.clear();
		//ArrayList<Read> currentReads = getCurrentReads();
		if (readMap == null) {
			System.out.println("Read map is null...");
			return;
		}
		if (getCurrentReads() == null) {
			return;
		}
		ArrayList<Read> currentReads = getCurrentReads();
		double zoomMulti = screen.getRulerScreen().getZoomMulti();
		int XPos = Controller.getxPosition();
		int YPos = Controller.getReadvPos();
		
		for (Read r : currentReads) {

			//Read r;
			int onScreenPos = r.onScreenPosition(XPos, zoomMulti, dim);
			if (onScreenPos == 1) {
				break;
			} else if (onScreenPos == -1) { 
				continue;
			} else {
				showReads.add(r);
				int pos = READS_OFFSET + 30 * r.getGraphicPosition() - 30 * YPos;
				if (pos < READS_OFFSET) {
					continue;
				}
				int rStart = (int)((r.getPos() - XPos) / zoomMulti);
				int rectLen = 0;
				int rectStart = rStart;
				int position_ = 0;
				
				for (BEDGap gap : r.getGaps()) {
					rectStart = rStart + (int)(position_ / zoomMulti);
					rectLen = (int) ((int)((gap.getPos()) - position_) / zoomMulti);
					g.setColor(ColorPalette.getReadColor(r.checkReverseComplement(), false));
					g.fillRect(rectStart, pos, rectLen, 20);
					
					rectStart += rectLen;
					rectLen = (int)(gap.getLen() / zoomMulti);
					g.setColor(ColorPalette.getReadColor(r.checkReverseComplement(), true));
					g.fillRect(rectStart, pos, rectLen, 20);
					position_ = gap.getPos() + gap.getLen();
				}
				
				
				
				rectStart += rectLen;
				int unclippedStart = r.getPos() - r.getUnclippedStart();
			    int unclippedEnd = r.getUnclippedEnd() - r.getPos();
			    
			    
			    rectLen = (int)((r.getReadLen() - position_) / zoomMulti);
			    g.setColor(new Color(157, 105, 163, 70));
			    g.fillRect(rectStart - unclippedStart, pos + 2, rectLen + unclippedEnd, 12);
				
			    g.setColor(ColorPalette.getReadColor(r.checkReverseComplement(), false));
				g.fillRect(rectStart, pos, rectLen, 20);

				
				
				 
				if (r.getFindSelected()) {
					rectStart =  rStart;
					rectLen = (int)(r.getReadLength() / zoomMulti);
					Highlight(g, rectStart, rectLen, pos, new Color(97, 112, 125));
				}
				else if (r.getSelected()) {
					rectStart = rStart;
					rectLen = (int)(r.getReadLen() / zoomMulti);
					Highlight(g, rectStart, rectLen, pos, Color.BLUE);
					
				}
  			}
		}
		buildGraph(g);
	}
	
	private void buildGraph(Graphics g) {
		Graph currentGrap = getCurrentGraph();
		if (graphMap == null) {
			return;
		}
		if (currentGrap == null) {
			return;
		}
		
		int xPos = Controller.getxPosition();
		
		int amp = currentGrap.getAmp();
		int data[] = currentGrap.getData();
		String currentSeq = "chrI";
		int zoom = screen.getRulerScreen().getZoom();
		
		if (!(currentSeq == null)) {
			if (zoom >= 0) {
				int recW;
				switch (zoom) {
				case 0:
					recW = 1;
					break;
				case 1:
					recW = 5;
					break;
				case 2:
					recW = 10;
					break;

					

				default:
					recW = 20;
					break;
				}
				
				for (int i = 0; i < dim.width/recW; i++) {
					int l = (int)((double)data[i + xPos] / amp * 100);
					g.setColor(new Color(97, 112, 125));
					g.fillRect(i*recW, 100 - l + HISTOGRAM_OFFSET, recW, l);
				}
			} else if (zoom < 0) {
				int n = (-zoom) * 5;
				for (int i = 0; i < dim.width; i++) {
					double avg = 0;
					for (int j = 0; j < n; j++) {
						int pos = xPos + i * n + j;
						avg += data[pos];
					}
					
					avg = avg / n;
					int l = (int)(avg / amp * 100);
					g.setColor(new Color(97, 112, 125));
					g.fillRect(i, 100 - l + HISTOGRAM_OFFSET, 1, l);
				}
			}
		}
	}
	
	
	public void Highlight(Graphics g, int rectStart, int rectLen, int pos, Color color) {
		g.setColor(color);
		g.fillRect(rectStart, pos, rectLen, 4);
		g.fillRect(rectStart, pos + 16, rectLen, 4);
		g.fillRect(rectStart, pos, 4, 20);
		g.fillRect(rectStart +  rectLen - 4, pos, 4, 20);
	}
	public Read getReadHere(int x, int y) {
		double zoomMulti = screen.getZoom().getZoomMulti();
		int hPos = Controller.getxPosition();
		int vPos = Controller.getReadvPos();
		
		for (Read r : showReads) {
			int rStart = (int)((r.getPos() - hPos) / zoomMulti);
			int rEnd = rStart + (int)(r.getReadLen() / zoomMulti);
			int readYPos = READS_OFFSET + 30 * r.getGraphicPosition() - 30 * vPos;
			if (x > rStart && x < rEnd && y > readYPos && y < (readYPos + 20)) {
				return r;
			}
		}
		return null;
		
	}
	
	public void buildScrollbar() {
		if (getCurrentGraph() == null) {
			return;
		}
		scrollbar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 1, getCurrentGraph().getAmp());
		scrollbar.setFocusable(true);
		scrollbar.addAdjustmentListener(new AdjustmentListener() {
			
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				int horizontalPos = Controller.getxPosition();
				int position = arg0.getValue() - 1;
				Controller.onPositionChanged(horizontalPos, position);
			}
		});
		this.add(scrollbar, BorderLayout.EAST);
		revalidate();
		repaint();
	}
	
	public void onZoomChanged(int zoom) {
		repaint();
	}
	
	public void onPositionChanged(int x, int y) {
		if (scrollbar != null) {
			scrollbar.setValue(y);
		}
		repaint();
	}
	
	public void onSequenceChanged() {
		if (scrollbar != null) {
			this.remove(scrollbar);
			buildScrollbar();
		}
	} 
	
	public void reset() {
		Controller.setReadsLoaded(false);
		readMap = null;
		graphMap = null;
		if (scrollbar != null) {
			this.remove(scrollbar);
		}
	}
	
	public Dimension getDim() {
		return dim;
	}

	public void setDim(Dimension dim) {
		this.dim = dim;
	}

	public Map<String, Graph> getGraphMap() {
		return graphMap;
	}

	public void setGraphMap(Map<String, Graph> graphMap) {
		this.graphMap = graphMap;
	}

	public Map<String, ArrayList<Read>> getReadMap() {
		return readMap;
	}

	public void setReadMap(Map<String, ArrayList<Read>> readMap) {
		this.readMap = readMap;
	}

	public ArrayList<Read> getShowReads() {
		return showReads;
	}

	public void setShowReads(ArrayList<Read> showReads) {
		this.showReads = showReads;
	}

	public Read getCurrentlySelectedRead() {
		return this.currentlySelectedRead;
	}

	public void setCurrentlySelectedRead(Read currentlySelectedRead) {
		this.currentlySelectedRead = currentlySelectedRead;
		this.screen.getRulerScreen().readSelected(true);
	}

	
	public Graph getCurrentGraph() {
		return graphMap.get(Controller.getCurrentSeq());
	}
	public ArrayList<Read> getCurrentReads() {
		return readMap.get(Controller.getCurrentSeq());
	}
}
