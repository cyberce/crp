package org.teiath.service.values;

import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.service.exceptions.ServiceException;

public interface CreatePopularPlaceService {

	public void savePopularPlace(PopularPlace popularPlace)
			throws ServiceException;
}
