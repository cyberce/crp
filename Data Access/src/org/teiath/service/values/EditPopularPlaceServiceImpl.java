package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.PopularPlaceDAO;
import org.teiath.data.domain.crp.PopularPlace;
import org.teiath.service.exceptions.ServiceException;

@Service("editPopularPlaceService")
@Transactional
public class EditPopularPlaceServiceImpl
		implements EditPopularPlaceService {

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

	@Override
	public PopularPlace getPopularPlaceById(Integer popularPlaceId)
			throws ServiceException {
		PopularPlace productStatus;
		try {
			productStatus = popularPlaceDAO.findById(popularPlaceId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return productStatus;
	}
}
