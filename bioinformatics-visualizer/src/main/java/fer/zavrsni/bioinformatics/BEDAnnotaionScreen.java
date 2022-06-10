package fer.zavrsni.bioinformatics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.BorderLayout;
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

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class BEDAnnotaionScreen extends JPanel{
	
	private MainScreen screen;
	private Dimension dim;
	private Map<String, ArrayList<BEDAnnotation>> BedMap;
	private int xPosition = 0;
	private int yPosition = 0;
	private int amp;
	private Scrollbar scrollbar;
	private ArrayList<BEDAnnotation> showAnnotations = new ArrayList<BEDAnnotation>();
	private BEDAnnotation currentlySelectedAnnotation;
	
	public BEDAnnotaionScreen(MainScreen screen) {
		this.screen = screen;
		dim = new Dimension(screen.getDimensions());
		dim.height = 50;
		
		setLayout(new BorderLayout());
		this.setPreferredSize(dim);
		
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
				if (currentlySelectedAnnotation == null) {
					return;
				}
				if (SwingUtilities.isRightMouseButton(arg0)) {
					currentlySelectedAnnotation.setHighlighted(!currentlySelectedAnnotation.isHighlighted());
					repaint();
				}
				
				if (SwingUtilities.isLeftMouseButton(arg0)) {
					String name = "Name: " + currentlySelectedAnnotation.getName();
					StringSelection stringSel = new StringSelection(name);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSel, null);
					
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();
				
				BEDAnnotation annotation = getOnPosition(x, y);
				if (annotation == null) {
					setToolTipText("");
					currentlySelectedAnnotation = null;
					return;
				} else {
					currentlySelectedAnnotation = annotation;
					String name = "Name: " + annotation.getLineName();
					setToolTipText(name);
				}
				
			}
			
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		this.setVisible(true);
	}
	
	public BEDAnnotation getOnPosition(int x, int y) {
		double zoomMulti = screen.getZoom().getZoomMulti();
		int xPos = Controller.getxPosition();
		int yPos = Controller.getAnnotationvPos();
		if (showAnnotations == null) {
			return null;
		}
		for (BEDAnnotation a : showAnnotations) {
			int aStart = (int)((a.getStart() - xPos) / zoomMulti);
			int aEnd = aStart + (int)(a.getLen() / zoomMulti);
			int avPos = 30 * a.getGraphicalPosition() - 30 * yPos;
			if (x > aStart && x < aEnd && y > avPos && y < (avPos + 20)) {
				return a;
			}
		}
		return null;
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		buildAnnotations(g);
	}
	
	private void buildAnnotations(Graphics g) {
		showAnnotations.clear();
		if (BedMap == null) {
			return;
		}
		
		
		if (getCurrentAnnotations() == null) {
			return;
		}
		ArrayList<BEDAnnotation> current = getCurrentAnnotations();
		
		for (BEDAnnotation a : current) {
			double zoomMulti = screen.getZoom().getZoomMulti();
			int posOnScreen = a.getPosition(xPosition, dim, zoomMulti);
			
			if (posOnScreen == -1) {
				continue;
			}
			else if (posOnScreen == 1) {
				break;
			}
			showAnnotations.add(a);
			int pos = 30 * a.getGraphicalPosition() - 30 * yPosition;
			int rectStart = (int)((a.getChromStart() - xPosition) / zoomMulti);
			int rectLen = (int)((a.getChromEnd() - a.getChromStart()) / zoomMulti);
			//System.out.println("chrom end: " + a.getChromEnd() + " chrom start: " + a.getChromStart() + " nesto: " + a.getChromName() + " nesto: " + a.getLineName());
			
			
			g.setColor(ColorPalette.BEDAnnotaionColor(a.getStrand().toString(), false));
			g.fillRect(rectStart, pos + 5, rectLen, 10);
			
			rectStart = (int)((a.getThickStart() - xPosition) / zoomMulti);
			rectLen = (int)((a.getThickEnd() - a.getThickStart()) / zoomMulti);
			g.fillRect(rectStart, pos, rectLen, 20);
			
			
			g.setColor(ColorPalette.BEDAnnotaionColor(a.getStrand().toString(), true));
			for (BEDGap gap : a.getGaps()) {
				rectStart = (int)((gap.getPos() - xPosition) / zoomMulti);
				rectLen = (int)((gap.getLen()) / zoomMulti);
				g.fillRect(rectStart, pos, rectLen, 20);
			}
			
			if (a.isGetHighlighted()) {
				rectStart = (int)((a.getStart() - xPosition) / zoomMulti);
				rectLen = (int)((a.getEnd() - a.getStart()) / zoomMulti);
				createHighlight(g, rectStart, rectLen, pos, new Color(97, 112, 125));
			} else if (a.isHighlighted()) {
				rectStart = (int)((a.getStart() - xPosition) / zoomMulti);
				rectLen = (int)((a.getEnd() - a.getStart()) / zoomMulti);
				createHighlight(g, rectStart, rectLen, pos, Color.BLACK);
			}
			
		}
	}
	public void createHighlight(Graphics g, int rs, int rl, int pos, Color color) {
		g.setColor(color);
		g.fillRect(rs, pos, rl, 4);
		g.fillRect(rs, pos + 16, rl, 4);
		g.fillRect(rs, pos, 4, 20);
		g.fillRect(rs + rl - 4, pos, 4, 20);
	}
	
	public void onPositionChnaged(int xPosition) {
		this.xPosition = xPosition;
		repaint();
	}
	
	public void onZoomChanged(int zoom) {
		repaint();
	}
	public void onSequenceChanged(String seq) {
		xPosition = Controller.getxPosition();
		yPosition = 0;
		
		if (scrollbar != null) {
			this.remove(scrollbar);
			buildScrollbar();
		}
		repaint();
	}
	
	public void reset() {
		Controller.annotationsLoaded = false;
		yPosition = 0;
		amp = 0;
		BedMap = null;
		if (scrollbar != null) {
			this.remove(scrollbar);
		}
	}
	
	public void buildScrollbar() {
		scrollbar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 1, amp);
		scrollbar.setFocusable(true);
		scrollbar.addAdjustmentListener(new AdjustmentListener() {
			
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				int position = arg0.getValue();
				yPosition = position - 1;
				repaint();
				
			}
		});
		
		this.add(scrollbar, java.awt.BorderLayout.EAST);
		revalidate();
		repaint();
	}
	
	public ArrayList<BEDAnnotation> getCurrentAnnotations() {
		return BedMap.get(Controller.getCurrentSeq());
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

	public Map<String, ArrayList<BEDAnnotation>> getBedMap() {
		return BedMap;
	}

	public void setBedMap(Map<String, ArrayList<BEDAnnotation>> bedMap) {
		BedMap = bedMap;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getAmp() {
		return amp;
	}

	public void setAmp(int amp) {
		this.amp = amp;
	}

	public Scrollbar getScrollbar() {
		return scrollbar;
	}

	public void setScrollbar(Scrollbar scrollbar) {
		this.scrollbar = scrollbar;
	}

	public ArrayList<BEDAnnotation> getShowAnnotations() {
		return showAnnotations;
	}

	public void setShowAnnotations(ArrayList<BEDAnnotation> showAnnotations) {
		this.showAnnotations = showAnnotations;
	}

	public BEDAnnotation getCurrentlySelectedAnnotation() {
		return currentlySelectedAnnotation;
	}

	public void setCurrentlySelectedAnnotation(BEDAnnotation currentlySelectedAnnotation) {
		this.currentlySelectedAnnotation = currentlySelectedAnnotation;
	}

}
