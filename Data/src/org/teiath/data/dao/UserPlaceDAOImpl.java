package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.UserPlace;

import java.util.Collection;

@Repository("userPlaceDAO")
public class UserPlaceDAOImpl
		implements UserPlaceDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<UserPlace> findByUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		Collection<UserPlace> places;
		Criteria criteria = session.createCriteria(UserPlace.class);

		criteria.add(Restrictions.eq("user", user));
		places = criteria.list();

		return places;
	}

	@Override
	public void save(UserPlace userPlace) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(userPlace);
	}

	@Override
	public void delete(UserPlace userPlace) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(userPlace);
	}

	@Override
	public UserPlace findById(Integer id) {
		UserPlace place;

		Session session = sessionFactory.getCurrentSession();
		place = (UserPlace) session.get(UserPlace.class, id);

		return place;
	}
}
