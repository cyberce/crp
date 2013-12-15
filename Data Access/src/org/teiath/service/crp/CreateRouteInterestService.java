package org.teiath.service.crp;

import org.teiath.data.domain.crp.ExtraPassenger;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.RouteInterest;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.zul.ListModelList;

public interface CreateRouteInterestService {

	public Route getRouteById(Integer routeId)
			throws ServiceException;

	public void saveRouteInterest(RouteInterest routeInterest, ListModelList<ExtraPassenger> extraPassengers)
			throws ServiceException;
}
