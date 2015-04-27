package es.upm.dit.isst.geonote.dao;

import java.util.List;

import es.upm.dit.isst.geonote.model.User;

public interface UserDAO {

	public void add(String number, String authId, String authToken, 
			String gcmId, String username);
	public User getByNumber(String number);
	public List<User> getByNumber(List<String> numbers);
	public List<User> getById(List<Long> ids);
	public User getById(Long id);
	public void update(User session);
	public void updateByNumber(User session);
	
	public List<String> checkContacts(List<String> contacts);
	
}
