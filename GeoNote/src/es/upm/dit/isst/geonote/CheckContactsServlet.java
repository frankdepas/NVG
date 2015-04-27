package es.upm.dit.isst.geonote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.controller.RequestError;
import es.upm.dit.isst.controller.RequestUtils;
import es.upm.dit.isst.controller.ResponseUtils;
import es.upm.dit.isst.controller.SessionManager;
import es.upm.dit.isst.geonote.dao.UserDAO;
import es.upm.dit.isst.geonote.dao.UserDAOImpl;
import es.upm.dit.isst.geonote.model.User;

@SuppressWarnings("serial")
public class CheckContactsServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws IOException{
		JSONObject jsonObject = RequestUtils.checkJSONRequest(req, res);
		User user = SessionManager.checkAuthentication(res, jsonObject);
		checkContacts(res, jsonObject, user);		
	}
	
	private void checkContacts(HttpServletResponse res, JSONObject jsonObject, User user)
		throws IOException{
		// Si el objeto jsonObject es nulo ya se habría devuelto
		// el mensaje de error pertinente al cliente.
		if(user == null) return;
		// Si user != null, jsonObject no puede ser null
		try{
			JSONArray jsonArray = jsonObject.getJSONArray("contacts");
			List<String> contacts = new ArrayList<>();
			for(int i = 0; i < jsonArray.length(); i++)
				contacts.add(jsonArray.getString(i));
			UserDAO userDAO = UserDAOImpl.getInstance();
			contacts = userDAO.checkContacts(contacts);
			jsonArray = new JSONArray();
			if(contacts != null)
				for(String contact : contacts)
					jsonArray.put(contact);
			JSONObject data = new JSONObject();
			data.put("contacts", jsonArray);
			ResponseUtils.JSONResultResponse(res, true, data);
		}catch (JSONException e){
			RequestError.resWithError(res, RequestError.Code.MALFORMED_JSON_REQUEST);
			return;
		}
	}

}
