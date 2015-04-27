package es.upm.dit.isst.geonote;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.upm.dit.isst.controller.ResponseUtils;
import es.upm.dit.isst.geonote.model.Note;
import es.upm.dit.isst.geonote.model.NoteState;
import es.upm.dit.isst.geonote.model.User;

@SuppressWarnings("serial")
public class TestPushServlet extends HttpServlet {
	

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws IOException{
		/*Date date, String body, float latitude, 
		float longitude, float radius, boolean blocked, boolean read,
		User sender, List<User> recipients*/
		User sender = new User("+34633181990", "3067804521", "token=3067804521-9AAHv59XBefX0kslVwryWpp6dHsfxHN2q4qGZFx,secret=NTU45DJ6TScTnPS1ggFwTFpg2GF5yWmkOTtuwYa7Bogus",
				"", "pepos");
		User recipient = new User("+34644131688", "3125658625", 
				"token=3125658625-Ju0GX4ETqfnky1r4E8kTdyVK0cXrYYWX9TFQ82q,secret=SDyB4tztm7bcct7RzMX0C7xp5bnVlMwt5zTrhzaGWTpBz",
				"APA91bHgkOcJBqrGEMkFHJ5YBeEc2JE6wo5Fij-czibYMb5G3Td13qBfb5Ya0H6MTvGnKlDaqs6iOJCU2-ofqmDyh_Rk3KAadraQNIkrFuPjLY3uYYwrX7-CJbWPyopVgAsmf0sOmIz7WxKgDIUKr1nTes4gkf8csQ",
				"mama");
		Map<User, NoteState> recipients = new HashMap<>();
		//List<User> recipients = new ArrayList<>();
		recipients.put(recipient, new NoteState(true, false));
		recipients.put(sender, new NoteState(false, false));
		Note note = new Note(new Date(), "El cuerpo es este UPDATED", 
				2.0f, 1.0f, 0.5f, false, sender,
				recipients);
		note.setId(123124123L);
		Set<User> userRecipients = note.getRecipients().keySet();
		Iterator<User> i = userRecipients.iterator();
		while(i.hasNext()){
			User user = i.next();
			System.out.println("La nota enviada a " + user.getNumber());
			NoteState state = note.getRecipients().get(user);
			note.setState(state);
			System.out.println(note.serializeToJSONObject().toString());
		}
		ResponseUtils.sendPushNote(note);
	}

}
