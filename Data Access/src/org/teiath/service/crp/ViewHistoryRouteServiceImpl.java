package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAssessmentDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewHistoryRouteService")
@Transactional
public class ViewHistoryRouteServiceImpl
		implements ViewHistoryRouteService {

	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	RouteDAO routeDAO;
	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;

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
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<RouteAssessment> findDriverRatings(Route route)
			throws ServiceException {
		Collection<RouteAssessment> routeAssessments;
		try {
			routeAssessments = routeAssessmentDAO.getDriverRatingsByRoute(route);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessments;
	}

	@Override
	public Collection<RouteAssessment> findPassengerRatings(Route route)
			throws ServiceException {
		Collection<RouteAssessment> routeAssessments;
		try {
			routeAssessments = routeAssessmentDAO.getPassengerRatingsByRoute(route);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessments;
	}
}
