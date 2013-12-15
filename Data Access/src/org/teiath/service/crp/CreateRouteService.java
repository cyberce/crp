package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface CreateRouteService {

	public Collection<Vehicle> getVehiclesByUser(User user)
			throws ServiceException;

	public void saveRoute(Route route)
			throws ServiceException;

	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException;
}
