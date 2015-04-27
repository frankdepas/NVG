package es.upm.dit.isst.geonote.model;

public class NoteState {

	private boolean blocked;
	private boolean read;
	
	public NoteState(boolean blocked, boolean read){
		this.blocked = blocked;
		this.read = read;
	}
	
	public boolean isBlocked(){
		return blocked;
	}
	
	public boolean isRead(){
		return read;
	}
}
