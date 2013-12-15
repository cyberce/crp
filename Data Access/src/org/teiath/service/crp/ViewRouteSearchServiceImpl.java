package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAssessmentDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewRouteSearchService")
@Transactional
public class ViewRouteSearchServiceImpl
		implements ViewRouteSearchService {

	@Autowired
	RouteDAO routeDAO;
	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;

	@Override
	public Route getRouteById(Integer routeId)
			throws ServiceException {
		Route route;

		try {
			route = routeDAO.findById(routeId);
			route.getRouteInterests().iterator();
			route.getRouteSegments().iterator();
			route.getUser().setAverageDriverRating(routeAssessmentDAO.getDriverAverageRating(route.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return route;
	}

	@Override
	public Collection<RouteAssessment> findDriverComments(User user)
			throws ServiceException {
		Collection<RouteAssessment> routeAssessments;
		try {
			routeAssessments = routeAssessmentDAO.getDriverComments(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessments;
	}
}
