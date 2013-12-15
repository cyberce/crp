package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListRoutesService {

	public SearchResults<Route> searchRoutesByCriteria(RouteSearchCriteria criteria)
			throws ServiceException;

	public Collection<Vehicle> getVehiclesByUser(User user)
			throws ServiceException;

	public void deleteRoute(Route route)
			throws ServiceException;

	public void deleteRouteInterests(Route route)
			throws ServiceException;

	public void sendDeleteNotifications(Route route)
			throws ServiceException;

	public void createUserAction(Route route)
			throws ServiceException;
}
