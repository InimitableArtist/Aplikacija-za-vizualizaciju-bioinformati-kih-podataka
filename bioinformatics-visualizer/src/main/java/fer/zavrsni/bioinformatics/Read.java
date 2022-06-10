package fer.zavrsni.bioinformatics;

import java.awt.Dimension;
import java.util.ArrayList;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;

public class Read extends SAMRecord implements Comparable<Read>{

	private String readName;
	private String refName;
	private int pos;
	private int mapQ;
	private Cigar cigar;	
	private boolean reverseComplement;
	private int graphicPosition;
	private int readLen;
	private ArrayList<BEDGap> gaps = new ArrayList<BEDGap>();
	private boolean selected = false;
	private boolean findSelected = false;
	int gapLen = 0;
	private int alignEnd;
	private int unclippedStart;
	private int unclippedEnd;
	
	public Read(SAMFileHeader header, String readName, String refName, int pos,  Cigar cigar,
			boolean reverseComplement, int readLen, int mapQ, int alignEnd, int unclippedStart, int unclippedEnd) {
		super(header);
		this.readName = readName;
		this.refName = refName;
		this.pos = pos;
		this.mapQ = mapQ;
		this.cigar = cigar;
		this.alignEnd = alignEnd;
		this.reverseComplement = reverseComplement;
		this.readLen = readLen;
		this.unclippedStart = unclippedStart;
		this.unclippedEnd = unclippedEnd;
		
		if (cigar != null) {
			for (CigarElement ce : cigar.getCigarElements()) {
				int operationLen = ce.getLength();
				//System.out.println(ce.getOperator().toString());
				//System.out.println("ce operator: " + ce.getOperator().toString());
				String currentOperator = ce.getOperator().toString();
				if (currentOperator == "N") {
					gaps.add(new BEDGap(gapLen, operationLen));
					gapLen += operationLen;
				} else if (currentOperator != "P" && currentOperator != "I") {
					gapLen += operationLen;
				}
			}
		} else {
			gapLen = readLen;
		}
		
	}
	public Read(String readName) {
		super(new SAMFileHeader());
		this.readName = readName;
	}



	public int getGraphicPosition() {
		return graphicPosition;
	}

	public void setGraphicPosition(int graphicPosition) {
		this.graphicPosition = graphicPosition;
	}

	public boolean checkReverseComplement() {
		return this.reverseComplement;
	}

	public String getReadName() {
		return readName;
	}

	public void setReadName(String readName) {
		this.readName = readName;
	}

	public String getRefName() {
		return refName;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public Cigar getCigar() {
		return cigar;
	}
	public ArrayList<BEDGap> getGaps() {
		return gaps;
	}
	
	public void setGapList(ArrayList<BEDGap> gaps) {
		this.gaps = gaps;
	}

	public void setCigar(Cigar cigar) {
		this.cigar = cigar;
	}


	public int compareTo(Read arg0) {
		return this.pos - arg0.pos;
	}

	public int getReadLen() {
		return readLen;
	}

	public void setReadLen(int readLen) {
		this.readLen = readLen;
	}
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
		
	}
	public boolean getFindSelected() {
		return findSelected;
	}
	public void setFindSelected(boolean findSelected) {
		this.findSelected = findSelected;
	}
	

	public int getMapQ() {
		return mapQ;
	}



	public void setMapQ(int mapQ) {
		this.mapQ = mapQ;
	}



	public int getAlignEnd() {
		return alignEnd;
	}



	public void setAlignEnd(int alignEnd) {
		this.alignEnd = alignEnd;
	}



	public void setGaps(ArrayList<BEDGap> gaps) {
		this.gaps = gaps;
	}



	public int onScreenPosition(int XPos, double zoomMulti, Dimension dim) {
		if (pos + readLen < XPos) {
			return -1;
		}
		else if (pos > XPos + zoomMulti * dim.getWidth()) {
			return 1;
		} else {
			return 0;
		}
	}



	public int getUnclippedStart() {
		return unclippedStart;
	}



	public void setUnclippedStart(int unclippedStart) {
		this.unclippedStart = unclippedStart;
	}



	public int getUnclippedEnd() {
		return unclippedEnd;
	}



	public void setUnclippedEnd(int unclippedEnd) {
		this.unclippedEnd = unclippedEnd;
	}
	
}
