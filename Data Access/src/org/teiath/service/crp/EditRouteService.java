package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface EditRouteService {

	public void saveRoute(Route route)
			throws ServiceException;

	public Collection<Vehicle> getVehiclesByUser(User user)
			throws ServiceException;

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException;

	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException;

	public Route getRouteByCode(String code)
			throws ServiceException;

}
