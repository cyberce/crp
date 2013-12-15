package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteNotificationCriteria;

import java.util.Collection;

@Repository("routeNotificationCriteriaDAO")
public class RouteNotificationCriteriaDAOImpl
		implements RouteNotificationCriteriaDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void save(RouteNotificationCriteria routeNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(routeNotificationCriteria);
	}

	@Override
	public void delete(RouteNotificationCriteria routeNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(routeNotificationCriteria);
	}

	@Override
	public Collection<RouteNotificationCriteria> checkCriteria(Route route) {
		Session session = sessionFactory.getCurrentSession();
		Collection<RouteNotificationCriteria> routeNotificationCriteria;
		Criteria criteria = session.createCriteria(RouteNotificationCriteria.class);

		//Date restriction
		criteria.add(Restrictions.or(Restrictions.le("dateFrom", route.getRouteDate()), Restrictions.le("dateTo", route.getRouteEndDate()) ));
		criteria.add(Restrictions.ge("dateTo", route.getRouteDate()));

		//Objects allowed restriction
		criteria.add(Restrictions.eq("objectTransportAllowed", route.isObjectTransportAllowed()));

		//Smoking restriction
		criteria.add(Restrictions.eq("smokingAllowed", route.isSmokingAllowed()));

		//Contribution restricion
		criteria.add(Restrictions.ge("maxAmount", route.getContributionAmount()));

		//Tag restricion
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("tags"), Restrictions.isNotNull("id")),
				Restrictions.and(Restrictions.ne("tags", ""),
						Restrictions.ilike("tags", route.getTags(), MatchMode.ANYWHERE))));

		routeNotificationCriteria = criteria.list();

		return routeNotificationCriteria;
	}

	@Override
	public RouteNotificationCriteria findById(Integer id) {
		RouteNotificationCriteria routeNotificationCriteria;

		Session session = sessionFactory.getCurrentSession();
		routeNotificationCriteria = (RouteNotificationCriteria) session.get(RouteNotificationCriteria.class, id);

		return routeNotificationCriteria;
	}
}
