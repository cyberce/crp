package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewRouteInterestDriverService {

	public RouteInterest getRouteInterestById(Integer routeInterestId)
			throws ServiceException;

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public SearchResults<Route> findCommonRoutes(Route route, RouteInterest routeInterest,
	                                             RouteInterestSearchCriteria routeInterestSearchCriteria)
			throws ServiceException;

	public Collection<RouteAssessment> findPassengerComments(User user)
			throws ServiceException;

	public Collection<RouteAssessment> findPassengerRatings(User passenger, User driver)
			throws ServiceException;

	public Collection<ExtraPassenger> getRounteInterestPassengers(RouteInterest routeInterest)
			throws ServiceException;
}

