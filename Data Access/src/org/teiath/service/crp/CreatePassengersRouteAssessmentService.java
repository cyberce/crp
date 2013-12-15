package org.teiath.service.crp;

import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteAssessment;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.RouteAssessmentSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.zul.ListModelList;

public interface CreatePassengersRouteAssessmentService {

	public void saveAssessments(ListModelList<RouteAssessment> assessmentsList)
			throws ServiceException;

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public RouteAssessment getRouteAssessmentByRouteId(Integer routeId)
			throws ServiceException;

	public SearchResults<RouteAssessment> searchRouteAssessmentsByCriteria(RouteAssessmentSearchCriteria criteria)
			throws ServiceException;
}
