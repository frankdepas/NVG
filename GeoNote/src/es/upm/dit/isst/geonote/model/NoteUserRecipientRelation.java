package es.upm.dit.isst.geonote.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class NoteUserRecipientRelation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private Long noteId;
	@Column
	private Long recipientId;
	@Column
	private boolean blocked;
	@Column
	private boolean read;
	
	public NoteUserRecipientRelation(Long noteId, Long recipientId) {
		this.noteId = noteId;
		this.recipientId = recipientId;
		this.blocked = true;
		this.read = false;
	}

	public Long getId() {
		return id;
	}

	public Long getNoteId() {
		return noteId;
	}

	public Long getRecipientId() {
		return recipientId;
	}
	
	public boolean isBlocked(){
		return blocked;
	}
	
	public boolean isRead(){
		return read;
	}
	
}
