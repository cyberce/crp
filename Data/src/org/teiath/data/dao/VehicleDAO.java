package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Vehicle;

import java.util.Collection;

public interface VehicleDAO {

	public Collection<Vehicle> findByUser(User user);

	public Collection<Vehicle> findByPassenger(User user);

	public void save(Vehicle vehicle);

	public void delete(Vehicle vehicle);

	public Vehicle findById(Integer id);

	public Boolean vehicleIsCurrentlyUsed(Vehicle vehicle);

	public Vehicle findByPlate(String plate);

}
