package org.teiath.service.user;

import org.hibernate.exception.ConstraintViolationException;
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
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.util.PasswordService;
import org.zkoss.zul.ListModelList;

import java.util.Collection;

@Service("editUserService")
@Transactional
public class EditUserServiceImpl
		implements EditUserService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	VehicleDAO vehicleDAO;
	@Autowired
	UserPlaceDAO userPlaceDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveUser(User user)
			throws ServiceException {
		try {
			userDAO.save(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void deleteVehicles(ListModelList<Vehicle> vehiclesToDelete)
			throws ServiceException {
		try {
			for (Vehicle vehicle : vehiclesToDelete) {
				vehicleDAO.delete(vehicle);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void deletePlaces(ListModelList<UserPlace> placesToDelete)
			throws ServiceException {
		try {
			for (UserPlace place : placesToDelete) {
				userPlaceDAO.delete(place);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public User getUserById(Integer userId)
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteVehicle(Vehicle vehicle)
			throws ServiceException, DeleteViolationException {
		try {
			vehicleDAO.delete(vehicle);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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
	public Boolean vehicleOnRoute(Vehicle vehicle)
			throws ServiceException {
		return vehicleDAO.vehicleIsCurrentlyUsed(vehicle);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void notifyAdmins(User user)
			throws ServiceException {
		try {
			Collection<User> admins;

			admins = userDAO.findAdmins();

			for (User admin : admins) {
				//Create and send Email
				String mailSubject = emailProperties.getDriverApplicationSubject();
				String mailBody = emailProperties.getDriverApplicationBody().replace("$1", user.getFullName());
				mailManager.sendMail(emailProperties.getFromAddress(), admin.getEmail(), mailSubject, mailBody);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public boolean oldPasswordIsCorrect(String oldPassword)
			throws ServiceException {
		try {
			User user = userDAO.checkPassword(PasswordService.encrypt(oldPassword));
			if (user != null)
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
