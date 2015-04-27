package es.upm.dit.isst.geonote.dao;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.upm.dit.isst.geonote.model.User;

public class UserDAOImpl implements UserDAO {
	
	private static UserDAOImpl instance;
	
	private UserDAOImpl(){}
	
	public static UserDAOImpl getInstance(){
		if(instance == null)
			instance = new UserDAOImpl();
		return instance;
	}

	@Override
	public void add(String number, String authId, String authToken,
			String gcmId, String username) {
		EntityManager em = EMF.get().createEntityManager();
		User user = new User(number, authId, authToken, gcmId, username);
		em.persist(user);
		em.close();	
	}

	@Override
	public User getByNumber(String number) {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select u from User u" +
		" where u.number = :number");
		q.setParameter("number", number);
		try{
			User user = (User) q.getResultList().get(0);
			return user;
		}catch(Exception e){
			return null;
		}finally{
			em.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getByNumber(List<String> numbers) {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select u from User u where u.number in (" 
				+getListString(numbers.iterator())+ ")");
		try{
			return q.getResultList();
		}catch(Exception e){
			return null;
		}finally{
			em.close();
		}
	}
	


	@SuppressWarnings("unchecked")
	@Override
	public List<User> getById(List<Long> ids) {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select u from User u where u.id in (" 
				+getListLong(ids.iterator())+ ")");
		try{
			return q.getResultList();
		}catch(Exception e){
			return null;
		}finally{
			em.close();
		}
	}
	
	@Override
	public void update(User user){
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select u from User u where u.id = :id");
		q.setParameter("id", user.getId());
		try{
			User u = (User)q.getResultList().get(0);
			em.getTransaction().begin();
			u.setAuthId(user.getAuthId());
			u.setAuthToken(user.getAuthToken());
			u.setGcmId(user.getGcmId());
			u.setUsername(user.getUsername());
			em.getTransaction().commit();
		}catch(Exception e){
		}finally{
			em.close();
		}
	}
	
	@Override
	public void updateByNumber(User user){
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select u from User u where u.number = :number");
		q.setParameter("number", user.getNumber());
		try{
			User u = (User)q.getResultList().get(0);
			em.getTransaction().begin();
			u.setAuthId(user.getAuthId());
			u.setAuthToken(user.getAuthToken());
			u.setGcmId(user.getGcmId());
			u.setUsername(user.getUsername());
			em.getTransaction().commit();
		}catch(Exception e){
		}finally{
			em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> checkContacts(List<String> contacts) {
		if(contacts == null || contacts.size() == 0) return null;
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select u.number from User u where u.number in (" 
				+getListString(contacts.iterator())+ ")");
		try{
			return q.getResultList();
		}catch(Exception e){}
		finally{
			em.close();
		}
		return null;
	}
	
	private String getListLong(Iterator<Long> i){
		StringBuilder result = new StringBuilder();
		boolean first = true;
		while(i.hasNext()){
			Long id = i.next();
			if(first) first = false;
			else result.append(",");
			result.append(id);
		}
		return result.toString();
	}
	
	private String getListString(Iterator<String> i){
		StringBuilder result = new StringBuilder();
		boolean first = true;
		while(i.hasNext()){
			String str = i.next();
			if(first) first = false;
			else result.append(",");
			result.append("'");
			result.append(str);
			result.append("'");
		}
		return result.toString();
	}

	@Override
	public User getById(Long id) {
		EntityManager em = EMF.get().createEntityManager();
		Query q = em.createQuery("select u from User u where u.id = :userId");
		q.setParameter("userId", id);
		try{
			if(q.getResultList() != null && q.getResultList().size() > 0)
				return (User)q.getResultList().get(0);
			else return null;
		}catch(Exception e){
			return null;
		}finally{
			em.close();
		}
	}

}
