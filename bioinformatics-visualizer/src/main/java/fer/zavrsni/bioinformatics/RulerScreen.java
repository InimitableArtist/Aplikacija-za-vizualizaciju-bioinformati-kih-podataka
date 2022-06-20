package fer.zavrsni.bioinformatics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.biojava.nbio.core.sequence.DNASequence;

public class RulerScreen extends JPanel{
	
	private int minZoomLevel = -6;
	private int numZoomLevels = 5;
	
	public Dimension dim;
	private MainScreen screen;
	private int zoom, XCoords;
	private String currentSeq = "";
	private JComboBox<String> chromSel;
	private JPanel buttonControlPanel;
	private JButton findAnnotationBtn;
	private JButton findReadBtn;
	private JButton getCIGARBtn;
	
	
	JSlider zoomSlider;
	private JButton search;
	private JLabel bp;
	
	private Read prevRead;
	private BEDAnnotation prevAnnotation;
	
	public RulerScreen(final MainScreen screen) {
		this.screen = screen;
		zoom = 0;
		XCoords = 0;
		
		zoomSlider = new JSlider(minZoomLevel, numZoomLevels, minZoomLevel);
		zoomSlider.setEnabled(true);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setPaintLabels(true);
		
		findAnnotationBtn = new JButton("Find Annotation");
		findAnnotationBtn.setEnabled(false);
		
		findReadBtn = new JButton("Find Read");
		findReadBtn.setEnabled(false);
		
		search = new JButton("Go to Read");
		
		getCIGARBtn = new JButton("Get CIGAR");
		getCIGARBtn.setEnabled(false);
		
		buttonControlPanel = new JPanel();
		
		chromSel = new JComboBox<String>();
		chromSel.setEnabled(false);
		bp = new JLabel("");
		
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				
				RulerScreen.this.zoom = zoomSlider.getValue();
				
				
				Controller.OnZoomChanged(RulerScreen.this.zoom);
			}
		});
		chromSel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				String str = (String)chromSel.getSelectedItem();
				if(!str.equals(currentSeq)) {
					Controller.onSeqChanged(str);
					//System.out.println("Ovo radi.");
				}
				
			}
		});
		search.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				int seqLen = screen.getRef().getCurrentSeqLen();
				String msg = "Jump location: (0 - " + seqLen + "):";
				String input = "";
				int pos;
				input = JOptionPane.showInputDialog(msg);
				pos = Integer.parseInt(input);
				
				Controller.onPositionChanged(pos, Controller.getReadvPos());
			}
		});
		findAnnotationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String filter = JOptionPane.showInputDialog(null, "Enter the annotation: ", "", JOptionPane.PLAIN_MESSAGE, null, null, "").toString();
				
				final ArrayList<BEDAnnotation> filterAnns = new ArrayList<BEDAnnotation>();
				for (BEDAnnotation an : screen.getAnnotations().getCurrentAnnotations()) {
					if (an.getLineName().toLowerCase().contains(filter.toLowerCase())) {
						filterAnns.add(an);
					}
				}
				if (filterAnns.isEmpty()) {
					JOptionPane.showMessageDialog(null, "There were no annotations found.");
					return;
				}
				int matchNumbers = filterAnns.size();
				String msg = matchNumbers + " matches Found.";
				JButton next = new JButton();
				next.setText("Next match");
				Object[] dialogObjects = new Object[]{msg, next};
				next.addActionListener(new SearchListener(matchNumbers, dialogObjects) {
					int currentMatch = getCurrent();
					int matchSize = getMatchSize();
					@Override
					public void actionPerformed(ActionEvent e) {
						if (currentMatch + 1 > matchSize) {
							setCurrent(1);
						} else {
							inc();
						}
						BEDAnnotation matchedAnn = filterAnns.get(currentMatch - 1);
						matchedAnn.setGetHighlighted(true);
						prevAnnotation.setGetHighlighted(false);
						screen.getAnnotations().setyPosition(matchedAnn.getGraphicalPosition());
						fer.zavrsni.bioinformatics.Controller.onPositionChanged(matchedAnn.getStart(), fer.zavrsni.bioinformatics.Controller.getReadvPos());
						prevAnnotation = matchedAnn;
					}
				});
				
				filterAnns.get(0).setGetHighlighted(true);
				prevAnnotation = filterAnns.get(0);
				screen.getAnnotations().setyPosition(filterAnns.get(0).getGraphicalPosition());
				fer.zavrsni.bioinformatics.Controller.onPositionChanged(filterAnns.get(0).getStart(), fer.zavrsni.bioinformatics.Controller.getReadvPos());
				
				JOptionPane.showMessageDialog(null, dialogObjects);
				prevAnnotation.setGetHighlighted(false);
				screen.getAnnotations().repaint();
				
			}
		}); 
		findReadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String currentClipboard = "";
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable content = clipboard.getContents(null);
				boolean hasTransferableText = (content != null) && content.isDataFlavorSupported(DataFlavor.stringFlavor);
				
				if (hasTransferableText) {
					try {
						currentClipboard = (String)content.getTransferData(DataFlavor.stringFlavor);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String filter = JOptionPane.showInputDialog(null, "Enter read: ", "Find a read", JOptionPane.PLAIN_MESSAGE, null, null, currentClipboard).toString();
				final ArrayList<Read> fltReads = new ArrayList<Read>();
				ArrayList<Read> reads = screen.getReads().getCurrentReads();
				ArrayList<Read> readsString = new ArrayList<Read>(reads);
				
				ReadComparator comp = new ReadComparator();
				Collections.sort(readsString, comp);
				Read filterRead = new Read(filter);
				int index = Collections.binarySearch(readsString, filterRead, comp);
				if (index < 0) {
					JOptionPane.showMessageDialog(null, "No matching reads were found.");
					return;
				}
				Read foundRead = readsString.get(index);
				
				
				String msg = "A match was found!";

				
				foundRead.setFindSelected(true);
				fer.zavrsni.bioinformatics.Controller.onPositionChanged(foundRead.getPos(), foundRead.getGraphicPosition());
				
				
				JOptionPane.showMessageDialog(null, msg);
				
				screen.getReads().repaint();
				
			}
			
		});
		
		
		getCIGARBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				Read g = screen.getReads().getCurrentlySelectedRead();
				System.out.println(g.toString());
				
			}
		});
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		buttonControlPanel.add(chromSel);
		buttonControlPanel.add(zoomSlider);
		buttonControlPanel.add(search);
		buttonControlPanel.add(findAnnotationBtn);
		buttonControlPanel.add(findReadBtn);
		buttonControlPanel.add(getCIGARBtn);
		buttonControlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		bp.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(buttonControlPanel);
		this.add(bp);
		
		dim = new Dimension();
		
		dim.width = screen.getDimensions().width;
		dim.height = 110;
		buttonControlPanel.setMaximumSize(new Dimension(dim.width, 45));
		this.setPreferredSize(dim);
		//this.setVisible(true);
		
		repaint();
	}
	
	public void onZoomChange(int zoom) {
		this.zoom = zoom;
		if (zoom == 0) {
			bp.setText("Zoom: " + (int)dim.width + " bp");
		} else {
			bp.setText("Zoom: " + (int)(dim.width * getZoomMulti()) + " bp");
			
		}
		repaint();
	}
	
	public void onPositionChange(int XCoords) {
		this.XCoords = XCoords;
		repaint();
	}
	
	public void onSequenceChange(String seq_) {
		currentSeq = seq_;
		onPositionChange(0);
		onZoomChange(0);
	}
	
	void reset() {
		zoom = 0;
		XCoords = 0;
		bp.setText("");
		currentSeq = "";
		zoomSlider.setEnabled(false);
		search.setEnabled(false);
		chromSel.setModel(new JComboBox<String>().getModel());
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public JLabel getBp() {
		return bp;
	}

	public void setBp(JLabel bp) {
		this.bp = bp;
	}
	public double getZoomMulti() {
		if (zoom == 0) {
			return 1;
		}
		else if (zoom > 0) {
			return 1.0 / (5 * zoom);
		} else {
			return 1.0 * 5 * (-zoom);
		}
	}
	
	public void loadSeq(LinkedHashMap<String, DNASequence> sequences) {
		for (Entry<String, DNASequence> entry: sequences.entrySet()) {
			chromSel.addItem(entry.getKey());
		}
		chromSel.setEnabled(true);
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if (Controller.fastaLoaded) {
			int lineH = dim.height - 10;
			g.fillRect(0, lineH, dim.width, 1);
			for (int i = 0; i <= dim.width/100 + 1; i++) {
				String str = "" + (int)(XCoords + (i * getZoomMulti()) * 100) + " bp";
				int linePos = i * 100;
				int strPos = g.getFontMetrics().stringWidth(str);
				
				g.setFont(new Font("Serif", Font.BOLD, 12));
				g.fillRect(linePos, lineH - 15, 1, 15);
				g.drawString(str, linePos - strPos / 2, 80);
			}
			
		}
 		
	}
	public Dimension getDimension() {
		return dim;
	}
	public void setDimension(Dimension dim) {
		this.dim = dim;
	}
	
	public void OnPositionChanged(int xpos) {
		this.XCoords = xpos;
	}
	
	public void enableR() {
		zoomSlider.setEnabled(true);
		search.setEnabled(true);
		bp.setText("Zoom: " + dim.width * getZoomMulti() + " bp");
		//bp.setText("a : " + getZoomMulti());
	}
	public void readsLoaded(boolean isItLoaded) {
		findReadBtn.setEnabled(true);
	}
	public void annotationsLoaded(boolean isItLoaded) {
		findAnnotationBtn.setEnabled(true);
	}
	
	public void readSelected(boolean isItSelected) {
		getCIGARBtn.setEnabled(isItSelected);
	}
	
	
}
