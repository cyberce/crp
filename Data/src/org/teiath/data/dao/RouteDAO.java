package org.teiath.data.dao;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;
import org.teiath.data.search.RouteSearchCriteria;

import java.util.Collection;
import java.util.Date;

public interface RouteDAO {

	public Route findById(Integer id);

	public Route findByCode(String code);

	public SearchResults<Route> search(RouteSearchCriteria criteria, boolean spatialSearch);

	public SearchResults<Route> findCommonRoutes(Route route, RouteInterest routeInterest,
	                                             RouteInterestSearchCriteria routeInterestSearchCriteria);

	public Collection<Route> findCreatedRoutes(Date dateFrom, Date dateTo);

	public Collection<Route> findCompletedRoutes(Date dateFrom, Date dateTo);

	public void save(Route route);

	public void delete(Route route);
}
