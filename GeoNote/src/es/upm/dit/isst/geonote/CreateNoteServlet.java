package es.upm.dit.isst.geonote;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import es.upm.dit.isst.geonote.dao.UserDAO;
import es.upm.dit.isst.geonote.dao.UserDAOImpl;
import es.upm.dit.isst.geonote.model.Note;
import es.upm.dit.isst.geonote.model.NoteState;
import es.upm.dit.isst.geonote.model.User;

@SuppressWarnings("serial")
public class CreateNoteServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws IOException{
		JSONObject jsonObject = RequestUtils.checkJSONRequest(req, res);
		User user = SessionManager.checkAuthentication(res, jsonObject);
		createNote(res, user, jsonObject);	
	}
	
	private void createNote(HttpServletResponse res, User user, JSONObject jsonObject)
		throws IOException{
		
		if(user == null || jsonObject == null)
			return;
		
		try{
			JSONObject noteJSON = jsonObject.getJSONObject("note");
			Note note = new Note(noteJSON);
			if(note.getRecipientsNumbers() == null){
				RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
				return;
			}else if(note.getRecipientsNumbers().size() == 0){
				// Si no hay recipientes ninguno, la nota es publica.
				note.setPublic(true);
				note.setDate(new Date());
				note.setSender(user);
				if(checkPublicNote(note)){
					NoteDAO noteDAO = NoteDAOImpl.getInstance();
					noteDAO.addPublic(note);
					ResponseUtils.JSONResultResponse(res, true, (JSONObject)null);
				}else{
					RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
				}
				return;
			}
			note.setDate(new Date());
			note.setSender(user);
			note.setPublic(false);
			UserDAO userDAO = UserDAOImpl.getInstance();
			List<User> userRecipients = userDAO.getByNumber(note.getRecipientsNumbers());
			Map<User, NoteState> recipients = new HashMap<>();
			for(User recipient : userRecipients)
				recipients.put(recipient, new NoteState(true, false));
		
			note.setRecipients(recipients);
			if(checkNote(note)){
				NoteDAO noteDAO = NoteDAOImpl.getInstance();
				noteDAO.add(note);
				ResponseUtils.JSONResultResponse(res, true, (JSONObject)null);
				ResponseUtils.sendPushNote(note);
			}else{
				RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
			}
		}catch(JSONException e){
			RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
			return;
		}
	}
	
	private boolean checkPublicNote(Note note){
		boolean result = note.getDate() != null && note.getBody() != null
				&& note.getSender() != null;
		result &= note.getLatitude() >= -90 && note.getLatitude() <= 90;
		result &= note.getLongitude() >= -180 && note.getLongitude() <= 180;
		return result;
	}
	private boolean checkNote(Note note){
		boolean result = note.getDate() != null && note.getBody() != null
				&& note.getSender() != null && note.getRecipients() != null;
		result &= note.getRecipients().size() > 0;
		result &= note.getLatitude() >= -90 && note.getLatitude() <= 90;
		result &= note.getLongitude() >= -180 && note.getLongitude() <= 180;
		return result;
	}

}
