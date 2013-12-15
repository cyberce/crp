package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteAbuseReport;
import org.teiath.service.exceptions.ServiceException;

public interface ViewRouteAbuseReportService {

	public RouteAbuseReport getRouteAbuseReportById(Integer routeAbuseReportId)
			throws ServiceException;

	public void banUser(User user)
			throws ServiceException;

	public void restoreUser(User user)
			throws ServiceException;
}


