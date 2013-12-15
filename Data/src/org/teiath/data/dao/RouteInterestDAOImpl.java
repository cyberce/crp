package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;

import java.util.Collection;

@Repository("routeInterestDAO")
public class RouteInterestDAOImpl
		implements RouteInterestDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public RouteInterest findById(Integer id) {
		RouteInterest routeInterest;

		Session session = sessionFactory.getCurrentSession();
		routeInterest = (RouteInterest) session.get(RouteInterest.class, id);

		return routeInterest;
	}

	@Override
	public SearchResults<RouteInterest> search(RouteInterestSearchCriteria criteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<RouteInterest> results = new SearchResults<>();
		Criteria hibernateCriteria = session.createCriteria(RouteInterest.class);
		hibernateCriteria.createAlias("route", "rout");

		//Route code
		if ((criteria.getRouteCode() != null) && (! criteria.getRouteCode().equals(""))) {
			hibernateCriteria.add(Restrictions.eq("rout.code", criteria.getRouteCode()));
		}

		//User
		if (criteria.getUser() != null) {
			hibernateCriteria.add(Restrictions.eq("user.id", criteria.getUser().getId()));
		}

		//Driver
		if (criteria.getDriver() != null) {
			hibernateCriteria.add(Restrictions.eq("rout.user", criteria.getDriver()));
		}

		//Route
		if ((criteria.getRoute() != null) && (criteria.getRoute().getId() != 0)) {
			hibernateCriteria.add(Restrictions.eq("route", criteria.getRoute()));
		}

		//Status
		if (criteria.getStatus() != null) {
			hibernateCriteria.add(Restrictions.eq("status", criteria.getStatus()));
		}

		//DateFrom  restriction
		if ((criteria.getDateFrom() != null)) {
			hibernateCriteria.add(Restrictions.ge("rout.routeDate", criteria.getDateFrom()));
		}

		//DateTo restriction
		if ((criteria.getDateTo() != null)) {
			hibernateCriteria.add(Restrictions.le("rout.routeDate", criteria.getDateTo()));
		}

		//Vehicle restriction
		if (criteria.getVehicle() != null) {
			hibernateCriteria.add(Restrictions.eq("rout.vehicle", criteria.getVehicle()));
		}

		//Driver restriction
		if (criteria.getRoute() != null) {
			if ((criteria.getRoute().getUser() != null) && (criteria.getRoute().getUser().getId() != 0) && (criteria
					.getPassenger() == null)) {
				hibernateCriteria.add(Restrictions.eq("rout.user", criteria.getRoute().getUser()));
			}
		}

		//Passenger restriction
		if (criteria.getPassenger() != null) {
			hibernateCriteria.add(Restrictions.eq("user", criteria.getPassenger()));
		}

		//Total records
		results.setTotalRecords(hibernateCriteria.list().size());

		//Paging
		hibernateCriteria.setFirstResult(criteria.getPageNumber() * criteria.getPageSize());
		hibernateCriteria.setMaxResults(criteria.getPageSize());

		//Sorting
		if (criteria.getOrderField() != null) {
			hibernateCriteria.createAlias("user", "usr");
			if (criteria.getOrderDirection().equals("ascending")) {
				hibernateCriteria.addOrder(Order.asc(criteria.getOrderField()));
			} else {
				hibernateCriteria.addOrder(Order.desc(criteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(hibernateCriteria.list());

		return results;
	}

	@Override
	public void save(RouteInterest routeInterest) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(routeInterest);
	}

	@Override
	public void deleteByRoute(Route route) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "delete from RouteInterest I where I.route.id = :routeId";
		Query query = session.createQuery(hql);
		query.setParameter("routeId", route.getId());
		query.executeUpdate();
	}

	@Override
	public void delete(RouteInterest routeInterest) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(routeInterest);
	}

	@Override
	public Collection<RouteInterest> findByRoute(Route route) {
		Session session = sessionFactory.getCurrentSession();
		Collection<RouteInterest> routeInterests;
		Criteria criteria = session.createCriteria(RouteInterest.class);

		criteria.add(Restrictions.eq("route", route));
		criteria.add(Restrictions.eq("status", RouteInterest.STATUS_APPROVED));
		routeInterests = criteria.list();

		return routeInterests;
	}
}
