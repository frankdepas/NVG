package es.upm.dit.isst.geonote;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.controller.RequestError;
import es.upm.dit.isst.controller.RequestError.Code;
import es.upm.dit.isst.controller.RequestUtils;
import es.upm.dit.isst.controller.ResponseUtils;
import es.upm.dit.isst.controller.SessionManager;
import es.upm.dit.isst.geonote.dao.NoteDAO;
import es.upm.dit.isst.geonote.dao.NoteDAOImpl;
import es.upm.dit.isst.geonote.model.LatLng;
import es.upm.dit.isst.geonote.model.Note;
import es.upm.dit.isst.geonote.model.User;

@SuppressWarnings("serial")
public class GetNotesServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws IOException{
			JSONObject jsonObject = RequestUtils.checkJSONRequest(req, res);
			User user = SessionManager.checkAuthentication(res, jsonObject);
			LatLng point = LatLng.getPoint(jsonObject);
			if(point != null) getPublicNotes(res, user, point);
			else getNotes(res, user);
	}
	
	private void getPublicNotes(HttpServletResponse res, User user, LatLng point) throws IOException{
		if(user == null) return;
		
		NoteDAO noteDAO = NoteDAOImpl.getInstance();
		List<Note> notes = noteDAO.getPublicNotes(point, 1000f);
		JSONArray array = new JSONArray();
		for(Note note : notes)
			array.put(note.serializeToJSONObject());
		try {
			JSONObject data = new JSONObject();
			data.put("notes", array);
			ResponseUtils.JSONResultResponse(res, true, data);
		} catch (JSONException e) {
			RequestError.resWithError(res, Code.MALFORMED_JSON_REQUEST);
		}
	}
	
	private void getNotes(HttpServletResponse res, User user) throws IOException{
		
		// Si el usuario es nulo, ya habremos devuelto el error
		// pertinente al cliente
		if(user == null) return;
		
		NoteDAO noteDAO = NoteDAOImpl.getInstance();
		Note note = noteDAO.getBySender(user.getId());
		note.setSender(user);
		ResponseUtils.JSONObjectResponse(res, note.serializeToJSONObject());
	}

}
