package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.zul.ListModelList;

import java.util.Collection;

public interface EditUserService {

	public void saveUser(User user)
			throws ServiceException;

	public void deleteVehicles(ListModelList<Vehicle> vehiclesToDelete)
			throws ServiceException;

	public void deletePlaces(ListModelList<UserPlace> placesToDelete)
			throws ServiceException;

	public User getUserById(Integer userId)
			throws ServiceException;

	public void deleteVehicle(Vehicle vehicle)
			throws ServiceException, DeleteViolationException;

	public Collection<Vehicle> getVehiclesByUser(User user)
			throws ServiceException;

	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException;

	public Boolean vehicleOnRoute(Vehicle vehicle)
			throws ServiceException;

	public void notifyAdmins(User user)
			throws ServiceException;

	public boolean oldPasswordIsCorrect(String oldPassword)
			throws ServiceException;
}
