package org.teiath.service.crp;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.User;
import org.teiath.data.domain.crp.*;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.service.exceptions.ServiceException;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Service("createRouteService")
@Transactional
public class CreateRouteServiceImpl
		implements CreateRouteService {

	@Autowired
	RouteDAO routeDAO;
	@Autowired
	VehicleDAO vehicleDAO;
	@Autowired
	RouteNotificationCriteriaDAO routeNotificationCriteriaDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;
	@Autowired
	UserPlaceDAO userPlaceDAO;
	@Autowired
	UserActionDAO userActionDAO;

	@Override
	public Collection<Vehicle> getVehiclesByUser(User user)
			throws ServiceException {
		Collection<Vehicle> vehicles;

		try {
			vehicles = vehicleDAO.findByUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return vehicles;
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

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveRoute(Route route)
			throws ServiceException {
		Collection<RouteNotificationCriteria> routeNotificationCriteria;
		try {
			route.setRouteCreationDate(new Date());
			route.setCode(RandomStringUtils.randomAlphanumeric(15).toUpperCase());
			routeDAO.save(route);
			routeNotificationCriteria = routeNotificationCriteriaDAO.checkCriteria(route);
			for (RouteNotificationCriteria matchedCriteria : routeNotificationCriteria) {

				//Create and save Notification
				Notification notification = new Notification();
				notification.setUser(matchedCriteria.getUser());
				notification.setRoute(route);
				notification.setType(Notification.TYPE_ROUTES);
				notification.setSentDate(new Date());
				notification.setTitle("Νεα διαδρομή");
				String body = notificationProperties.getNotificationBody().replace("$1", route.getUser().getFullName())
						.replace("$2", "διαδρομή");
				notification.setBody(body);
				notification.setNotificationCriteria(matchedCriteria);
				notificationDAO.save(notification);

				if (matchedCriteria.getUser().isEmailNotifications()) {
					//Create and send Email
					String mailSubject = emailProperties.getRouteCreateSubject()
							.replace("$1", "[Υπηρεσία εύρεσης Διαδρομών]:").replace("$2", "διαδρομής");
					StringBuilder htmlMessageBuiler = new StringBuilder();
					htmlMessageBuiler.append("<html> <body>");
					String mailBody = emailProperties.getRouteCreateBody().replace("$1", route.getUser().getFullName())
							.replace("$2", "διαδρομή");
					htmlMessageBuiler.append(mailBody);
					htmlMessageBuiler.append("<br>");
					htmlMessageBuiler.append("<br>Ημερομηνία: " + new SimpleDateFormat("dd/MM/yyyy")
							.format(route.getRouteDate()));
					htmlMessageBuiler.append("<br>Ώρα: " + new SimpleDateFormat("HH:mm").format(route.getRouteTime()));
					htmlMessageBuiler.append("<br>Ονοματεπώνυμο οδηγού: " + route.getUser().getFullName());
					htmlMessageBuiler.append("<br>Αφετηρία: " + route.getStartingPoint());
					htmlMessageBuiler.append("<br>Προορισμός: " + route.getDestinationPoint());
					htmlMessageBuiler.append("<br>Όχημα: " + route.getVehicle().getFullName());
					htmlMessageBuiler.append("</body> </html>");
					mailManager.sendMail(emailProperties.getFromAddress(), matchedCriteria.getUser().getEmail(),
							mailSubject, htmlMessageBuiler.toString());
				}
			}

			//create and save user action trace
			UserAction userAction = new UserAction();
			userAction.setDate(new Date());
			userAction.setUser(route.getUser());
			userAction.setStartingPoint(route.getStartingPoint());
			userAction.setDestination(route.getDestinationPoint());
			userAction.setRouteDate(route.getRouteDate());
			userAction.setRouteCode(route.getCode());
			userAction.setVehicle(route.getVehicle().getFullName());
			userAction.setType(UserAction.TYPE_CREATE);
			userActionDAO.save(userAction);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
