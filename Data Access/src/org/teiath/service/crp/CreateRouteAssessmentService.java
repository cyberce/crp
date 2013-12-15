package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.service.exceptions.ServiceException;

public interface CreateRouteAssessmentService {

	public void saveAssessment(RouteAssessment routeAssessment)
			throws ServiceException;

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public RouteAssessment getRouteAssessmentByRouteId(Integer routeId)
			throws ServiceException;

	public RouteAssessment getDriversRating(Route route, User assessor)
			throws ServiceException;
}
