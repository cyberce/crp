package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ExtraPassengerDAO;
import org.teiath.data.dao.RouteAssessmentDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewRouteInterestDriverService")
@Transactional
public class ViewRouteInterestDriverServiceImpl
		implements ViewRouteInterestDriverService {

	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	ExtraPassengerDAO extraPassengerDAO;
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
			routeInterest.getUser()
					.setAveragePassengerRating(routeAssessmentDAO.getPassengerAverageRating(routeInterest.getUser()));
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
	public SearchResults<Route> findCommonRoutes(Route route, RouteInterest routeInterest,
	                                             RouteInterestSearchCriteria routeInterestSearchCriteria)
			throws ServiceException {
		SearchResults<Route> results;

		try {
			results = routeDAO.findCommonRoutes(route, routeInterest, routeInterestSearchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<RouteAssessment> findPassengerComments(User user)
			throws ServiceException {
		Collection<RouteAssessment> routeAssessments;
		try {
			routeAssessments = routeAssessmentDAO.getPassengerComments(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessments;
	}

	@Override
	public Collection<RouteAssessment> findPassengerRatings(User passenger, User driver)
			throws ServiceException {
		Collection<RouteAssessment> routeAssessments;
		try {
			routeAssessments = routeAssessmentDAO.getPassengerRatings(passenger, driver);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessments;
	}

	@Override
	public Collection<ExtraPassenger> getRounteInterestPassengers(RouteInterest routeInterest)
			throws ServiceException {
		Collection<ExtraPassenger> extraPassengers;

		try {
			extraPassengers = extraPassengerDAO.findByRouteInterest(routeInterest);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return extraPassengers;
	}
}
