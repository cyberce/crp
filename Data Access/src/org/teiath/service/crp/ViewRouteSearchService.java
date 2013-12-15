package org.teiath.service.crp;

import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewRouteSearchService {

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public Collection<RouteAssessment> findDriverComments(User user)
			throws ServiceException;
}


