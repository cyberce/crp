package org.teiath.data.dao;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteInterestSearchCriteria;

import java.util.Collection;

public interface RouteInterestDAO {

	public RouteInterest findById(Integer id);

	public SearchResults<RouteInterest> search(RouteInterestSearchCriteria criteria);

	public void save(RouteInterest routeInterest);

	public void deleteByRoute(Route route);

	public void delete(RouteInterest routeInterest);

	public Collection<RouteInterest> findByRoute(Route route);
}
