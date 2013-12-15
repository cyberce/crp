package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAssessmentDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewRouteInterestPassengerService")
@Transactional
public class ViewRouteInterestPassengerServiceImpl
		implements ViewRouteInterestPassengerService {

	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	RouteDAO routeDAO;
	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;

	@Override
	public RouteInterest getRouteInterestById(Integer routeInterestId)
			throws ServiceException {
		RouteInterest routeInterest;
		try {
			routeInterest = routeInterestDAO.findById(routeInterestId);
			routeInterest.getRoute().getUser().setAverageDriverRating(
					routeAssessmentDAO.getDriverAverageRating(routeInterest.getRoute().getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeInterest;
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<RouteAssessment> findDriverComments(User user)
			throws ServiceException {
		Collection<RouteAssessment> routeAssessments;
		try {
			routeAssessments = routeAssessmentDAO.getDriverComments(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessments;
	}
}
