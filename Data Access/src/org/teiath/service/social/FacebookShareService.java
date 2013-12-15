package org.teiath.service.social;

import org.teiath.data.domain.crp.Route;
import org.teiath.service.exceptions.ServiceException;

public interface FacebookShareService {

	public Route getRouteById(Integer routeId)
			throws ServiceException;
}
