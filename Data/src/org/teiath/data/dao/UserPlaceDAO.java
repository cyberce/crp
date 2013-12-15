package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.UserPlace;

import java.util.Collection;

public interface UserPlaceDAO {

	public Collection<UserPlace> findByUser(User user);

	public void save(UserPlace userPlace);

	public void delete(UserPlace userPlace);

	public UserPlace findById(Integer id);
}
