package es.upm.dit.isst.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import es.upm.dit.isst.geonote.model.Note;
import es.upm.dit.isst.geonote.model.NoteState;
import es.upm.dit.isst.geonote.model.User;

public class ResponseUtils {
	
	private ResponseUtils(){};
	
	public static void JSONObjectResponse(HttpServletResponse res, JSONObject json)
		throws IOException{
		res.setContentType("application/json");
		PrintWriter out = res.getWriter();
		out.print(json != null ? json.toString() : (new JSONArray()).toString());
		out.flush();
	}

	public static void JSONArrayResponse(HttpServletResponse res, JSONArray array)
		throws IOException{
		res.setContentType("application/json");
		PrintWriter out = res.getWriter();
		out.print(array != null ? array.toString() : (new JSONArray()).toString());
		out.flush();
	}
	
	public static void JSONResultResponse(HttpServletResponse res, 
			boolean result, JSONObject data) throws IOException, JSONException{
		JSONObject json = new JSONObject();
		json.put("result", result);
		if(data != null)
			json.put("data", data);
		else json.put("data", new JSONObject());
		JSONObjectResponse(res, json);
	}
	
	public static MulticastResult sendPushMessageToDevice(String device, String payload){
		List<String> list = new ArrayList<>();
		list.add(device);
		return sendPushMessageToDevices(list, payload);
	}
	
    public static MulticastResult sendPushMessageToDevices(List<String> devices, String payload) {
        Sender sender = new Sender(Config.PUSH_API_KEY);
        Message gcmMessage = new Message.Builder()
        		.addData("payload", payload).build();
        MulticastResult result = null;
        try {
            result = sender.send(gcmMessage, devices, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private static Result sendNote(String gcmId, Note note){
    	Sender sender = new Sender(Config.PUSH_API_KEY);
    	Message gcmMessage = new Message.Builder()
			.addData("note", note.serializeToJSONObject().toString()).build();
    	Result result = null;
    	try{
    		result = sender.send(gcmMessage, gcmId, 5);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return result;
    }
    
    public static List<Result> sendPushNote(Note note){
    	Map<User, NoteState> recipients = note.getRecipients();
    	Set<User> userRecipients = note.getRecipients().keySet();
    	List<Result> multicastResult = new ArrayList<>();
    	for(User user : userRecipients){
    		NoteState state = recipients.get(user);
    		note.setState(state);
    		multicastResult.add(sendNote(user.getGcmId(), note));
    	}
    	return multicastResult;
    }
}
