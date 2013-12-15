package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserPlaceDAO;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.exceptions.ServiceException;

@Service("editUserPlaceService")
@Transactional
public class EditUserPlaceServiceImpl
		implements EditUserPlaceService {

	@Autowired
	UserPlaceDAO userPlaceDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveUserPlace(UserPlace userPlace)
			throws ServiceException {
		try {
			userPlaceDAO.save(userPlace);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public UserPlace getPlaceById(Integer userPlaceId)
			throws ServiceException {
		UserPlace userPlace;
		try {
			userPlace = userPlaceDAO.findById(userPlaceId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return userPlace;
	}
}
