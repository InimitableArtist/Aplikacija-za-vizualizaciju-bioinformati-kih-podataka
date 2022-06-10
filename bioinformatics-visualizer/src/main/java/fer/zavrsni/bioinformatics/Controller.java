package fer.zavrsni.bioinformatics;

import java.awt.Dimension;

public class Controller {

	static int xPosition = 0;
	static int readvPos = 0;
	static int annotationvPos = 0;
	static String currentSeq = "";
	static boolean fastaLoaded = false;
	static boolean readsLoaded = false;
	static boolean annotationsLoaded = false;
	static MainScreen screen;
	static BEDAnnotaionScreen annotations;
	static ReadScreen reads;
	static RulerScreen rulerScreen;
	static Reference seq;
	
	public static void setScreens(BEDAnnotaionScreen annotations, ReadScreen reads, RulerScreen rulerScreen, Reference seq) {
		Controller.annotations = annotations;
		Controller.reads = reads;
		Controller.rulerScreen = rulerScreen;
		Controller.seq = seq;
	}
	
	public static void setScreen(MainScreen screen) {
		Controller.screen = screen;
	}
	
	public static void repaintAll() {
		rulerScreen.repaint();
		seq.repaint();
		reads.repaint();
		annotations.repaint();
	}
	
	public static void OnZoomChanged(int zoom) {
		rulerScreen.onZoomChange(zoom);
		seq.onZoomChanged(zoom);
		reads.onZoomChanged(zoom);
		annotations.onZoomChanged(zoom);
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
		reads.onPositionChanged(Controller.xPosition, yPosition);
		annotations.onPositionChnaged(Controller.xPosition);
				
	}
	
	public static void onSeqChanged(String seq_) {
		setCurrentSeq(seq_);
		xPosition = 0;
		readvPos = 0;
		
		rulerScreen.onSequenceChange(seq_);
		seq.onSequenceChanged(seq_);
		reads.onSequenceChanged();
		annotations.onSequenceChanged(seq_);
		
		
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
		reads.reset();
		annotations.reset();
	}
	
	public static void updateWindowSize() {
		seq.setDimensions(new Dimension(screen.getDimensions().width, seq.getDimensions().height));
		reads.setDim(new Dimension(screen.getDimensions().width, reads.getDim().height));
		annotations.setDim(new Dimension(screen.getDimensions().width, annotations.getDim().height));
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
		return annotations;
	}

	public static void setAnnotations(BEDAnnotaionScreen annotations) {
		Controller.annotations = annotations;
	}

	public static ReadScreen getReads() {
		return reads;
	}

	public static void setReads(ReadScreen reads) {
		Controller.reads = reads;
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
