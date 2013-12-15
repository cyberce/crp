package org.teiath.service.crp;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewHistoryRouteService {

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public SearchResults<RouteInterest> searchRouteInterestsByCriteria(RouteInterestSearchCriteria criteria)
			throws ServiceException;

	public Collection<RouteAssessment> findDriverRatings(Route route)
			throws ServiceException;

	public Collection<RouteAssessment> findPassengerRatings(Route route)
			throws ServiceException;
}

