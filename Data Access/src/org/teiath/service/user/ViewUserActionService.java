package org.teiath.service.user;

import org.teiath.data.domain.Notification;
import org.teiath.data.domain.crp.Route;
import org.teiath.data.domain.crp.UserAction;
import org.teiath.service.exceptions.ServiceException;

public interface ViewUserActionService {

	public UserAction getUserActionById(Integer userActionId)
			throws ServiceException;

	public Route getRouteByCode(String code)
			throws ServiceException;
}


