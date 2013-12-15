package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAbuseReportSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ListRouteAbuseReportsService {

	public SearchResults<RouteAbuseReport> searchRouteAbuseReportsByCriteria(RouteAbuseReportSearchCriteria criteria)
			throws ServiceException;

	public void toggleBan(User user)
			throws ServiceException;
}
