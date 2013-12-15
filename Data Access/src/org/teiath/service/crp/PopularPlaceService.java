package org.teiath.service.crp;

import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface PopularPlaceService {

	public Collection<PopularPlace> fetchAll()
			throws ServiceException;
}
