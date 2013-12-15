package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteAssessmentDAO;
import org.teiath.data.dao.RouteDAO;
import org.teiath.data.dao.RouteInterestDAO;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAssessmentSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.zul.ListModelList;

import java.util.Date;

@Service("createPassengersRouteAssessmentService")
@Transactional
public class CreatePassengersRouteAssessmentServiceImpl
		implements CreatePassengersRouteAssessmentService {

	@Autowired
	RouteAssessmentDAO routeAssessmentDAO;
	@Autowired
	RouteDAO routeDAO;
	@Autowired
	RouteInterestDAO routeInterestDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveAssessments(ListModelList<RouteAssessment> assessmentsList)
			throws ServiceException {
		try {
			for (RouteAssessment routeAssessment : assessmentsList) {
				routeAssessment.setAssessmentDate(new Date());
				routeAssessmentDAO.save(routeAssessment);
			}
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
	public SearchResults<RouteAssessment> searchRouteAssessmentsByCriteria(RouteAssessmentSearchCriteria criteria)
			throws ServiceException {
		SearchResults<RouteAssessment> results;

		try {
			results = routeAssessmentDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}
}
