package fer.zavrsni.bioinformatics;

public class BEDGap implements Comparable<BEDGap>{
	
	private int pos;
	private int len;
	
	
	public BEDGap(int pos, int len) {
		this.pos = pos;
		this.len = len;
	}
	
	public int compareTo(BEDGap arg0) {
		return this.pos - arg0.pos;
	}


	public int getPos() {
		return pos;
	}


	public void setPos(int pos) {
		this.pos = pos;
	}


	public int getLen() {
		return len;
	}


	public void setLen(int len) {
		this.len = len;
	}

}
