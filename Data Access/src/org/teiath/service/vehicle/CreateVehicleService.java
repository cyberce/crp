package org.teiath.service.vehicle;

import org.teiath.data.domain.crp.Vehicle;
import org.teiath.service.exceptions.ServiceException;

public interface CreateVehicleService {

	public void saveVehicle(Vehicle vehicle)
			throws ServiceException;
}
