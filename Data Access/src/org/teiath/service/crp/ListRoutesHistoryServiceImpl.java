package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.dao.VehicleDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listRoutesHistoryService")
@Transactional
public class ListRoutesHistoryServiceImpl
		implements ListRoutesHistoryService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	VehicleDAO vehicleDAO;
	@Autowired
	RouteInterestDAO routeInterestDAO;
	@Autowired
	RouteDAO routeDAO;

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
	public SearchResults<Route> searchRoutesByCriteria(RouteSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Route> results;

		try {
			results = routeDAO.search(criteria, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<Vehicle> getVehiclesByDriver(User user)
			throws ServiceException {
		Collection<Vehicle> vehicles;

		try {
			vehicles = vehicleDAO.findByUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return vehicles;
	}

	@Override
	public Collection<Vehicle> getVehiclesByPassenger(User user)
			throws ServiceException {
		Collection<Vehicle> vehicles;

		try {
			vehicles = vehicleDAO.findByPassenger(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return vehicles;
	}

	@Override
	public Collection<User> getDriversByPassenger(User user)
			throws ServiceException {
		Collection<User> users;

		try {
			users = userDAO.findDriversByPassenger(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return users;
	}
}
