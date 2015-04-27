package es.upm.dit.isst.geonote.dao;

import java.util.List;

import es.upm.dit.isst.geonote.model.LatLng;
import es.upm.dit.isst.geonote.model.Note;

public interface NoteDAO {
	
	public void add(Note note);
	public void addPublic(Note note);
	public Note getBySender(Long senderId);
	
	public List<Note> getPublicNotes(LatLng point, float distance);
	
}
