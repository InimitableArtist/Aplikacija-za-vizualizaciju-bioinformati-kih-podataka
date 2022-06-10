package fer.zavrsni.bioinformatics;

import java.awt.Dimension;
import java.util.ArrayList;

import htsjdk.tribble.annotation.Strand;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.bed.SimpleBEDFeature;
public class BEDAnnotation extends SimpleBEDFeature implements Comparable<BEDAnnotation>{
	
	private String chromName;
	private String lineName;
	private int chromStart;
	private int chromEnd;
	private Strand strand;
	private int[] rgb;
	private int graphicalPosition;
	private ArrayList<BEDGap> gaps = new ArrayList<BEDGap>();
	
	private boolean highlighted = false;
	private boolean getHighlighted = false;
	
	private int thickStart;
	private int thickEnd;
	
	//gaplist
	//highlighted
	//find highlighted
	
	public BEDAnnotation(String chromName, int chromStart, int chromEnd, Strand strand, int[] rgb, String lineName, int thickStart, int thickEnd) {
		super(chromStart, chromEnd, chromName);
		this.chromName = chromName;
		this.chromStart = chromStart;
		this.chromEnd = chromEnd;
		this.strand = strand;
		this.rgb = rgb;
		this.lineName = lineName;
		this.thickStart = thickStart;
		this.thickEnd = thickEnd;
	}
	
	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public int getPosition(int horizontal, Dimension dim, double zoom) {
		if (this.chromEnd < horizontal) {
			return - 1;
		} else if (this.chromStart > horizontal + zoom * dim.getWidth()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public int compareTo(BEDAnnotation arg0) {
		return this.chromStart - arg0.getChromStart();
	}
	public int getLen() {
		return this.chromEnd - this.chromStart;
	}
	public String getChromName() {
		return chromName;
	}
	public void setChromName(String chromName) {
		this.chromName = chromName;
	}
	public int getChromStart() {
		return chromStart;
	}
	public void setChromStart(int chromStart) {
		this.chromStart = chromStart;
	}
	public int getChromEnd() {
		return chromEnd;
	}
	public void setChromEnd(int chromEnd) {
		this.chromEnd = chromEnd;
	}
	public Strand getStrand() {
		return strand;
	}
	public void setStrand(Strand strand) {
		this.strand = strand;
	}
	public int[] getRgb() {
		return rgb;
	}
	public void setRgb(int[] rgb) {
		this.rgb = rgb;
	}

	public int getGraphicalPosition() {
		return graphicalPosition;
	}

	public void setGraphicalPosition(int graphicalPosition) {
		this.graphicalPosition = graphicalPosition;
	}

	public ArrayList<BEDGap> getGaps() {
		return gaps;
	}

	public void setGaps(ArrayList<BEDGap> gaps) {
		this.gaps = gaps;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean isGetHighlighted() {
		return getHighlighted;
	}

	public void setGetHighlighted(boolean getHighlighted) {
		this.getHighlighted = getHighlighted;
	}

	public int getThickStart() {
		return thickStart;
	}

	public void setThickStart(int thickStart) {
		this.thickStart = thickStart;
	}

	public int getThickEnd() {
		return thickEnd;
	}

	public void setThickEnd(int thickEnd) {
		this.thickEnd = thickEnd;
	}

}
