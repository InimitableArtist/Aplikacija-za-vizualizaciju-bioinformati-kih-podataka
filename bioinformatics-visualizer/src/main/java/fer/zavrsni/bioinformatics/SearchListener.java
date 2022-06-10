package fer.zavrsni.bioinformatics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchListener implements ActionListener{
	
	private int current;
	private int matchSize;
	private Object[] dialogObjects;
	
	public SearchListener(int matchSize, Object[] dialogObjects) {
		super();
		current = 1;
		this.matchSize = matchSize;
		this.dialogObjects = dialogObjects;
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getMatchSize() {
		return matchSize;
	}

	public void setMatchSize(int matchSize) {
		this.matchSize = matchSize;
	}

	public Object[] getDialogObjects() {
		return dialogObjects;
	}

	public void setDialogObjects(Object[] dialogObjects) {
		this.dialogObjects = dialogObjects;
	}
	public void inc() {
		current++;
	}
	
	public String getMessage() {
		return dialogObjects[0].toString();
	}
	public void setMessage(String msg) {
		dialogObjects[0] = msg;
	}
	
}
