package es.upm.dit.isst.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class RequestUtils {
	
	private RequestUtils(){}
	
	public static JSONObject getJSONFromReader(BufferedReader reader) throws IOException, JSONException{
		StringBuffer jb = new StringBuffer();
		String line = null;
	    while ((line = reader.readLine()) != null)
	    	jb.append(line);
	    JSONObject json = new JSONObject(jb.toString());
	    return json;
	}
	
	public static JSONObject checkJSONRequest(HttpServletRequest req, HttpServletResponse res)
		throws IOException {
		
		if(!req.getContentType().equals("application/json")){
			RequestError.resWithError(res, RequestError.Code.NOT_JSON_REQUEST);
			return null;
		}
		
		try{
			JSONObject jStr = RequestUtils.getJSONFromReader(req.getReader());
			return jStr;
		}catch(JSONException e){
			RequestError.resWithError(res, RequestError.Code.NOT_JSON_REQUEST);
			return null;
		}
		
	}
}
