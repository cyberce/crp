package org.teiath.service.crp;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface SearchRoutesService {

	public SearchResults<Route> searchRoutes(RouteSearchCriteria criteria, boolean spatialSearch)
			throws ServiceException;
}
