package org.teiath.data.dao;

import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.RouteInterest;

import java.util.Collection;

public interface ExtraPassengerDAO {

	public Collection<ExtraPassenger> findByRouteInterest(RouteInterest routeInterest);


	public void save(ExtraPassenger extraPassenger);


//	public void deleteAll(Listing listing);
}
