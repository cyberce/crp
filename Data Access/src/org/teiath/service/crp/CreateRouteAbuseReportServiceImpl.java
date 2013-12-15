package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAbuseReportDAO;
import org.teiath.data.dao.RouteAssessmentDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("createRouteAbuseReportService")
@Transactional
public class CreateRouteAbuseReportServiceImpl
		implements CreateRouteAbuseReportService {

	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;
	@Autowired
	RouteAbuseReportDAO routeAbuseReportDAO;
	@Autowired
	RouteDAO routeDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveReport(RouteAbuseReport routeAbuseReport)
			throws ServiceException {
		try {
			routeAbuseReportDAO.save(routeAbuseReport);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public Route getRouteById(Integer routeId)
			throws ServiceException {
		Route route;
		try {
			route = routeDAO.findById(routeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}

	@Override
	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException {
		SearchResults<RouteInterest> results;

		try {
			results = routeInterestDAO.search(criteria);
			for (RouteInterest routeInterest : results.getData()) {
				routeInterest.getUser().setAveragePassengerRating(
						routeAssessmentDAO.getPassengerAverageRating(routeInterest.getUser()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}
}
