package fer.zavrsni.bioinformatics;

import java.awt.Dimension;

import javax.swing.JSplitPane;

public class Controller {

	static int xPosition = 0;
	static int readvPos = 0;
	static int annotationvPos = 0;
	static String currentSeq = "";
	static boolean fastaLoaded = false;
	static boolean readsLoaded = false;
	static boolean annotationsLoaded = false;
	static MainScreen screen;
	//static BEDAnnotaionScreen annotations;
	//static ReadScreen reads;
	static JSplitPane splitPanel;
	static RulerScreen rulerScreen;
	static Reference seq;
	
	public static void setScreens(JSplitPane splitPanel, RulerScreen rulerScreen, Reference seq) {
		//Controller.annotations = annotations;
		//Controller.reads = reads;
		Controller.splitPanel = splitPanel;
		Controller.rulerScreen = rulerScreen;
		Controller.seq = seq;
	}
	
	public static void setScreen(MainScreen screen) {
		Controller.screen = screen;
	}
	
	public static void repaintAll() {
		rulerScreen.repaint();
		seq.repaint();
		//reads.repaint();
		//annotations.repaint();
		splitPanel.repaint();
	}
	
	public static void OnZoomChanged(int zoom) {
		rulerScreen.onZoomChange(zoom);
		seq.onZoomChanged(zoom);
		((BEDAnnotaionScreen) splitPanel.getBottomComponent()).onZoomChanged(zoom);
		((ReadScreen) splitPanel.getTopComponent()).onZoomChanged(zoom);
		//annotations.onZoomChanged(zoom);
	}
	
	public static void onPositionChanged(int xPosition, int yPosition) {
		int maxX = seq.getCurrentSeqLen() - (int)(screen.getDimensions().width * rulerScreen.getZoomMulti());
		if (xPosition > maxX) {
			Controller.xPosition = maxX;
		} else {
			Controller.xPosition = xPosition;
		}
		
		Controller.readvPos = yPosition;
		
		rulerScreen.onPositionChange(Controller.xPosition);
		seq.onPositionChanged(Controller.xPosition);
		//reads.onPositionChanged(Controller.xPosition, yPosition);
		//annotations.onPositionChnaged(Controller.xPosition);
		((BEDAnnotaionScreen) splitPanel.getBottomComponent()).onPositionChnaged(Controller.xPosition);
		((ReadScreen) splitPanel.getTopComponent()).onPositionChanged(Controller.xPosition, yPosition);;
		
				
	}
	
	public static void onSeqChanged(String seq_) {
		setCurrentSeq(seq_);
		xPosition = 0;
		readvPos = 0;
		
		rulerScreen.onSequenceChange(seq_);
		seq.onSequenceChanged(seq_);
		((BEDAnnotaionScreen) splitPanel.getBottomComponent()).onSequenceChanged(seq_);
		((ReadScreen) splitPanel.getTopComponent()).onSequenceChanged();
		//reads.onSequenceChanged();
		//annotations.onSequenceChanged(seq_);
		
		
	}
	
	public static void reset() {
		xPosition = 0;
		readvPos = 0;
		annotationvPos = 0;
		setFastaLoaded(false);
		setReadsLoaded(false);
		setAnnotationsLoaded(false);
		
		rulerScreen.reset();
		seq.reset();
		((BEDAnnotaionScreen) splitPanel.getBottomComponent()).reset();
		((ReadScreen) splitPanel.getTopComponent()).reset();
		//reads.reset();
		//annotations.reset();
	}
	
	public static void updateWindowSize() {
		seq.setDimensions(new Dimension(screen.getDimensions().width, seq.getDimensions().height));
		//reads.setDim(new Dimension(screen.getDimensions().width, reads.getDim().height));
		//annotations.setDim(new Dimension(screen.getDimensions().width, annotations.getDim().height));
		((BEDAnnotaionScreen) splitPanel.getBottomComponent()).setDim(new Dimension(screen.getDimensions().width, ((BEDAnnotaionScreen) splitPanel.getBottomComponent()).getDim().height));
		((ReadScreen) splitPanel.getTopComponent()).setDim(new Dimension(screen.getDimensions().width, ((ReadScreen) splitPanel.getTopComponent()).getDim().height));
		rulerScreen.setDimension(new Dimension(screen.getDimensions().width, rulerScreen.getDimension().height));
		rulerScreen.onZoomChange(rulerScreen.getZoom());
		repaintAll();
	}

	public static int getxPosition() {
		return xPosition;
	}

	public static void setxPosition(int xPosition) {
		Controller.xPosition = xPosition;
	}

	public static int getReadvPos() {
		return readvPos;
	}

	public static void setReadvPos(int readvPos) {
		Controller.readvPos = readvPos;
	}

	public static int getAnnotationvPos() {
		return annotationvPos;
	}

	public static void setAnnotationvPos(int annotationvPos) {
		Controller.annotationvPos = annotationvPos;
	}

	public static String getCurrentSeq() {
		return currentSeq;
	}

	public static void setCurrentSeq(String currentSeq) {
		Controller.currentSeq = currentSeq;
	}

	public static boolean isFastaLoaded() {
		return fastaLoaded;
	}

	public static void setFastaLoaded(boolean fastaLoaded) {
		Controller.fastaLoaded = fastaLoaded;
		screen.getVismenuBar().fastaLoaded(fastaLoaded);
		if (fastaLoaded == true) {
			rulerScreen.enableR();
		}
	}

	public static boolean isReadsLoaded() {
		return readsLoaded;
	}

	public static void setReadsLoaded(boolean readsLoaded) {
		Controller.readsLoaded = readsLoaded;
		screen.getRulerScreen().readsLoaded(readsLoaded);
		screen.getVismenuBar().readsLoaded(readsLoaded);
	}

	public static boolean isAnnotationsLoaded() {
		return annotationsLoaded;
	}

	public static void setAnnotationsLoaded(boolean annotationsLoaded) {
		Controller.annotationsLoaded = annotationsLoaded;
		screen.getRulerScreen().annotationsLoaded(annotationsLoaded);
	}

	public static BEDAnnotaionScreen getAnnotations() {
		//return annotations;
		return (BEDAnnotaionScreen) splitPanel.getBottomComponent();
	}

	public static void setAnnotations(BEDAnnotaionScreen annotations) {
		//Controller.annotations = annotations;
		Controller.splitPanel.setBottomComponent(annotations);
	}

	public static ReadScreen getReads() {
		//return reads;
		return (ReadScreen) splitPanel.getTopComponent();
	}

	public static void setReads(ReadScreen reads) {
		//Controller.reads = reads;
		Controller.splitPanel.setTopComponent(reads);
	}

	public static RulerScreen getRulerScreen() {
		return rulerScreen;
	}

	public static void setRulerScreen(RulerScreen rulerScreen) {
		Controller.rulerScreen = rulerScreen;
	}

	public static Reference getSeq() {
		return seq;
	}

	public static void setSeq(Reference seq) {
		Controller.seq = seq;
	}

	public static MainScreen getScreen() {
		return screen;
	}
	
	
}
