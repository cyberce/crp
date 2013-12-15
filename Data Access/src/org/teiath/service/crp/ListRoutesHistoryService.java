package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListRoutesHistoryService {

	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException;

	public SearchResults<Route> searchRoutesByCriteria(RouteSearchCriteria criteria)
			throws ServiceException;

	public Collection<Vehicle> getVehiclesByDriver(User user)
			throws ServiceException;

	public Collection<Vehicle> getVehiclesByPassenger(User user)
			throws ServiceException;

	public Collection<User> getDriversByPassenger(User user)
			throws ServiceException;
}
