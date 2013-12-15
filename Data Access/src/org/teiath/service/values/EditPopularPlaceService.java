package org.teiath.service.values;

import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.service.exceptions.ServiceException;

public interface EditPopularPlaceService {

	public void savePopularPlace(PopularPlace popularPlace)
			throws ServiceException;

	public PopularPlace getPopularPlaceById(Integer popularPlaceId)
			throws ServiceException;
}
