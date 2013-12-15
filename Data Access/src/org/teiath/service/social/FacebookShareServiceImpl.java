package org.teiath.service.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.domain.crp.Route;
import org.teiath.service.exceptions.ServiceException;

@Service("facebookShareService")
@Transactional
public class FacebookShareServiceImpl
		implements FacebookShareService {

	@Autowired
	RouteDAO routeDAO;

	@Override
	public Route getRouteById(Integer routeId)
			throws ServiceException {
		Route route;
		try {
			route = routeDAO.findById(routeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}
}
