package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListRouteInboxInterestsService {

	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException;

	public Collection<Vehicle> getVehiclesByPassenger(User user)
			throws ServiceException;

	public void rejectRouteInterests(Route route)
			throws ServiceException;

	public void approveRouteInterest(RouteInterest routeInterest)
			throws ServiceException;

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public void rejectRouteInterest(RouteInterest routeInterest)
			throws ServiceException;

	public void createRouteAssessement(RouteAssessment routeAssessment)
			throws ServiceException;

	public Boolean hasEnoughSeats(Route route, RouteInterest routeInterest)
			throws ServiceException;
}
