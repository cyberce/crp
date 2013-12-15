package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAssessmentSearchCriteria;

import java.util.Collection;

@Repository("routeAssessmentDAO")
public class RouteAssessmentDAOImpl
		implements RouteAssessmentDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public RouteAssessment findById(Integer id) {
		RouteAssessment routeAssessment;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedRoute.id", id));
		routeAssessment = (RouteAssessment) criteria.uniqueResult();

		return routeAssessment;
	}

	@Override
	public RouteAssessment findDriversRating(Route route, User assessor) {
		RouteAssessment routeAssessment;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedRoute.id", route.getId()));
		criteria.add(Restrictions.eq("user.id", assessor.getId()));
		criteria.add(Restrictions.eq("assessedUser.id", route.getUser().getId()));
		criteria.add(Restrictions.eq("assessedType", RouteAssessment.RATING_FOR_DRIVER));

		routeAssessment = (RouteAssessment) criteria.uniqueResult();

		return routeAssessment;
	}

	@Override
	public void save(RouteAssessment routeAssessment) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(routeAssessment);
	}

	@Override
	public void delete(RouteAssessment routeAssessment) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(routeAssessment);
	}

	@Override
	public Double getPassengerAverageRating(User user) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", user));
		criteria.add(Restrictions.eq("assessedType", RouteAssessment.RATING_FOR_PASSENGER));
		criteria.setProjection(Property.forName("rating").avg());
		Double avgRating = (Double) criteria.uniqueResult();

		return avgRating;
	}

	@Override
	public Double getDriverAverageRating(User user) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", user));
		criteria.add(Restrictions.eq("assessedType", RouteAssessment.RATING_FOR_DRIVER));
		criteria.setProjection(Property.forName("rating").avg());
		Double avgRating = (Double) criteria.uniqueResult();

		return avgRating;
	}

	@Override
	public Collection<RouteAssessment> getPassengerComments(User user) {
		Collection<RouteAssessment> routeAssessments;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", user));
		criteria.add(Restrictions.ne("comment", ""));
		criteria.add(Restrictions.eq("assessedType", 1));
		routeAssessments = criteria.list();

		return routeAssessments;
	}

	@Override
	public Collection<RouteAssessment> getDriverComments(User user) {
		Collection<RouteAssessment> routeAssessments;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", user));
		criteria.add(Restrictions.ne("comment", ""));
		criteria.add(Restrictions.eq("assessedType", 0));
		routeAssessments = criteria.list();

		return routeAssessments;
	}

	@Override
	public Collection<RouteAssessment> getPassengerRatings(User passenger, User driver) {
		Collection<RouteAssessment> routeAssessments;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", passenger));
		criteria.add(Restrictions.eq("assessedType", 1));
		criteria.add(Restrictions.eq("user", driver));
		routeAssessments = criteria.list();

		return routeAssessments;
	}

	@Override
	public Collection<RouteAssessment> getDriverRatingsByRoute(Route route) {
		Collection<RouteAssessment> routeAssessments;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", route.getUser()));
		criteria.add(Restrictions.eq("assessedRoute", route));
		criteria.add(Restrictions.eq("assessedType", 0));
		routeAssessments = criteria.list();

		return routeAssessments;
	}

	@Override
	public Collection<RouteAssessment> getPassengerRatingsByRoute(Route route) {
		Collection<RouteAssessment> routeAssessments;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RouteAssessment.class);

		criteria.add(Restrictions.eq("user.id", route.getUser().getId()));
		criteria.add(Restrictions.eq("assessedRoute.id", route.getId()));
		criteria.add(Restrictions.eq("assessedType", RouteAssessment.RATING_FOR_PASSENGER));
		routeAssessments = criteria.list();

		return routeAssessments;
	}

	@Override
	public SearchResults<RouteAssessment> search(RouteAssessmentSearchCriteria criteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<RouteAssessment> results = new SearchResults<>();
		Criteria hibernateCriteria = session.createCriteria(RouteAssessment.class);
		hibernateCriteria.createAlias("assessedRoute", "rout");

		//Route
		if ((criteria.getRoute() != null) && (criteria.getRoute().getId() != 0)) {
			hibernateCriteria.add(Restrictions.eq("assessedRoute.id", criteria.getRoute().getId()));
		}

		//Type
		if (criteria.getAssessedType() != null) {
			hibernateCriteria.add(Restrictions.eq("assessedType", criteria.getAssessedType()));
		}

		//Total records
		results.setTotalRecords(hibernateCriteria.list().size());

		//Fetch data
		results.setData(hibernateCriteria.list());

		return results;
	}
}
