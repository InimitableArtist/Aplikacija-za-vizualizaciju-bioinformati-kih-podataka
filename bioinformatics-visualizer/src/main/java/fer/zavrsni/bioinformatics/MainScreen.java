package fer.zavrsni.bioinformatics;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainScreen extends JFrame implements AWTEventListener{
	private Dimension dimensions;
	private VisMenuBar vismenuBar;
	private Reference reference;
	private ReadScreen readScreen;
	private RulerScreen rulerScreen;
	private JPanel mainPanel;
	private JSplitPane splitPanel;
	private BEDAnnotaionScreen annotations;
	
	
	public MainScreen() {
		this.setLayout(new BorderLayout());
		
		this.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		this.vismenuBar = new VisMenuBar(this);
		this.dimensions = new Dimension(1280, 720);
		
		this.addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void componentResized(ComponentEvent arg0) {
				MainScreen.this.dimensions = arg0.getComponent().getBounds().getSize();
				Controller.updateWindowSize();
				
			}

			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		setMinimumSize(this.dimensions);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Bioinformatics Visualizer");
		setJMenuBar(this.vismenuBar);
		pack();
		setVisible(true);
		
		
		this.readScreen = new ReadScreen(this);
		this.rulerScreen = new RulerScreen(this);
		this.reference = new Reference(this);
		this.annotations = new BEDAnnotaionScreen(this);
		this.splitPanel = new JSplitPane(SwingConstants.HORIZONTAL);
		this.splitPanel.setTopComponent(readScreen);
		this.splitPanel.setBottomComponent(annotations);
		this.splitPanel.getTopComponent().setSize(splitPanel.getWidth(), splitPanel.getHeight());
		Controller.setScreen(this);
		//Controller.setScreens(annotations, readScreen, rulerScreen, reference);
		Controller.setScreens(splitPanel, rulerScreen, reference);
		this.mainPanel = new JPanel();
		
		add(this.mainPanel, BorderLayout.CENTER);
		this.mainPanel.setLayout(new BorderLayout());
		//this.mainPanel.add(readScreen, BorderLayout.CENTER);
		//this.mainPanel.add(annotations, BorderLayout.NORTH);
		add(rulerScreen, BorderLayout.NORTH);
		add(reference, BorderLayout.SOUTH);
		this.mainPanel.add(splitPanel, BorderLayout.CENTER);
		
		//this.add(mainPanel, BorderLayout.CENTER);
		//mainPanel.setVisible(true);
		//this.setVisible(true);
		
	}
	
	public RulerScreen getZoom() {
		return this.rulerScreen;
	}
	
	public ReadScreen getReads() {
		return this.readScreen;
	}
	
	public Reference getRef() {
		return this.reference;
	}
	//public AnnotationScreen getAnnot() {return annotationScreen;}
	
	public Dimension getDimensions() {
		return this.dimensions;
	}

	public void setDimensions(Dimension dimensions) {
		this.dimensions = dimensions;
	}



	public VisMenuBar getVismenuBar() {
		return vismenuBar;
	}

	public void setVismenuBar(VisMenuBar vismenuBar) {
		this.vismenuBar = vismenuBar;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}
	public void eventDispatched(AWTEvent arg0) {
		if (arg0 instanceof KeyEvent) {
			KeyEvent k = (KeyEvent)arg0;
			if (k.getID() == KeyEvent.KEY_PRESSED) {
				int keyPressed = k.getKeyCode();
				
				int currentXCoords = Controller.getxPosition();
				int currentYCoords = Controller.getReadvPos();
				int XCoordsDelta = (int)(rulerScreen.getZoomMulti() * 250);
				int YCoordsDelta = 1;
				
				switch(keyPressed) {
				case KeyEvent.VK_DOWN:
					currentYCoords += YCoordsDelta;
					break;
				case KeyEvent.VK_UP:
					if (currentYCoords - YCoordsDelta < 0) {
						YCoordsDelta = 0; }
						else {
							currentYCoords -= YCoordsDelta;
						}
					break;
				case KeyEvent.VK_LEFT:
					if (currentXCoords - XCoordsDelta < 0) {
						currentXCoords = 0;
					} else {currentXCoords -= XCoordsDelta;}
					break;
				case KeyEvent.VK_RIGHT:
					currentXCoords += XCoordsDelta;
				}
				
				Controller.onPositionChanged(currentXCoords, currentYCoords);
				k.consume();
			}
		}
		
	}

	public ReadScreen getReadScreen() {
		return readScreen;
	}

	public void setReadScreen(ReadScreen readScreen) {
		this.readScreen = readScreen;
	}

	public RulerScreen getRulerScreen() {
		return rulerScreen;
	}

	public void setRulerScreen(RulerScreen rulerScreen) {
		this.rulerScreen = rulerScreen;
	}

	public Reference getReference() {
		return reference;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public BEDAnnotaionScreen getAnnotations() {
		return annotations;
	}

	public void setAnnotations(BEDAnnotaionScreen annotations) {
		this.annotations = annotations;
	}



}
