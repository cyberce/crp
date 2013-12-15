package org.teiath.service.ntf;

import org.teiath.data.domain.crp.RouteNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ViewNotificationCriteriaService {

	public RouteNotificationCriteria getNotificationCriteriaById(Integer notificationCriteriaId)
			throws ServiceException;
}


