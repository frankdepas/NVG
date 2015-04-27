package es.upm.dit.isst.geonote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.geonote.utils.JSONSerializable;

@Entity
public class User extends JSONSerializable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Deserialize
	@Column
	private String username;
	
	@Serialize
	@Deserialize
	@Column
	private String number;

	@Deserialize
	@Column
	private String authId;

	@Deserialize
	@Column
	private String authToken;

	@Deserialize
	@Column
	private String gcmId;
	
	public User(JSONObject json) throws JSONException{
		deserializeJSONObject(json);
	}
	
	public User(String number, String authId, String authToken,
			String gcmId, String username){
		this.number = number;
		this.authId = authId;
		this.authToken = authToken;
		this.gcmId = gcmId;
		this.username = username;
	}
	
	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getNumber(){
		return number;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getAuthId(){
		return authId;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
	public String getAuthToken(){
		return authToken;
	}

	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}
	
	public String getGcmId(){
		return gcmId;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getToken(){
		return strBetween(authToken, "token=", ",");
	}
	
	public String getSecret(){
		return strBetween(authToken, "secret=", null);
	}
	
	private String strBetween(String str, String before, String after){
		if(str==null || before == null) return null;
		int index = str.indexOf(before);
		if(index == -1 || index+before.length() >= str.length()) return null;
		String result = str.substring(index+before.length());
		if(after == null) return result;
		index = result.indexOf(after);
		if(index == -1) return null;
		return result.substring(0, index);
	}
}
