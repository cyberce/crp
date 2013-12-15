package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("searchRoutesService")
@Transactional
public class SearchRoutesServiceImpl
		implements SearchRoutesService {

	@Autowired
	RouteDAO routeDAO;

	@Override
	public SearchResults<Route> searchRoutes(RouteSearchCriteria criteria, boolean spatialSearch)
			throws ServiceException {
		SearchResults<Route> results;

		try {
			results = routeDAO.search(criteria, spatialSearch);
			for (Route route : results.getData()) {
				route.getRouteInterests().iterator();
				route.getRouteSegments().iterator();
				route.getVehicle().getUser().getRoles().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}
}
