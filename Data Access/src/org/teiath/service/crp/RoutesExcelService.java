package org.teiath.service.crp;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

public interface RoutesExcelService {

	public Collection<Route> getCreatedRoutes(Date dateFrom, Date dateTo)
			throws ServiceException;

	public Collection<Route> getCompletedRoutes(Date dateFrom, Date dateTo)
			throws ServiceException;

	public Collection<RouteInterest> getPassengers(Route route)
			throws ServiceException;
}
