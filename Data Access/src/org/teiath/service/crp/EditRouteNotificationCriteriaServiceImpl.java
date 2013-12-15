package org.teiath.service.crp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.RouteNotificationCriteriaDAO;
import org.teiath.data.dao.UserPlaceDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.RouteNotificationCriteria;
import org.teiath.data.domain.crp.UserPlace;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("editRouteNotificationCriteriaService")
@Transactional
public class EditRouteNotificationCriteriaServiceImpl
		implements EditRouteNotificationCriteriaService {

	@Autowired
	RouteNotificationCriteriaDAO routeNotificationCriteriaDAO;
	@Autowired
	UserPlaceDAO userPlaceDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveRouteNotificationCriteria(RouteNotificationCriteria routeNotificationCriteria)
			throws ServiceException {
		try {
			routeNotificationCriteriaDAO.save(routeNotificationCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public RouteNotificationCriteria getRouteNotificationCriteriaById(Integer notificationId)
			throws ServiceException {
		RouteNotificationCriteria routeNotificationCriteria;
		try {
			routeNotificationCriteria = routeNotificationCriteriaDAO.findById(notificationId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return routeNotificationCriteria;
	}

	@Override
	public Collection<UserPlace> getPlacesByUser(User user)
			throws ServiceException {
		Collection<UserPlace> places;

		try {
			places = userPlaceDAO.findByUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return places;
	}
}
