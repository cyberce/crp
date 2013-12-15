package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

@Service("routesExcelService")
@Transactional
public class RoutesExcelServiceImpl
		implements RoutesExcelService {

	@Autowired
	RouteDAO routeDAO;
	@Autowired
	RouteInterestDAO routeInterestDAO;

	@Override
	public Collection<Route> getCreatedRoutes(Date dateFrom, Date dateTo)
			throws ServiceException {
		Collection<Route> routes;

		try {
			routes = routeDAO.findCreatedRoutes(dateFrom, dateTo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routes;
	}

	@Override
	public Collection<Route> getCompletedRoutes(Date dateFrom, Date dateTo)
			throws ServiceException {
		Collection<Route> routes;

		try {
			routes = routeDAO.findCompletedRoutes(dateFrom, dateTo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routes;
	}

	@Override
	public Collection<RouteInterest> getPassengers(Route route)
			throws ServiceException {
		Collection<RouteInterest> routeInterests;

		try {
			routeInterests = routeInterestDAO.findByRoute(route);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeInterests;
	}
}
