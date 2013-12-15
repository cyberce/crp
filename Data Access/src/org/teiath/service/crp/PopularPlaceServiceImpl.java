package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.PopularPlaceDAO;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("popularPlaceService")
@Transactional
public class PopularPlaceServiceImpl
		implements PopularPlaceService {

	@Autowired
	PopularPlaceDAO popularPlaceDAO;

	@Override
	public Collection<PopularPlace> fetchAll()
			throws ServiceException {
		Collection<PopularPlace> results;

		try {
			results = popularPlaceDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}
}
