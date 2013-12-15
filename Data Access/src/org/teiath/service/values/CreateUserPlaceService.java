package org.teiath.service.values;

import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.exceptions.ServiceException;

public interface CreateUserPlaceService {

	public void saveUserPlace(UserPlace place)
			throws ServiceException;
}
