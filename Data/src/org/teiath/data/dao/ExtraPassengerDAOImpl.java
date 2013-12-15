package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

import java.util.Collection;

@Repository("extraPassengerDAO")
public class ExtraPassengerDAOImpl
		implements ExtraPassengerDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<ExtraPassenger> findByRouteInterest(RouteInterest routeInterest) {
		Session session = sessionFactory.getCurrentSession();
		Collection<ExtraPassenger> extraPassengers;
		Criteria criteria = session.createCriteria(ExtraPassenger.class);

		criteria.add(Restrictions.eq("routeInterest.id", routeInterest.getId()));
		extraPassengers = criteria.list();

		return extraPassengers;
	}

	@Override
	public void save(ExtraPassenger extraPassenger) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(extraPassenger);
	}
}
