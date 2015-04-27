package es.upm.dit.isst.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.geonote.dao.UserDAO;
import es.upm.dit.isst.geonote.dao.UserDAOImpl;
import es.upm.dit.isst.geonote.model.Session;
import es.upm.dit.isst.geonote.model.User;

public class SessionManager {
	
	private SessionManager(){}
	
	public static User checkAuthentication(HttpServletResponse res, JSONObject json)
		throws IOException{
		if(json == null)
			return null;
		try{
			JSONObject sessionJson = json.getJSONObject("session");
			Session session = new Session(sessionJson);
			User user = isSessionValid(session);
			if(user != null) return user;
			else{
				RequestError.resWithError(res, RequestError.Code.AUTHENTICATION_NOT_VALID);
				return null;
			}
		}catch(JSONException e){
			RequestError.resWithError(res, RequestError.Code.AUTHENTICATION_REQUIRED);
			return null;
		}
	}
	
	private static User isSessionValid(Session session){
		if(hasNullFields(session)) return null;
		// La tupla numero, authid y authtoken coinciden,
		// devolvemos usuario
		UserDAO userDAO = UserDAOImpl.getInstance();
		User user = userDAO.getByNumber(session.getNumber());
		if(user == null) return null;
		if(!checkCredentials(session, user)) return null;
		return user;
	}
	
	private static boolean hasNullFields(Session session){
		return session.getNumber() == null || session.getAuthId() == null
				|| session.getAuthToken() == null;
	}
	
	public static boolean registerUser(User userRegistration){
		if(userRegistration == null) return false;
		UserDAO userDAO = UserDAOImpl.getInstance();
		User userEntity = userDAO.getByNumber(userRegistration.getNumber());
		if(userEntity != null){
			// Esta actualizando el id para enviar push
			// En caso de actualizacion de aplicacion
			// se debe registrar de nuevo en el servidor push
			// y por lo tanto la id cambiara
			if(checkCredentials(userRegistration, userEntity)){
				userRegistration.setId(userEntity.getId());
				userDAO.update(userRegistration);
				return true;
			}else{
				boolean validDigitsSession = checkSessionWithDigitsAPI(userRegistration);
				if(!validDigitsSession){
					return false;
				}
				userRegistration.setId(userEntity.getId());
				userDAO.update(userRegistration);
				return true;
			}
		} else {
			// Checkeamos que la API de Digits nos confirme
			// que la sesion es buena. Si es valida, la
			// registramos la sesion en el usuario
			boolean validDigitsSession = checkSessionWithDigitsAPI(userRegistration);
			if(!validDigitsSession){
				return false;
			}
			userEntity = userDAO.getByNumber(userRegistration.getNumber());
			if(userEntity == null)
				userDAO.add(userRegistration.getNumber(), userRegistration.getAuthId(), 
					userRegistration.getAuthToken(), userRegistration.getGcmId(), 
					userRegistration.getUsername());
			else{
				userRegistration.setId(userEntity.getId());
				userDAO.update(userRegistration);
			}
			return true;
		}
	}
	
	private static boolean checkSessionWithDigitsAPI(User session){
		// Aqui hay que realizar una llamada a la API de Digits
		// para corroborar que la sesion es buena.
		 try {
	 		ConfigurationBuilder confBuilder  = new ConfigurationBuilder();
	 		confBuilder.setOAuthAccessToken(session.getToken()) 
	        .setOAuthAccessTokenSecret(session.getSecret()) 
	        .setOAuthConsumerKey(Config.TWITTER_CONSUMER_KEY) 
	        .setOAuthConsumerSecret(Config.TWITTER_CONSUMER_SECRET);
	        Twitter twitter = new TwitterFactory(confBuilder.build()).getInstance();
	        twitter.verifyCredentials();
	        return true;
	    } catch (TwitterException | IllegalStateException e) {
	    	return false;
	    }
	}
	private static boolean checkCredentials(Session session, User userEntity){
		return checkCredentials(session.getAuthId(), session.getAuthToken(), userEntity);
	}
	
	private static boolean checkCredentials(User userRegistration, User userEntity){
		return checkCredentials(userRegistration.getAuthId(), 
				userRegistration.getAuthToken(), userEntity);
	}
	
	private static boolean checkCredentials(String authId, String authToken, User userEntity){
		return authId.equals(userEntity.getAuthId()) && authToken.equals(userEntity.getAuthToken());
	}
	
}
