package org.teiath.service.crp;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ViewRouteService {

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public Route getRouteByCode(String code)
			throws ServiceException;

	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException;
}


