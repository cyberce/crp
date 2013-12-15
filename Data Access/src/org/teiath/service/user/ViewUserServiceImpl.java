package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.dao.UserPlaceDAO;
import org.teiath.data.dao.VehicleDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewUserService")
@Transactional
public class ViewUserServiceImpl
		implements ViewUserService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	VehicleDAO vehicleDAO;
	@Autowired
	UserPlaceDAO userPlaceDAO;

	@Override
	public User getUserById(int userId)
			throws ServiceException {
		User user;

		try {
			user = userDAO.findById(userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return user;
	}

	@Override
	public Collection<Vehicle> getVehiclesByUser(User user)
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
	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException {
		Collection<UserPlace> places;

		try {
			places = userPlaceDAO.findByUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return places;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteVehicle(Vehicle vehicle)
			throws ServiceException {
		try {
			vehicleDAO.delete(vehicle);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
