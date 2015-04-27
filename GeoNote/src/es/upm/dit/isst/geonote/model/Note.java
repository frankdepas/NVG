package es.upm.dit.isst.geonote.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.geonote.utils.JSONSerializable;

@Entity
public class Note extends JSONSerializable{

	@Serialize
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Serialize
	@Column
	private Date date;

	@Serialize
	@Deserialize
	@Column
	private String body;

	@Serialize
	@Deserialize
	@Column
	private float latitude;

	@Serialize
	@Deserialize
	@Column
	private float longitude;
	
	@Serialize
	@Deserialize
	@Column
	private float radius;
	
	@Column
	private Long senderId;
	
	@Column
	private boolean public_note;
	
	@Serialize
	@Transient
	private User sender;
	
	@Deserialize
	@Transient
	private List<String> recipientsNumbers;
	
	@Serialize
	@Transient
	private boolean blocked;
	
	@Serialize
	@Transient
	private boolean read;
	
	@Transient
	private Map<User, NoteState> recipients;
	
	@Serialize
	@Transient
	private NoteState state;
	
	
	public Note(JSONObject json) throws JSONException{
		deserializeJSONObject(json);
	}	
	
	public Note(Date date, String body, float latitude, 
			float longitude, float radius, boolean public_note,
			User sender, Map<User, NoteState> recipients){
		this.date = date;
		this.body = body;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.public_note = public_note;
		this.sender = sender;
		this.recipients = recipients;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	
	public Long getId(){
		return id;
	}
	
	public Date getDate(){
		return date;
	}
	
	public void setDate(Date date){
		this.date = date;
	}
	
	public String getBody(){
		return body;
	}
	
	public float getLatitude(){
		return latitude;
	}
	
	public float getLongitude(){
		return longitude;
	}
	
	public float getRadius(){
		return radius;
	}
	
	public boolean isPublic(){
		return public_note;
	}
	
	public void setSender(User sender){
		this.senderId = sender.getId();
		this.sender = sender;
	}
	
	public Long getSenderId(){
		return senderId;
	}
	public User getSender(){
		return sender;
	}

	public List<String> getRecipientsNumbers(){
		return recipientsNumbers;
	}

	public void setRecipients(Map<User, NoteState> recipients){
		this.recipients = recipients;
	}
	
	public Map<User, NoteState> getRecipients(){
		return recipients;
	}
	
	public void setState(NoteState state){
		this.blocked = state.isBlocked();
		this.read = state.isRead();
	}
	
	public void setRead(boolean read){
		this.read = read;
	}
	
	public void setPublic(boolean public_note){
		this.public_note = public_note;
	}
	
	public boolean isBlocked(){
		return blocked;
	}
	
	public boolean isRead(){
		return read;
	}
}
