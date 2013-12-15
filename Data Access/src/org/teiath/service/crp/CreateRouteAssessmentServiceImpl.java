package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAssessmentDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.service.exceptions.ServiceException;

@Service("createRouteAssessmentService")
@Transactional
public class CreateRouteAssessmentServiceImpl
		implements CreateRouteAssessmentService {

	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;
	@Autowired
	RouteDAO routeDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveAssessment(RouteAssessment routeAssessment)
			throws ServiceException {
		try {
			routeAssessmentDAO.save(routeAssessment);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

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

	@Override
	public RouteAssessment getRouteAssessmentByRouteId(Integer routeId)
			throws ServiceException {
		RouteAssessment routeAssessment;
		try {
			routeAssessment = routeAssessmentDAO.findById(routeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessment;
	}

	@Override
	public RouteAssessment getDriversRating(Route route, User assessor)
			throws ServiceException {
		RouteAssessment routeAssessment;
		try {
			routeAssessment = routeAssessmentDAO.findDriversRating(route, assessor);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return routeAssessment;
	}
}
