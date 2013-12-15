package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("viewRouteService")
@Transactional
public class ViewRouteServiceImpl
		implements ViewRouteService {

	@Autowired
	RouteDAO routeDAO;
	@Autowired
	RouteInterestDAO routeInterestDAO;

	@Override
	public Route getRouteById(Integer routeId)
			throws ServiceException {
		Route route;

		try {
			route = routeDAO.findById(routeId);
			route.getRouteInterests().iterator();
			route.getRouteSegments().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}

	@Override
	public Route getRouteByCode(String code)
			throws ServiceException {
		Route route;

		try {
			route = routeDAO.findByCode(code);
			if (route != null) {
				route.getRouteInterests().iterator();
				route.getRouteSegments().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}

	@Override
	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException {
		SearchResults<RouteInterest> results;

		try {
			results = routeInterestDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}
}
