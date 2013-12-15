package org.teiath.service.values;

import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.exceptions.ServiceException;

public interface EditUserPlaceService {

	public void saveUserPlace(UserPlace place)
			throws ServiceException;

	public UserPlace getPlaceById(Integer userPlaceId)
			throws ServiceException;
}
