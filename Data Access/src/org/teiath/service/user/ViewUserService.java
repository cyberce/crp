package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewUserService {

	public User getUserById(int userId)
			throws ServiceException;

	public Collection<Vehicle> getVehiclesByUser(User user)
			throws ServiceException;

	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException;

	public void deleteVehicle(Vehicle vq)
			throws ServiceException;
}


