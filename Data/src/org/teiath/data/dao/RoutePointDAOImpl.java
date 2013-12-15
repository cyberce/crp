package org.teiath.data.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.crp.RouteSegment;

@Repository("routePointDAO")
public class RoutePointDAOImpl
		implements RoutePointDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void save(RouteSegment routeSegment) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(routeSegment);
	}
}
