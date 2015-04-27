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
import es.upm.dit.isst.geonote.dao.NoteDAO;
import es.upm.dit.isst.geonote.dao.NoteDAOImpl;
import es.upm.dit.isst.geonote.model.LatLng;
import es.upm.dit.isst.geonote.model.Note;
import es.upm.dit.isst.geonote.model.User;

@SuppressWarnings("serial")
public class GetPublicNotesServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws IOException{
			JSONObject jsonObject = RequestUtils.checkJSONRequest(req, res);
			User user = SessionManager.checkAuthentication(res, jsonObject);
			LatLng point = getPoint(res, jsonObject);
			getPublicNotes(res, user, point);
	}
	
	private void getPublicNotes(HttpServletResponse res, User user, LatLng point)
			throws IOException{
		if(user == null || point == null) return;
		
		NoteDAO noteDAO = NoteDAOImpl.getInstance();
		Note note = noteDAO.getBySender(user.getId());
		note.setSender(user);
		ResponseUtils.JSONObjectResponse(res, note.serializeToJSONObject());
	}
	
	private LatLng getPoint(HttpServletResponse res, JSONObject json) throws IOException{
		if(json == null) return null;
		try{
			JSONObject point = json.getJSONObject("point");
			float lat = (float)point.getDouble("latitude");
			float lon = (float)point.getDouble("longitude");
			return new LatLng(lat, lon);
		}catch(JSONException e){}
		RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
		return null;
	}
}
