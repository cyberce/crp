package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.Vehicle;

import java.util.Collection;

@Repository("vehicleDAO")
public class VehicleDAOImpl
		implements VehicleDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<Vehicle> findByUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Vehicle> vehicles;
		Criteria criteria = session.createCriteria(Vehicle.class);

		criteria.add(Restrictions.eq("user", user));
		vehicles = criteria.list();

		return vehicles;
	}

	@Override
	public Collection<Vehicle> findByPassenger(User user) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Vehicle> vehicles;
		String hql = "select distinct vehicle " +
				"from Vehicle as vehicle, RouteInterest as routeInterest, Route as route " +
				"where routeInterest.route.id = route.id " +
				"and routeInterest.user.id =:passengerId " +
				"and route.vehicle.id = vehicle.id";

		Query query = session.createQuery(hql);
		query.setParameter("passengerId", user.getId());

		vehicles = query.list();

		return vehicles;
	}

	@Override
	public void save(Vehicle vehicle) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(vehicle);
	}

	@Override
	public void delete(Vehicle vehicle) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(vehicle);
	}

	@Override
	public Vehicle findById(Integer id) {
		Vehicle vehicle;

		Session session = sessionFactory.getCurrentSession();
		vehicle = (Vehicle) session.get(Vehicle.class, id);

		return vehicle;
	}

	@Override
	public Boolean vehicleIsCurrentlyUsed(Vehicle vehicle) {
		Session session = sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(Route.class);
		crit.setProjection(Projections.rowCount());
		crit.add(Restrictions.eq("vehicle", vehicle));

		Long count = (Long) crit.uniqueResult();

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Vehicle findByPlate(String plate) {
		Session session = sessionFactory.getCurrentSession();
		Vehicle vehicle;

		String hql = "from Vehicle vehicle where vehicle.plateNumber = :plate";
		Query query = session.createQuery(hql).setParameter("plate", plate);

		vehicle = (Vehicle) query.uniqueResult();

		return vehicle;
	}

}
