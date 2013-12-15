package org.teiath.data.dao;

import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAbuseReportSearchCriteria;

public interface RouteAbuseReportDAO {

	public RouteAbuseReport findById(Integer id);

	public SearchResults<RouteAbuseReport> search(RouteAbuseReportSearchCriteria criteria);

	public void save(RouteAbuseReport routeAbuseReport);
}
