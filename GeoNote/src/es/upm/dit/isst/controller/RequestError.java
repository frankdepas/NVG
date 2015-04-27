package es.upm.dit.isst.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class RequestError {
	
	public enum Code {NOT_JSON_REQUEST, MALFORMED_JSON_REQUEST, AUTHENTICATION_REQUIRED,
		AUTHENTICATION_NOT_VALID};

	private RequestError(){};
	
	private static Map<Code, String> errors;
	
	static{
		errors = new HashMap<Code, String>();
		errors.put(Code.NOT_JSON_REQUEST, "Post data is not a valid JSONObject");
		errors.put(Code.MALFORMED_JSON_REQUEST, "Required fields are not present in JSONObject request");
		errors.put(Code.AUTHENTICATION_REQUIRED, "The request need object \"auth\" at root of the JSONObject request");
		errors.put(Code.AUTHENTICATION_NOT_VALID, "The auth object doesn't contain expected values: "
				+"\"number\", \"authId\" and \"authToken\"");
	}
	
	private static JSONObject getBadRequestError(Code error){
		try{
			JSONObject json = new JSONObject();
			json.put("error", error);
			json.put("message", errors.get(error));
			return json;
		}catch(JSONException e){
			return new JSONObject();
		}
	}
	
	public static void resWithError(HttpServletResponse res, Code error)
		throws IOException{
		ResponseUtils.JSONObjectResponse(res, getBadRequestError(error));
	}

}
