package es.upm.dit.isst.geonote.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.upm.dit.isst.geonote.model.LatLng;
import es.upm.dit.isst.geonote.model.Note;
import es.upm.dit.isst.geonote.model.NoteState;
import es.upm.dit.isst.geonote.model.NoteUserRecipientRelation;
import es.upm.dit.isst.geonote.model.User;

public class NoteDAOImpl implements NoteDAO {
	
	private static NoteDAOImpl instance;
	
	private NoteDAOImpl(){}
	
	public static NoteDAOImpl getInstance(){
		if(instance == null)
			instance = new NoteDAOImpl();
		return instance;
	}


	@Override
	public void add(Note note) {
		EntityManager em = EMF.get().createEntityManager();
		em.getTransaction().begin();
		em.persist(note);
		em.flush();
		registerRecipients(em, note);
		em.getTransaction().commit();
		em.close();
	}
	
	@Override
	public void addPublic(Note note){
		EntityManager em = EMF.get().createEntityManager();
		em.getTransaction().begin();
		em.persist(note);
		em.flush();
		em.getTransaction().commit();
		em.close();		
	}

	private void registerRecipients(EntityManager em, Note note){
		Set<User> users = note.getRecipients().keySet();
		Iterator<User> i = users.iterator();
		while(i.hasNext()){
			NoteUserRecipientRelation relation = 
					new NoteUserRecipientRelation(note.getId(), i.next().getId());
			em.persist(relation);
		}
	}

	@Override
	public Note getBySender(Long senderId) {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select n from Note n" +
		" where n.senderId = :senderId");
		q.setParameter("senderId", senderId);
		try{
			Note note = (Note) q.getResultList().get(0);
			loadRecipients(em, note);
			return note;
		}catch(Exception e){
			return null;
		}finally{
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadRecipients(EntityManager em, Note note){
		Query q = em.createQuery("select nu from NoteUserRecipientRelation nu" +
				" where nu.noteId = :noteId");
		q.setParameter("noteId", note.getId());
		try{
			List<NoteUserRecipientRelation> recipientRelations = q.getResultList();
			List<Long> recipientsId = getRecipientsIds(recipientRelations);
			UserDAO userDAO = UserDAOImpl.getInstance();
			List<User> userRecipients = userDAO.getById(recipientsId);
			Map<User, NoteState> recipients = new HashMap<User, NoteState>(); 
			for(User user : userRecipients){
				NoteUserRecipientRelation relation = 
						getByRecipientIdAndDelete(user.getId(), recipientRelations);
				if(relation != null){
					recipients.put(user, new NoteState(relation.isBlocked(), relation.isRead()));
				}
			}
			note.setRecipients(recipients);
		}catch(Exception e){	
		}
	}
	
	private NoteUserRecipientRelation getByRecipientIdAndDelete(long id, List<NoteUserRecipientRelation> list){
		for(NoteUserRecipientRelation item : list)
			if(item.getRecipientId() == id){
				list.remove(item);
				return item;
			}
		return null;
	}
	
	private List<Long> getRecipientsIds(List<NoteUserRecipientRelation> relations){
		List<Long> result = new ArrayList<>();
		for(NoteUserRecipientRelation relation : relations){
			result.add(relation.getRecipientId());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Note> getPublicNotes(LatLng point, float distance_meters) {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select n from Note n where n.public_note = true");
		try{
			List<Note> notes = q.getResultList();
			float distance_miles = 0.000621371192f * distance_meters;
			Iterator<Note> i = notes.iterator();
			while(i.hasNext()){
				Note note = i.next();
				if(point.distanceTo(note.getLatitude(), note.getLongitude()) > distance_miles)
					notes.remove(note);
			}
			UserDAO userDAO = UserDAOImpl.getInstance();
			for(Note note : notes){
				note.setSender(userDAO.getById(note.getSenderId()));
			}
			return notes;
		}catch(Exception e){
			return null;
		}finally{
			em.close();
		}
	}
}
