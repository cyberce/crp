package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.PopularPlaceDAO;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.service.exceptions.ServiceException;

@Service("createPopularPlaceService")
@Transactional
public class CreatePopularPlaceServiceImpl
		implements CreatePopularPlaceService {

	@Autowired
	PopularPlaceDAO popularPlaceDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void savePopularPlace(PopularPlace popularPlace)
			throws ServiceException {
		try {
			popularPlaceDAO.save(popularPlace);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
