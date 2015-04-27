package es.upm.dit.isst.geonote;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.controller.RequestError;
import es.upm.dit.isst.controller.RequestUtils;
import es.upm.dit.isst.controller.ResponseUtils;
import es.upm.dit.isst.controller.SessionManager;
import es.upm.dit.isst.geonote.model.User;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws IOException{
			JSONObject jsonObject = RequestUtils.checkJSONRequest(req, res);
			checkSession(res, jsonObject);		
	}
	
	private void checkSession(HttpServletResponse res, JSONObject jsonObject)
		throws IOException{
		// Si el objeto jsonObject es nulo ya se habría devuelto
		// el mensaje de error pertinente al cliente.
		if(jsonObject == null)
			return;

		try{
			User userRegistration = new User(jsonObject);
			if(hasNullFields(userRegistration)){
				RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
				return;
			}
			boolean result = SessionManager.registerUser(userRegistration);
			ResponseUtils.JSONResultResponse(res, result, (JSONObject)null);
			
		}catch (JSONException e){
			RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
			return;
		}
	}
	
	private boolean hasNullFields(User userRegistration){
		return userRegistration.getNumber() == null || userRegistration.getAuthId() == null
				|| userRegistration.getAuthToken() == null || userRegistration.getGcmId() == null;
	}

}
