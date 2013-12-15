package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAbuseReportSearchCriteria;

@Repository("routeAbuseReportDAO")
public class RouteAbuseReportDAOImpl
		implements RouteAbuseReportDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public RouteAbuseReport findById(Integer id) {
		RouteAbuseReport routeAbuseReport;

		Session session = sessionFactory.getCurrentSession();
		routeAbuseReport = (RouteAbuseReport) session.get(RouteAbuseReport.class, id);

		return routeAbuseReport;
	}

	@Override
	public void save(RouteAbuseReport routeAbuseReport) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(routeAbuseReport);
	}

	@Override
	public SearchResults<RouteAbuseReport> search(RouteAbuseReportSearchCriteria criteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<RouteAbuseReport> results = new SearchResults<>();
		Criteria hibernateCriteria = session.createCriteria(RouteAbuseReport.class);

		//Date restriction
		if ((criteria.getDateFrom() != null) && (criteria.getDateTo() != null)) {
			hibernateCriteria.add(Restrictions.ge("reportDate", criteria.getDateFrom()));
			hibernateCriteria.add(Restrictions.le("reportDate", criteria.getDateTo()));
		}

		//Total records
		results.setTotalRecords(hibernateCriteria.list().size());

		//Paging
		hibernateCriteria.setFirstResult(criteria.getPageNumber() * criteria.getPageSize());
		hibernateCriteria.setMaxResults(criteria.getPageSize());

		//Sorting
		if (criteria.getOrderField() != null) {
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
}
