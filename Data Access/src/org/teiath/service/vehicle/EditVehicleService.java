package org.teiath.service.vehicle;

import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;

public interface EditVehicleService {

	public void saveVehicle(Vehicle vehicle)
			throws ServiceException;

	public Vehicle getVehicleById(Integer vehicleId)
			throws ServiceException;

	public Vehicle getVehicleByPlate(String vehiclePlate) throws ServiceException;

}
