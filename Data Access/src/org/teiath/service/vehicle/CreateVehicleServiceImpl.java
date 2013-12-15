package org.teiath.service.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.VehicleDAO;
import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;

@Service("createVehicleService")
@Transactional
public class CreateVehicleServiceImpl
		implements CreateVehicleService {

	@Autowired
	VehicleDAO vehicleDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveVehicle(Vehicle vehicle)
			throws ServiceException {
		try {
			vehicleDAO.save(vehicle);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
