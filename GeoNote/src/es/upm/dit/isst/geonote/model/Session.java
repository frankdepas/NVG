package es.upm.dit.isst.geonote.model;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.geonote.utils.JSONSerializable;

public class Session extends JSONSerializable {
	
	@JSONSerializable.Deserialize
	private String number;
	@JSONSerializable.Deserialize
	private String authId;
	@JSONSerializable.Deserialize
	private String authToken;
	
	public Session (JSONObject session) throws JSONException{
		deserializeJSONObject(session);
	}

	public Session(String number, String authId, String authToken) {
		this.number = number;
		this.authId = authId;
		this.authToken = authToken;
	}

	public String getNumber() {
		return number;
	}

	public String getAuthId() {
		return authId;
	}

	public String getAuthToken() {
		return authToken;
	}
	
	
}
